# Redis Cache Version Strategy

## Overview

Permission Service uses a **version-based cache invalidation strategy** that leverages version numbers embedded in Redis key names for efficient cache management across distributed systems.

## Version Mechanism

### Master Version Key
```
Key: microservice:permission:version:{accountId}
Value: v{timestamp}
Example: microservice:permission:version:user-123 → v1710000000000
```

- **One per account** - unique version for each user
- **No TTL** - managed by application logic
- **Atomic updates** - single Redis operation to invalidate all related caches

### Why Version in Key Names?

Traditional cache invalidation has problems:

```
❌ Problem 1: Pattern Deletion
   EVAL "return redis.call('del', unpack(redis.call('keys', ARGV[1])))" 0 "permission:*"
   - Scans ALL keys matching pattern (expensive O(n) operation)
   - At scale (millions of keys), this becomes a bottleneck
   - Can block Redis if too many keys
   - Not suitable for frequent invalidations

❌ Problem 2: Manual Key Tracking  
   - Need to track which keys to delete
   - Error-prone during complex updates
   - Requires multiple round-trips to Redis
   - Hard to ensure consistency

✅ Solution: Version in Key Names
   - Version changed → new key name used
   - Old keys with old version naturally not accessed
   - No scanning required
   - TTL handles automatic cleanup
   - Atomic single-operation update
```

## Cache Key Structure

### All Cache Keys Include Version

```
microservice:permission:menutree:{accountId}:{version}
                                              ├─ v1710000000000
                                              ├─ v1710000100000 (after update)
                                              └─ etc.

microservice:permission:roles:{accountId}:{version}

microservice:permission:check:{accountId}:{version}:{path}:{method}
```

### Master Control Key (No Version)

```
microservice:permission:version:{accountId}  ← Single key per account
                        └─ Current version value
```

## How It Works

### Scenario 1: Initial Cache Access

```
User requests: GET /permissions/me/menus

Step 1: Get current version
   Redis GET "microservice:permission:version:user-123"
   → Returns: v1710000000000

Step 2: Build cache key with version
   key = "microservice:permission:menutree:user-123:v1710000000000"

Step 3: Check cache
   Redis GET key
   → Cache MISS (first time)
   → Fetch from database
   → Wrap in CacheValue<T>
   → Store: SET key {data, expireAt: 2026-03-25 10:32:00} EX 1800

Step 4: Return to user
   Menu tree data
```

### Scenario 2: Cache Hit (Normal)

```
Same user makes another request within 30 minutes

Step 1: Get version (same value)
   → v1710000000000

Step 2: Build key
   → "microservice:permission:menutree:user-123:v1710000000000"

Step 3: Cache GET
   → Cache HIT ✅
   → Return cached data instantly
   → ~1-5ms response time
```

### Scenario 3: Invalidation (Version Change)

```
Admin updates a menu that affects this user

Step 1: Detect version needs update
   User has version: v1710000000000
   New version: v1710000100000

Step 2: Update master version key
   Redis SET "microservice:permission:version:user-123" "v1710000100000"
   → Single atomic operation ⚡

Step 3: User makes next request

Step 4: Get version (changed!)
   → v1710000100000 (new value)

Step 5: Build cache key with NEW version
   → "microservice:permission:menutree:user-123:v1710000100000"

Step 6: Cache GET
   → Cache MISS (old key with v1710000000000 not accessed)
   → Fetch from database (now sees menu update)
   → Store with new version: v1710000100000
   → Return fresh data ✅

Step 7: Old cache key cleanup
   After 30 minutes:
   → "microservice:permission:menutree:user-123:v1710000000000" TTL expires
   → Key auto-deleted by Redis
```

## CacheValue Wrapper

All cached data is wrapped for advanced features:

```json
{
  "data": { /* actual cached data */ },
  "expireAt": 1710000300000  // Absolute timestamp in milliseconds
}
```

### Benefits

1. **Type Information** - Generics for safe deserialization
2. **Explicit Expiry** - Know exact expiration time
3. **Proactive Refresh** - Can trigger refresh 3 mins before expiry
4. **Graceful Degradation** - Can use stale data if refresh fails

### Proactive Refresh Example

```java
// In CacheServiceImpl.get():
long now = System.currentTimeMillis();
long refreshThreshold = 3 * 60 * 1000;  // 3 minutes

if (cache.getExpireAt() - now <= refreshThreshold) {
    // Data expires in < 3 minutes
    // Trigger async refresh in background
    cacheWarmupService.refreshAsync(accId);
    
    // Return current data immediately (don't wait)
    return Optional.of((T) cache.getData());
}
// Data still fresh, just return it
return Optional.of((T) cache.getData());
```

