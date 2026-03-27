# Kafka & Redis Integration Guide

This document provides detailed information about how Kafka and Redis are used in the Permission Service.

## Table of Contents
1. [Kafka Overview](#kafka-overview)
2. [Redis Overview](#redis-overview)
3. [Architecture Diagrams](#architecture-diagrams)
4. [Monitoring & Debugging](#monitoring--debugging)

---

## Kafka Overview

### What is Kafka?

Apache Kafka is a distributed event streaming platform that enables:
- **Real-time data pipelines** between services
- **Event sourcing** and audit trails
- **Loose coupling** between microservices
- **Asynchronous processing** and eventual consistency

### Kafka Architecture in Permission Service

```
┌──────────────────────────────────────────────────────────────────┐
│                    Kafka Cluster (Brokers)                       │
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ Broker 1: Partition 0, Partition 1, Partition 2         │   │
│  │ Broker 2: Partition 0, Partition 1, Partition 2         │   │
│  │ Broker 3: Partition 0, Partition 1, Partition 2         │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
                              ↑
                    ┌─────────┼──────────┐
                    │         │          │
        ┌───────────▼──┐ ┌────▼───────┐ └────┬──────────┐
        │ Permission   │ │  Auth      │      │  Other   │
        │  Service     │ │  Service   │      │ Services │
        │ (Producer)   │ │(Consumer)  │      │(Consumers)
        └──────────────┘ └────────────┘      └──────────┘
```

### Kafka Topics Used

#### Published Topics (Permission Service → Others)

```
Topic: permission.changed
├─ Partitions: 3 (for scalability)
├─ Replication Factor: 2
├─ Retention: 7 days
├─ Format: JSON
└─ Consumers: Other services needing to know about permission changes

Topic: permission.role.assigned
├─ Partitions: 3
├─ Replication Factor: 2
├─ Retention: 30 days (audit trail)
├─ Format: JSON
└─ Consumers: Auth Service, User Service, Audit Service

Topic: permission.role.revoked
├─ Partitions: 3
├─ Replication Factor: 2
├─ Retention: 30 days (audit trail)
├─ Format: JSON
└─ Consumers: Auth Service, User Service, Audit Service
```

#### Subscribed Topics (Others → Permission Service)

```
Topic: account.created
├─ Source: Auth Service
├─ Action: Initialize default roles for new account
└─ Handler: AccountEventListener

Topic: account.deleted
├─ Source: Auth Service
├─ Action: Remove all roles/permissions for deleted account
└─ Handler: AccountEventListener
```

### Message Flow Diagram

```
┌────────────────────────────────────────────────────────────────┐
│                    API Request                                 │
│            POST /accounts/user-123/roles                       │
│            { "roleIds": [1, 2] }                               │
└────────┬───────────────────────────────────────────────────────┘
         │
         ▼
┌────────────────────────────────────────────────────────────────┐
│              RoleController.assignRoles()                      │
└────────┬───────────────────────────────────────────────────────┘
         │
         ▼
┌────────────────────────────────────────────────────────────────┐
│               RoleService.assignRole()                         │
│  - Validate role exists                                        │
│  - Check account exists                                        │
│  - Save assignment to DB                                       │
└────────┬───────────────────────────────────────────────────────┘
         │
         ▼
┌────────────────────────────────────────────────────────────────┐
│         RoleAssignedEventPublisher.publish()                   │
│  - Create RoleAssignedEvent                                    │
│  - Serialize to JSON                                           │
└────────┬───────────────────────────────────────────────────────┘
         │
         ▼
┌────────────────────────────────────────────────────────────────┐
│          KafkaTemplate.send("permission.role.assigned")        │
│  - Send to Kafka broker                                        │
│  - Wait for ack from replicas (acks=all)                       │
│  - Retry up to 3 times on failure                              │
└────────┬───────────────────────────────────────────────────────┘
         │
         ▼
┌──────────────────────────────────────────────────────────────┐
│  Kafka Topic: permission.role.assigned (Partitioned)         │
│  ┌─────────────┬─────────────┬──────────────────┐           │
│  │ Partition 0 │ Partition 1 │ Partition 2      │           │
│  │ Message: {  │            │                  │           │
│  │  eventId:   │            │                  │           │
│  │  timestamp: │            │                  │           │
│  │  accountId: │            │                  │           │
│  │  roleId: 1  │            │                  │           │
│  │ }           │            │                  │           │
│  └─────────────┴─────────────┴──────────────────┘           │
│                                                              │
│  Replicated to Broker 2 & Broker 3 (replication-factor: 2) │
└──────────────────────────────────────────────────────────────┘
         │
         ├─────────────────┬──────────────────┬─────────────────┐
         │                 │                  │                 │
         ▼                 ▼                  ▼                 ▼
    ┌──────────┐      ┌──────────┐      ┌──────────┐      ┌──────────┐
    │ Auth     │      │ User     │      │ Audit    │      │ Other    │
    │ Service  │      │ Service  │      │ Service  │      │ Service  │
    │ Consumer │      │ Consumer │      │ Consumer │      │ Consumer │
    │ Group:   │      │ Group:   │      │ Group:   │      │ Group:   │
    │ auth-svc │      │ user-svc │      │ audit-svc│      │ custom   │
    └──────────┘      └──────────┘      └──────────┘      └──────────┘
         │                 │                  │                 │
         ▼                 ▼                  ▼                 ▼
    Update user      Update user        Log event           Process
    permissions      permissions        for audit           role change
```

### Kafka Consumer Configuration Details

```yaml
spring.kafka.consumer:
  # Consumer group for coordination
  group-id: permission-service
  
  # Start from beginning if no offset is saved
  auto-offset-reset: earliest
  
  # Do NOT auto-commit offsets (manual control for reliability)
  enable-auto-commit: false
  
  # Message deserialization
  key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
  value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
  
  # Trust these packages for JSON deserialization
  properties:
    spring.json.trusted.packages: "com.example.demo.domain.event"
  
  # Max poll records in a single fetch
  max-poll-records: 100
  
  # Session timeout (ms) - if consumer doesn't send heartbeat, considered dead
  session.timeout.ms: 30000
  
  # Poll timeout (ms) - wait max this long for records
  fetch.min.bytes: 1024      # Fetch min 1KB
  fetch.max.wait.ms: 500     # But wait max 500ms
```

### Kafka Producer Configuration Details

```yaml
spring.kafka.producer:
  # All in-sync replicas must acknowledge (strongest guarantee)
  acks: all
  
  # Retry up to 3 times if broker is unavailable
  retries: 3
  
  # Message serialization
  key-serializer: org.apache.kafka.common.serialization.StringSerializer
  value-serializer: org.apache.kafka.common.serialization.StringSerializer
  
  # Idempotent producer - prevent duplicate sends
  properties:
    enable.idempotence: true
    spring.json.add.type.headers: false
  
  # Compression for bandwidth savings
  compression.type: snappy
  
  # Batch size - collect up to 16KB before sending
  batch.size: 16384
  
  # Linger - wait max 10ms for batching
  linger.ms: 10
  
  # Request timeout
  request.timeout.ms: 30000
```

---

## Redis Overview

### What is Redis?

Redis is an in-memory data structure store used for:
- **Distributed caching** to reduce database load
- **Session management** across service instances
- **Rate limiting** and throttling
- **Distributed locks** for concurrency control
- **Pub/Sub messaging** between services
- **Real-time analytics** and counting

### Redis Architecture in Permission Service

```
┌─────────────────────────────────────────────────────────────────┐
│                    Redis Server (Single Instance)               │
│                                                                 │
│  ┌────────────────────────────────────────────────────────┐   │
│  │ Database 0: Permission Service Data                    │   │
│  │                                                        │   │
│  │  ┌──────────────────────────────────────────────┐    │   │
│  │  │ String Keys (JSON serialized)                │    │   │
│  │  │ - permission:roles:all                       │    │   │
│  │  │ - permission:menus:tree:user-123             │    │   │
│  │  │ - permission:check:user:path:method          │    │   │
│  │  └──────────────────────────────────────────────┘    │   │
│  │                                                        │   │
│  │  ┌──────────────────────────────────────────────┐    │   │
│  │  │ Set Keys (For relationships)                 │    │   │
│  │  │ - permission:role:menus:2                    │    │   │
│  │  │ - permission:role:resources:3                │    │   │
│  │  └──────────────────────────────────────────────┘    │   │
│  │                                                        │   │
│  │  ┌──────────────────────────────────────────────┐    │   │
│  │  │ Hash Keys (For structured data)              │    │   │
│  │  │ - permission:account:roles:user-123          │    │   │
│  │  └──────────────────────────────────────────────┘    │   │
│  │                                                        │   │
│  │  TTL: Automatically delete expired keys               │   │
│  └────────────────────────────────────────────────────────┘   │
│  ┌────────────────────────────────────────────────────────┐   │
│  │ Database 1-15: Reserved for other services             │   │
│  └────────────────────────────────────────────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
         ↑                                              ↓
         │                                              │
    ┌────────────┐   ┌──────────────┐   ┌────────────┐
    │ Permission │   │ Caffeine     │   │ Other      │
    │ Service    │───│ Local Cache  │   │ Services   │
    │ Instance 1 │   │ (Backup)     │   │ (Shared)   │
    └────────────┘   └──────────────┘   └────────────┘
         ↑
    ┌────────────┐
    │ Permission │
    │ Service    │
    │ Instance 2 │
    └────────────┘
```

### Cache Hierarchy

```
Request for Roles
       │
       ▼
┌─────────────────────────────────────────┐
│  Check Caffeine Local Cache             │ ← ~1ms
│  (1st Level - Fastest)                  │
└──────────┬────────────────────────────┬──┘
           │ Miss                       │ Hit
           │                           └────▶ Return data
           │
           ▼
┌─────────────────────────────────────────┐
│  Check Redis Cache                      │ ← ~5-10ms
│  (2nd Level - Distributed)              │
└──────────┬────────────────────────────┬──┘
           │ Miss                       │ Hit
           │                           ├─▶ Update Caffeine
           │                           └─▶ Return data
           │
           ▼
┌─────────────────────────────────────────┐
│  Query Database                         │ ← ~50-100ms
│  (3rd Level - Source of Truth)          │
└──────────┬────────────────────────────┬──┘
           │                            │
           ├─▶ Save to Redis            │
           │   (TTL: 10 mins)          │
           │                            │
           ├─▶ Save to Caffeine        │
           │   (TTL: 5 mins)           │
           │                            │
           └─▶ Return data             │
               (All 3 levels updated)   │
```

### Cache Data Structures

**Key Prefix**: `microservice:permission:`

All cache keys include a **version** component for efficient cache invalidation:

#### 1. Version Key (Master Control)
```
Key: microservice:permission:version:{accountId}
Type: String
TTL: No expiry (managed by application)
Value: Version identifier (format: v{timestamp}, e.g., "v1710000000000")
Example Key: microservice:permission:version:user-123
Purpose: Master invalidation key - all cache keys for this account include this version
```

#### 2. Account Roles Cache (String/JSON with Version)
```
Key: microservice:permission:roles:{accountId}:{version}
Type: String (JSON array wrapped in CacheValue<T>)
TTL: 30 minutes
Value Example:
{
  "data": [
    {
      "id": 1,
      "name": "ADMIN",
      "description": "Administrator",
      "isSystem": true
    },
    {
      "id": 2,
      "name": "USER",
      "description": "Standard user",
      "isSystem": false
    }
  ],
  "expireAt": 1710000300000  // Absolute expiry timestamp in ms
}
Example Key: microservice:permission:roles:user-123:v1710000000000
```

#### 3. Menu Tree Cache (String/JSON with Version)
```
Key: microservice:permission:menutree:{accountId}:{version}
Type: String (JSON tree structure wrapped in CacheValue<T>)
TTL: 30 minutes
Value Example:
{
  "data": [
    {
      "id": 1,
      "code": "DASHBOARD",
      "name": "Dashboard",
      "sortOrder": 1,
      "isActive": true,
      "children": [
        {
          "id": 3,
          "code": "DASHBOARD_STATS",
          "name": "Statistics",
          "sortOrder": 1,
          "isActive": true,
          "children": []
        }
      ]
    }
  ],
  "expireAt": 1710000300000
}
Example Key: microservice:permission:menutree:user-123:v1710000000000
```

#### 4. Permission Check Cache (String/JSON with Version)
```
Key: microservice:permission:check:{accountId}:{version}:{path}:{method}
Type: String (PermissionCheckResponse wrapped in CacheValue<T>)
TTL: 30 minutes
Value Example:
{
  "data": {
    "accountId": "user-123",
    "path": "/api/users",
    "method": "GET",
    "hasPermission": true,
    "roles": ["USER", "ADMIN"],
    "checkTime": "2026-03-25T10:30:00Z"
  },
  "expireAt": 1710000300000
}
Example Keys:
- microservice:permission:check:user-123:v1710000000000:/api/users:GET
- microservice:permission:check:user-123:v1710000000000:/api/users:POST
```

#### 5. Refresh Lock Cache (Distributed Lock)
```
Key: microservice:permission:refresh:lock:{accountId}
Type: String
TTL: Very short-lived (milliseconds - prevents concurrent refreshes)
Value: "1"
Example Key: microservice:permission:refresh:lock:user-123
Purpose: Distributed lock using Redis for preventing cache stampede during concurrent refresh attempts
```

### Cache Invalidation Strategy

The service uses **version-based cache invalidation** for efficiency:

**How it works:**
1. Each account has a **version key**: `microservice:permission:version:{accountId}`
2. All cached data keys include this version: `microservice:permission:check:user-123:v1710000000000:...`
3. When permissions change:
   - Generate new version: `v{System.currentTimeMillis()}`
   - Update: `microservice:permission:version:{accountId}` → new version (e.g., v1710000100000)
   - OLD cached keys with old version (v1710000000000) are no longer accessed
   - On next request, new version key is used → cache miss → refresh from DB
   - Old version keys eventually expire (TTL: 30 mins) → automatic cleanup

**Advantages:**
- ✅ No expensive pattern matching (keys permission:*) across millions of keys
- ✅ No manual key deletion (error-prone)
- ✅ Atomic version update in Redis (single operation)
- ✅ Automatic cleanup via TTL (no manual garbage collection)
- ✅ Works seamlessly in distributed systems

| Event | Version Update | Cache Behavior |
|-------|-----------------|---|
| Create/Update/Delete Role affecting account | YES - for affected accounts | Next request uses new version key → cache miss |
| Create/Update/Delete Menu affecting role | YES - for accounts with role | Next request uses new version key → cache miss |
| Create/Update/Delete Resource | YES - for accounts with role | Next request uses new version key → cache miss |
| Assign Role to Account | YES - immediately | Version incremented → all caches invalidated |
| Revoke Role from Account | YES - immediately | Version incremented → all caches invalidated |

**Example Version Update Flow:**
```
BEFORE:
┌─────────────────────────────────────────────┐
│ microservice:permission:version:user-123    │
│ Value: v1710000000000                       │
└─────────────────────────────────────────────┘
         ↓
┌─────────────────────────────────────────────┐
│ Cached Keys (active):                       │
│ - ...menutree:user-123:v1710000000000       │
│ - ...roles:user-123:v1710000000000          │
│ - ...check:user-123:v1710000000000:...      │
└─────────────────────────────────────────────┘

EVENT: Admin updates a menu
       ↓
NEW VERSION GENERATED: v1710000100000
       ↓
AFTER:
┌─────────────────────────────────────────────┐
│ microservice:permission:version:user-123    │
│ Value: v1710000100000 ← UPDATED             │
└─────────────────────────────────────────────┘
         ↓
┌─────────────────────────────────────────────┐
│ Old Cached Keys (stale, not accessed):      │
│ - ...menutree:user-123:v1710000000000       │
│ - ...roles:user-123:v1710000000000          │
│ - ...check:user-123:v1710000000000:...      │
│   (TTL will delete these in 30 mins)        │
└─────────────────────────────────────────────┘
         ↓
┌─────────────────────────────────────────────┐
│ NEW Cached Keys (on next access):           │
│ - ...menutree:user-123:v1710000100000 ← NEW │
│ - ...roles:user-123:v1710000100000 ← NEW    │
│ - ...check:user-123:v1710000100000:... ← NEW│
└─────────────────────────────────────────────┘

User Request:
  GET /permissions/me/menus
  → Check key: microservice:permission:menutree:user-123:v1710000100000
  → Cache MISS (key doesn't exist yet)
  → Fetch from DB
  → Cache with new key: menutree:user-123:v1710000100000
  → Return fresh data with menus updated by admin
```

### Proactive Cache Refresh

The service includes a **proactive refresh mechanism**:

**CacheValue wrapper contains:**
```java
{
  "data": { /* cached data */ },
  "expireAt": 1710000300000  // Absolute expiry timestamp
}
```

**Refresh Logic:**
1. When getting cache: `now = System.currentTimeMillis()`
2. Check: `if (expireAt - now <= 3 minutes)` 
   - YES: Data will expire soon
   - Trigger **async refresh** in background
   - Return current stale data immediately (don't wait)
3. Next request gets fresh data from refresh

**Benefits:**
- Users never experience cache refresh latency
- Fresh data always available
- Graceful degradation if refresh fails

### Redis Connection Pool Management

```yaml
Max Connections: 20
├─ Active connections in use
├─ Grows as demand increases
└─ Max of 20 concurrent connections

Max Idle: 10
├─ Connections kept alive but not in use
├─ Useful for burst traffic
└─ Avoids reconnection overhead

Min Idle: 2
├─ Pre-created connections
├─ Always available
└─ Reduces initial latency
```

### Redis Key Naming Convention

```
microservice:permission:          (prefix)
├── version:{accountId}           (master invalidation key, no version)
│   └── value: v{timestamp}
│
├── menutree:{accountId}:{version} (menu tree with version)
│
├── roles:{accountId}:{version}     (account roles with version)
│
├── check:{accountId}:{version}:{path}:{method}  (permission check with version)
│
└── refresh:lock:{accountId}        (distributed lock, no version)
    └── value: "1" (TTL: milliseconds)
```

**Key Points:**
- ✅ **Prefix consistency**: All keys use `microservice:permission:`
- ✅ **Version in key**: Version embedded in cache key name
- ✅ **Atomic invalidation**: Update single version key to invalidate all related caches
- ✅ **TTL cleanup**: Old version keys auto-deleted after 30 mins

---

## Architecture Diagrams

### Complete Data Flow Diagram

```
┌──────────────────────────────────────────────────────────────────┐
│                        API Request                               │
│              POST /permissions/check                             │
└──────────────┬───────────────────────────────────────────────────┘
               │
               ▼
┌──────────────────────────────────────────────────────────────────┐
│           PermissionController                                   │
│     checkPermissions(accountId, path, method)                    │
└──────────────┬───────────────────────────────────────────────────┘
               │
               ▼
        ┌──────────────────────┐
        │ Check Caffeine Cache │
        │ (1ms)                │
        └──┬──────────────┬────┘
           │ HIT          │ MISS
           │              │
        RETURN DATA        │
                           ▼
                ┌──────────────────────┐
                │ Check Redis Cache    │
                │ (5ms)                │
                └──┬──────────────┬────┘
                   │ HIT          │ MISS
                   │              │
                   UPDATE CAFFEINE  │
                RETURN DATA        │
                                   ▼
                        ┌──────────────────────┐
                        │ Query Database       │
                        │ (100ms)              │
                        │ - Check role perms   │
                        │ - Check resource acc │
                        └──┬──────────────┬────┘
                           │              │
                           SAVE REDIS     │
                           SAVE CAFFEINE  │
                        RETURN DATA       │
                                         │
                                    ┌────┴──────────┐
                                    │               │
                           ┌────────▼────────┐  ┌──▼─────────┐
                           │ Kafka Producer  │  │ Metrics    │
                           │ (Async)         │  │ Monitoring │
                           │ Publish event   │  │            │
                           └─────────────────┘  └────────────┘
                                    │
                                    ▼
                           ┌────────────────────┐
                           │ Kafka Topic:       │
                           │ permission.changed │
                           └────────────────────┘
```

### Three-Tier Caching Visualization

```
SPEED/LATENCY
    ▲
    │
    │  1ms    ┌──────────────────┐
    │         │  Caffeine Cache  │  ← Fastest (in-process memory)
    │         │  (Local Only)     │     Hit Rate: ~80%
    │         └──────────────────┘
    │              ▲    ▲
    │              │ MISS
    │         ┌────┴────────────┐
    │    10ms │  Populate from  │
    │         │  Redis          │
    │         └────┬────────────┘
    │         ┌────▼──────────┐
    │         │  Redis Cache  │  ← Medium (distributed memory)
    │         │  (Cluster)    │    Hit Rate: ~70%
    │         └──────────────┘
    │              ▲
    │              │ MISS
    │         ┌────┴──────────┐
    │   100ms │  Query from   │
    │         │  Database     │
    │         └────┬──────────┘
    │         ┌────▼────────────┐
    │         │  MySQL Database │ ← Slowest (persistent storage)
    │         │  (Source Truth) │   Hit Rate: 100% (always found)
    │         └─────────────────┘
    │
    └──────────────────────────────────────────────► PERSISTENCE
```

---

## Monitoring & Debugging

### Kafka Monitoring Commands

```bash
# Check all Kafka topics
docker exec -it kafka kafka-topics.sh --list --bootstrap-server localhost:9092

# Describe permission topics
docker exec -it kafka kafka-topics.sh \
  --describe \
  --topic permission.changed \
  --bootstrap-server localhost:9092

# Check consumer group lag
docker exec -it kafka kafka-consumer-groups.sh \
  --bootstrap-server localhost:9092 \
  --group permission-service \
  --describe

# Monitor messages in real-time
docker exec -it kafka kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic permission.changed \
  --from-beginning

# Check partition distribution
docker exec -it kafka kafka-topics.sh \
  --describe \
  --bootstrap-server localhost:9092 \
  --under-replicated-partitions
```

### Redis Monitoring Commands

```bash
# Connect to Redis CLI
redis-cli

# Check memory usage
info memory

# View all permission service keys with pattern
keys microservice:permission:*

# Count total permission keys
keys microservice:permission:* | wc -l

# View all version keys (one per account)
keys microservice:permission:version:*

# Get current version for a specific account
get "microservice:permission:version:user-123"

# See which accounts have cached data
keys "microservice:permission:version:*" | sed 's/.*version://'

# Get actual cached menus for an account (requires getting version first)
get "microservice:permission:menutree:user-123:$(redis-cli get 'microservice:permission:version:user-123')"

# Get cached roles for an account
get "microservice:permission:roles:user-123:$(redis-cli get 'microservice:permission:version:user-123')"

# Check specific permission check cache
get "microservice:permission:check:user-123:v1710000000000:GET:/api/users"

# Get cache TTL (returns seconds remaining, -1 = no expiry, -2 = not exists)
ttl "microservice:permission:menutree:user-123:v1710000000000"

# Inspect CacheValue wrapper structure
get "microservice:permission:roles:user-123:v1710000000000"
# Output: {"data":[...],"expireAt":1710000300000}

# Parse the expireAt timestamp to see when cache expires
# Command: redis-cli get "microservice:permission:roles:user-123:v1710000000000" | jq '.expireAt'

# Get key size in bytes
memory usage "microservice:permission:roles:user-123:v1710000000000"

# List all stale keys (old versions not yet deleted)
keys "microservice:permission:*:v1710000000000"

# Monitor Redis operations in real-time
monitor

# Check Redis info
info
info stats    # Total commands processed
info memory   # Memory usage statistics

# Find all refresh lock keys (currently locking refreshes)
keys "microservice:permission:refresh:lock:*"

# Clear all permission service caches (DANGEROUS - clears all data)
EVAL "return redis.call('del', unpack(redis.call('keys', ARGV[1])))" 0 "microservice:permission:*"

# Clear only cache for one account (safer)
EVAL "return redis.call('del', unpack(redis.call('keys', ARGV[1])))" 0 "microservice:permission:*:user-123"

# Clear only stale caches (old versions)
EVAL "return redis.call('del', unpack(redis.call('keys', ARGV[1])))" 0 "microservice:permission:*:v1710000000000"

# Get Redis database info
dbsize              # Total keys in current database
select 1            # Switch to different database
select 0            # Back to permission-service database
```

**Performance Metrics to Monitor:**

- **Cache Hit Ratio**: (hits / (hits + misses)) × 100, should be > 70%
- **Memory Usage**: Should be < 80% of available Redis memory
- **Connected Clients**: Number of active connections
- **Commands/sec**: Operations per second
- **Key Eviction Rate**: How often keys are auto-deleted (TTL)

### Performance Metrics to Monitor

**Kafka:**
- Consumer lag (should be < 1 second)
- Message throughput (msgs/sec)
- Broker disk usage
- Replication lag between brokers

**Redis:**
- Cache hit ratio (should be > 70%)
- Memory usage (% of max)
- Connected clients
- Commands per second
- Eviction rate

---

## GIF/Diagram Storage Location

For visual diagrams and GIF animations, recommend storing in:

```
microservice-demo/
├── docs/
│   ├── diagrams/           ← Store architecture diagrams here
│   │   ├── kafka-flow.gif  ← GIF animations of message flow
│   │   ├── cache-layers.gif
│   │   └── event-flow.png
│   ├── kafka/              ← Kafka-specific documentation
│   ├── redis/              ← Redis-specific documentation
│   └── architecture/       ← Overall architecture docs
├── permission-service/
└── ...
```

### Recommended Tools for Creating Diagrams/GIFs

1. **Lucidchart** - Interactive diagrams (export as GIF/PNG)
2. **Draw.io** - Free diagramming tool
3. **PlantUML** - Text-based diagram generation
4. **Mermaid** - Markdown-native diagrams
5. **Screencastify** - Screen recording for GIFs
6. **OBS** - Screen capture & GIF conversion

Store GIFs at: `docs/diagrams/` with descriptive names like:
- `kafka-message-flow.gif`
- `redis-cache-hierarchy.gif`
- `permission-check-process.gif`