## Distributed Lock

Prevents "cache stampede" when multiple requests hit cache miss simultaneously:

```
Key: microservice:permission:refresh:lock:{accountId}
TTL: Very short (milliseconds)
Value: "1"

When cache misses:
  Thread 1: Tries to acquire lock ✅ Gets lock
  Thread 2: Tries to acquire lock ❌ Fails, waits
  Thread 3: Tries to acquire lock ❌ Fails, waits
  
  Thread 1: Refreshes cache from DB (slow)
  Thread 1: Releases lock
  
  Thread 2: Acquires lock, cache now exists, reads it
  Thread 3: Acquires lock, cache now exists, reads it
```

## Performance Metrics

### Cache Hit Ratio
- **Target**: > 70%
- **Calculation**: `hits / (hits + misses) × 100`
- **Healthy**: Users get cached data most of the time

### Response Times (3-Tier Cache)

| Layer | Hit Time | Status |
|-------|----------|--------|
| Caffeine (L1) | ~1ms | ✅ Fastest |
| Redis (L2) | ~5-10ms | ✅ Fast |
| Database (L3) | ~50-100ms | Acceptable |

### Version Update Impact
- Version update: **< 1ms** (single Redis SET)
- No scanning required
- No pattern matching
- No key cleanup latency

## Code Implementation

### CacheKeyFactory (Constants)

```java
public class CacheKeyFactory {
    public static final String PREFIX = "microservice:permission:";
    
    public static String version(String accId) {
        return PREFIX + "version:" + accId;
    }
    
    public static String menuTree(String accId, String version) {
        return String.format(PREFIX + "menutree:%s:%s", accId, version);
    }
    
    public static String permissionCheck(String accId, String version, 
                                         String path, String method) {
        return String.format(PREFIX + "check:%s:%s:%s:%s", 
                           accId, version, path, method);
    }
}
```

### Cache Service

```java
@Service
public class MenuCacheService {
    private final CacheService cacheService;
    
    public Optional<List<MenuTreeResponse>> get(String accId) {
        // Get current version
        String version = cacheService.getOrInitVersion(accId);
        
        // Build key with version
        String key = CacheKeyFactory.menuTree(accId, version);
        
        // Get from Redis
        return cacheService.get(key, accId, List.class);
    }
    
    public void put(String accId, String version, 
                   List<MenuTreeResponse> tree) {
        // Store with version in key
        cacheService.put(
            CacheKeyFactory.menuTree(accId, version), 
            tree, 
            Duration.ofMinutes(30)
        );
    }
}
```

### Invalidation

```java
// When role assigned to account
roleService.assignRole(accId, roleId);

// Update version to invalidate all caches for this account
String newVersion = "v" + System.currentTimeMillis();
cacheService.setVersion(accId, newVersion);
// Only 1 Redis operation! 🚀
```

## Migration to Version-Based Cache

If migrating from old cache strategy:

1. **Add version key generation**
   ```java
   String version = cacheService.getOrInitVersion(accId);
   ```

2. **Include version in cache keys**
   ```java
   // Old: "permission:menus:" + accId
   // New: "microservice:permission:menutree:" + accId + ":" + version
   ```

3. **Update invalidation logic**
   ```java
   // Old: delete all keys matching pattern
   // New: update single version key
   cacheService.setVersion(accId, newVersion);
   ```

4. **Test cache behavior**
   - Verify cache hits after updates
   - Monitor Redis memory (old keys cleaning up)
   - Check performance (should be same or better)

## Troubleshooting

### Cache Not Invalidating
Check if version key is being updated:
```bash
redis-cli get "microservice:permission:version:user-123"
# Should show timestamp like: v1710000000000
```

### Memory Growing (Old Keys Not Cleaning)
Check TTL on old version keys:
```bash
redis-cli keys "microservice:permission:*:v1709999900000"
# Should be empty after 30 mins, or check TTL
redis-cli ttl "microservice:permission:menutree:user-123:v1709999900000"
```

### Users Getting Stale Data
Increase cache TTL or decrease refresh threshold:
```yaml
# In CacheServiceImpl
REFRESH_BEFORE_MS = Duration.ofMinutes(3).toMillis()  // Refresh 3 mins before expiry
```

## References

- Implementation: `src/main/java/com/example/demo/common/constant/cache/`
- Cache Services: `src/main/java/com/example/demo/core/application/service/cache/`
- Redis Config: `src/main/java/com/example/demo/infrastructure/config/cache/redis/`

