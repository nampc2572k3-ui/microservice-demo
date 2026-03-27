# Permission Service

This is the permission service (`permission-service`) of the microservice-demo project.
This README explains how to run the service, required environment variables, and the main endpoints.

## Key Features

- **Role Management** — Create, update, and manage user roles
- **Menu Management** — Define hierarchical menus for the application UI
- **Resource Management** — Define API resources and HTTP methods
- **Permission Control** — Map roles to menus and resources for fine-grained access control
- **Caching** — Redis and local Caffeine caching for improved performance
- **Database Migration** — Flyway for automated database schema management

## Architecture

The permission service follows a layered hexagonal architecture:

```
src/main/java/com/example/demo/
├── PermissionServiceApplication.java    # Spring Boot application entry point
├── core/                                # Core domain logic
│   ├── adapter/                         # Controllers and REST endpoints
│   ├── aop/                             # Aspect-Oriented Programming components
│   ├── application/                     # Business logic and use cases
│   ├── domain/                          # Domain models (entities)
│   └── intergration/                    # External service integrations (NOTE: Folder has typo "intergration" instead of "integration")
├── infrastructure/                      # Infrastructure layer (DB, caching, Kafka)
└── common/                              # Common utilities and components
    ├── constant/                        # Application constants
    ├── exception/                       # Custom exceptions
    └── utils/                           # Utility classes
```

## Database Schema

The service uses the following main tables:

- **roles** — User roles with system flag and soft delete support
- **menus** — Hierarchical menu structure for UI navigation
- **resources** — API endpoints with HTTP methods and actions
- **account_roles** — Mapping between accounts and roles
- **role_menus** — Mapping between roles and menus
- **menu_resources** — Mapping between menus and resources

All tables include audit fields: `created_at`, `created_by`, `updated_at`, `updated_by`, and `is_deleted` for soft deletes.

## Requirements

- **Java 21** — Language version
- **Maven** — Build tool (the project includes `./mvnw`, use `./mvnw.cmd` on Windows)
- **MySQL 8.0+** — Relational database
- **Redis** — For distributed caching and session management
- **Docker & Docker Compose** — Optional, for running supporting services locally

### Dependencies

Main dependencies (see `pom.xml`):
- **Spring Boot 4.0.3** — Framework
- **Spring Data JPA** — ORM
- **Spring Kafka** — Event streaming
- **Flyway** — Database migrations
- **Redis** — Distributed cache
- **Caffeine** — Local cache
- **MySQL Connector** — Database driver
- **Lombok** — Boilerplate reduction

## Environment Variables (Required)

Configure these variables via a `.env` file or your environment management system:

### Database Configuration
- `PERMISSION_DB_HOST` — MySQL host (default: `localhost`)
- `PERMISSION_DB_PORT` — MySQL port (default: `3306`)
- `PERMISSION_MYSQL_DATABASE` — Database name
- `PERMISSION_MYSQL_USER` — Database user
- `PERMISSION_MYSQL_PASSWORD` — Database password
- `PERMISSION_MYSQL_ROOT_PASSWORD` — Root password (used if running MySQL via Docker Compose)

### Server Configuration
- `PERMISSION_SERVICE_PORT` — Service port (default: `8003`)
- `PERMISSION_SERVICE_PATH` — Context path (default: `permission-service`)

### Kafka Configuration
- `KAFKA_BOOTSTRAP_SERVERS` — Kafka broker addresses (e.g., `localhost:9092`)

### Redis Configuration
- `REDIS_HOST` — Redis host (default: `localhost`)
- `REDIS_PORT` — Redis port (default: `6379`)
- `REDIS_TIMEOUT` — Connection timeout in milliseconds (optional)
- `PERMISSION_REDIS_DATABASE` — Redis database index (optional, default: `0`)

### Example `.env`

```env
# Database
PERMISSION_DB_HOST=localhost
PERMISSION_DB_PORT=3306
PERMISSION_MYSQL_DATABASE=permission_db
PERMISSION_MYSQL_USER=permission_user
PERMISSION_MYSQL_PASSWORD=secure_password
PERMISSION_MYSQL_ROOT_PASSWORD=root_password

# Server
PERMISSION_SERVICE_PORT=8003
PERMISSION_SERVICE_PATH=permission-service

# Kafka
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_TIMEOUT=2000
PERMISSION_REDIS_DATABASE=0
```

## Running Locally

### Option 1: Run with Maven (Development)

```powershell
cd permission-service

# On Windows PowerShell
.\mvnw.cmd spring-boot:run
```

### Option 2: Build and Run JAR

```powershell
cd permission-service

# Build the project
.\mvnw.cmd clean package -DskipTests

# Run the JAR
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

### Option 3: Run with Docker Compose (Full Stack)

From the root directory (`microservice-demo`):

```powershell
docker-compose up -d
```

This starts:
- **MySQL** — Database server
- **Redis** — Cache server
- **Kafka** — Event streaming platform
- **Permission Service** — Permission management service
- **Auth Service** — Authentication service
- **API Gateway** — Gateway service

### Option 4: Debug with Maven

```powershell
cd permission-service
.\mvnw.cmd spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"
```

## Building and Testing

### Build the Project

```powershell
cd permission-service
.\mvnw.cmd clean package
```

### Run Tests

```powershell
.\mvnw.cmd test
```

### Run Integration Tests

```powershell
.\mvnw.cmd verify
```

## API Endpoints

The service exposes RESTful endpoints for managing roles, menus, resources, and permissions. 
All endpoints expect JSON request/response bodies and return a `BaseResponse` wrapper.

### Base URL
```
http://localhost:8003/permission-service
```

### Response Format

All successful responses follow this format:
```json
{
  "code": 200,
  "message": "Success message",
  "data": { /* response data */ },
  "timestamp": "2026-03-25T10:30:00Z"
}
```

---

### 1. Role Management

#### Get All Roles
```http
GET /roles
```
- **Description**: Retrieve all available roles
- **Response**: List of `RoleResponse` objects
- **Example Response**:
  ```json
  {
    "code": 200,
    "message": "Get data successfully",
    "data": [
      {
        "id": 1,
        "name": "ADMIN",
        "description": "Administrator role",
        "isSystem": true
      },
      {
        "id": 2,
        "name": "USER",
        "description": "Standard user role",
        "isSystem": false
      }
    ]
  }
  ```

#### Assign Menu to Role
```http
POST /roles/{rid}/menus
Content-Type: application/json

{
  "menuIds": [1, 2, 3]
}
```
- **Description**: Assign one or more menus to a specific role
- **Parameters**:
  - `rid` (path): Role ID
- **Request Body**: `AssignMenuRequest` with list of menu IDs
- **Response**: Success message

---

### 2. Menu Management

#### Get All Menus (Paginated)
```http
GET /menus?page=0&size=10&sort=id,desc
```
- **Description**: Retrieve menus with pagination support
- **Query Parameters**:
  - `page` (optional): Page number (0-indexed, default: 0)
  - `size` (optional): Page size (default: 10)
  - `sort` (optional): Sort criteria (e.g., `id,desc` or `name,asc`)
- **Response**: Paginated list of `MenuResponse` objects with metadata
- **Example Response**:
  ```json
  {
    "code": 200,
    "message": "Get data successfully",
    "data": [
      {
        "id": 1,
        "code": "DASHBOARD",
        "name": "Dashboard",
        "sortOrder": 1,
        "isActive": true,
        "parentId": null
      },
      {
        "id": 2,
        "code": "USER_MANAGEMENT",
        "name": "User Management",
        "sortOrder": 2,
        "isActive": true,
        "parentId": null
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10,
      "totalElements": 25,
      "totalPages": 3
    }
  }
  ```

#### Create Menu
```http
POST /menus
Content-Type: application/json

{
  "code": "NEW_MENU",
  "name": "New Menu",
  "sortOrder": 5,
  "isActive": true,
  "parentId": 1
}
```
- **Description**: Create a new menu item
- **Request Body**: `MenuRequest` with menu details
- **Response**: Success message

#### Update Menu
```http
PUT /menus/{mid}
Content-Type: application/json

{
  "code": "UPDATED_MENU",
  "name": "Updated Menu Name",
  "sortOrder": 3,
  "isActive": true,
  "parentId": 1
}
```
- **Description**: Update an existing menu
- **Parameters**:
  - `mid` (path): Menu ID
- **Response**: Success message

#### Delete Menu
```http
DELETE /menus/{mid}
```
- **Description**: Delete (soft delete) a menu
- **Parameters**:
  - `mid` (path): Menu ID
- **Response**: Success message

---

### 3. Resource Management

#### Get All Resources (Paginated)
```http
GET /resources?page=0&size=10&sort=id,asc
```
- **Description**: Retrieve API resources with pagination
- **Query Parameters**:
  - `page` (optional): Page number (default: 0)
  - `size` (optional): Page size (default: 10)
  - `sort` (optional): Sort criteria
- **Response**: Paginated list of `ResourceResponse` objects
- **Example Response**:
  ```json
  {
    "code": 200,
    "message": "Get data successfully",
    "data": [
      {
        "id": 1,
        "path": "/api/v1/users",
        "method": "GET",
        "action": "VIEW_USERS",
        "isActive": true
      },
      {
        "id": 2,
        "path": "/api/v1/users",
        "method": "POST",
        "action": "CREATE_USER",
        "isActive": true
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10,
      "totalElements": 45,
      "totalPages": 5
    }
  }
  ```

#### Create Resource
```http
POST /resources
Content-Type: application/json

{
  "path": "/api/v1/users/{id}",
  "method": "PUT",
  "action": "EDIT_USER"
}
```
- **Description**: Create a new API resource
- **Request Body**: `ResourceRequest` with resource details
- **Response**: Success message

#### Update Resource
```http
PUT /resources/{rId}
Content-Type: application/json

{
  "path": "/api/v1/users/{id}",
  "method": "PUT",
  "action": "UPDATE_USER"
}
```
- **Description**: Update an existing resource
- **Parameters**:
  - `rId` (path): Resource ID
- **Response**: Success message

#### Toggle Resource Active Status
```http
PATCH /resources/{rId}/toggle
```
- **Description**: Toggle the active/inactive status of a resource
- **Parameters**:
  - `rId` (path): Resource ID
- **Response**: Success message

#### Delete Resource
```http
DELETE /resources/{rId}
```
- **Description**: Delete a resource (soft delete)
- **Parameters**:
  - `rId` (path): Resource ID
- **Response**: Success message

---

### 4. Account & Role Assignment

#### Get Roles by Account
```http
GET /accounts/{accId}/roles
```
- **Description**: Retrieve all roles assigned to a specific account
- **Parameters**:
  - `accId` (path): Account ID (string)
- **Response**: List of `RoleResponse` objects
- **Example Response**:
  ```json
  {
    "code": 200,
    "message": "Get data successfully",
    "data": [
      {
        "id": 1,
        "name": "ADMIN",
        "description": "Administrator role",
        "isSystem": true
      }
    ]
  }
  ```

#### Assign Role to Account
```http
POST /accounts/{accId}/roles
Content-Type: application/json

{
  "roleIds": [1, 2]
}
```
- **Description**: Assign one or more roles to an account
- **Parameters**:
  - `accId` (path): Account ID (string)
- **Request Body**: `AssignRoleRequest` with list of role IDs
- **Response**: Success message
- **Note**: Publishes `permission.role.assigned` Kafka event

#### Revoke Role from Account
```http
DELETE /accounts/{accId}/roles/{roleId}
```
- **Description**: Revoke a role from an account
- **Parameters**:
  - `accId` (path): Account ID (string)
  - `roleId` (path): Role ID
- **Response**: Success message
- **Note**: Publishes `permission.role.revoked` Kafka event

---

### 5. Permission Checking

#### Check Permission
```http
GET /permissions/check?accountId=USER123&path=/api/v1/users&method=GET
X-Internal-Secret: your-secret-key
```
- **Description**: Check if an account has permission to access a resource
- **Query Parameters**:
  - `accountId` (required): Account ID
  - `path` (required): API path to check
  - `method` (required): HTTP method (GET, POST, PUT, DELETE, PATCH, etc.)
- **Headers**:
  - `X-Internal-Secret` (optional): Internal service authentication
- **Response**: `PermissionCheckResponse` with permission result
- **Example Response**:
  ```json
  {
    "code": 200,
    "message": "Permission check completed",
    "data": {
      "accountId": "USER123",
      "path": "/api/v1/users",
      "method": "GET",
      "hasPermission": true,
      "roles": ["USER", "ADMIN"],
      "checkTime": "2026-03-25T10:30:00Z"
    }
  }
  ```

#### Get Menu Tree by Account
```http
GET /permissions/accounts/{accId}/menus
```
- **Description**: Get hierarchical menu structure for an account based on assigned roles
- **Parameters**:
  - `accId` (path): Account ID (string)
- **Response**: Hierarchical list of `MenuTreeResponse` objects
- **Example Response**:
  ```json
  {
    "code": 200,
    "message": "Get data successfully",
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
            "code": "DASHBOARD_VIEW",
            "name": "Dashboard View",
            "sortOrder": 1,
            "isActive": true,
            "children": []
          }
        ]
      }
    ]
  }
  ```

#### Get My Menus (Current User)
```http
GET /permissions/me/menus
X-Account-Id: USER123
```
- **Description**: Get menu tree for the current authenticated user
- **Headers**:
  - `X-Account-Id` (required): Current user's account ID from JWT token
- **Response**: Hierarchical list of `MenuTreeResponse` objects
- **Example Response**: Same as above

---

### Error Response Format

When an error occurs, the response format is:
```json
{
  "code": 400,
  "message": "Error message describing what went wrong",
  "data": null,
  "timestamp": "2026-03-25T10:30:00Z"
}
```

### Common Error Codes

- **200** — Success
- **400** — Bad Request (invalid input)
- **401** — Unauthorized (missing authentication)
- **403** — Forbidden (insufficient permissions)
- **404** — Not Found (resource doesn't exist)
- **409** — Conflict (duplicate entry, constraint violation)
- **500** — Internal Server Error

---

### Testing API Endpoints

You can test these endpoints using tools like:
- **Postman** — GUI-based REST client
- **cURL** — Command-line tool
- **Thunder Client** — VS Code extension

Example cURL request:
```bash
curl -X GET "http://localhost:8003/permission-service/roles" \
  -H "Content-Type: application/json"
```

## Database Migrations

Database migrations are managed by **Flyway**. Migration files are located in:

```
src/main/resources/db/migration/
```

Migrations are automatically applied on application startup. See the migration files for the current database schema:

- `V1_001__create_roles_table.sql` — Creates roles table
- `V1_002__create_menus_table.sql` — Creates menus table with parent-child relationships
- `V1_003__create_resources_table.sql` — Creates resources table for API endpoints
- `V1_004__create_account_roles_table.sql` — Links accounts to roles
- `V1_005__create_role_menus_table.sql` — Links roles to menus
- `V1_006__create_menu_resources_table.sql` — Links menus to resources

## Caching Strategy

The service uses a two-level caching strategy for optimal performance and scalability:

### 1. Local Cache (Caffeine)
- **Purpose**: Fast in-memory caching within a single service instance
- **Scope**: Service-local, not shared across instances
- **Performance**: Sub-millisecond access time
- **Use Cases**:
  - Frequently accessed roles (refreshed every 5 minutes)
  - Menu hierarchies (refreshed every 10 minutes)
  - Permission check results (short-lived, 1 minute TTL)
- **Configuration**:
  - Max size: 1000 entries per cache
  - Expire after write: 5-10 minutes (varies by cache type)
  - Automatic refresh on expiry

### 2. Distributed Cache (Redis)
- **Purpose**: Shared cache across multiple service instances
- **Scope**: Cluster-wide, enables cache sharing between services
- **Performance**: Few milliseconds for network I/O
- **Database**: `PERMISSION_REDIS_DATABASE` (separate DB for data isolation)
- **Use Cases**:
  - Cross-instance cache synchronization
  - Session data for distributed systems
  - Rate limiting counters
  - Distributed locks

### Cache Data Structures

**Key Prefix**: `microservice:permission:`

The service uses **version-based cache invalidation** strategy. Each account has a unique version key that changes when permissions are updated, automatically invalidating all related cache entries.

#### 1. Version Key (Master Control)
```
Key Format: microservice:permission:version:{accountId}
Type: String
TTL: No expiry (managed by application)
Content: Version identifier (format: v{timestamp}, e.g., "v1234567890123")
Example Key: microservice:permission:version:user-123
Purpose: Cache invalidation - when version changes, all cached data for that account becomes stale
```

#### 2. Permission Check Cache
```
Key Format: microservice:permission:check:{accountId}:{version}:{path}:{method}
Type: String (JSON serialized with expiry metadata)
TTL: 30 minutes
Content: Permission check result with auto-refresh mechanism
Example Key: microservice:permission:check:user-123:v1234567890:GET:/api/users:GET
```

#### 3. Menu Tree Cache
```
Key Format: microservice:permission:menutree:{accountId}:{version}
Type: String (JSON serialized hierarchical menu structure)
TTL: 30 minutes
Content: Hierarchical menu tree based on assigned roles
Example Key: microservice:permission:menutree:user-123:v1234567890
```

#### 4. Account Roles Cache
```
Key Format: microservice:permission:roles:{accountId}:{version}
Type: String (JSON array of role objects)
TTL: 30 minutes
Content: List of roles assigned to an account
Example Key: microservice:permission:roles:user-123:v1234567890
```

#### 5. Refresh Lock Cache
```
Key Format: microservice:permission:refresh:lock:{accountId}
Type: String ("1")
TTL: Short-lived (prevents concurrent refresh attempts)
Content: Lock flag to prevent cache stampede
Example Key: microservice:permission:refresh:lock:user-123
Purpose: Distributed lock using Redis for cache refresh coordination
```

### Cache Value Wrapper

All cached data is wrapped in a `CacheValue<T>` object:
```java
{
  "data": { /* actual cached data */ },
  "expireAt": 1234567890123  // Absolute expiry timestamp in ms
}
```

This wrapper enables:
- **Proactive refresh** — Data refreshed 3 minutes before actual expiry
- **Type safety** — Generic type information for deserialization
- **Graceful degradation** — Expired data can be used temporarily if refresh fails

### Cache Invalidation Strategy

The service uses **version-based cache invalidation** for efficiency:

**When a change occurs:**
1. Generate new version: `v{System.currentTimeMillis()}`
2. Update: `microservice:permission:version:{accountId}` → new version
3. Next cache access uses new version key → cache miss → refresh from DB
4. Old version keys eventually expire (TTL: 30 mins) → automatic cleanup

**This approach avoids:**
- Pattern matching across millions of keys (slow)
- Manual key deletion (error-prone)
- Distributed coordination issues

| Event | Cache Behavior | Version Updated |
|-------|------|---|
| Create/Update/Delete Role affecting account | Next request uses new version key → cache miss | Yes, for affected accounts |
| Create/Update/Delete Menu affecting role | Next request uses new version key → cache miss | Yes, for accounts with that role |
| Assign Role to Account | Version invalidated immediately | Yes, for that account |
| Revoke Role from Account | Version invalidated immediately | Yes, for that account |

**Example Flow:**
```
User has version: v1000
GET /permissions/me/menus
  → Check key: microservice:permission:menutree:user-123:v1000
  → Cache HIT → Return data

Admin updates a menu the user can see
  → Update user version: v1000 → v2000 (new timestamp)

User makes next request
  → Check key: microservice:permission:menutree:user-123:v2000
  → Cache MISS (old v1000 expired or doesn't match)
  → Fetch from DB → Cache with new version v2000
  → Return fresh data
```

### Redis Connection Pool

Configuration in `Application.yaml`:
```yaml
spring.data.redis.lettuce.pool:
  max-active: 20          # Max connections in pool
  max-idle: 10            # Max idle connections
  min-idle: 2             # Min connections to maintain
```

### Redis CLI Examples

```bash
# Connect to Redis
redis-cli

# Select the permission-service database
select 0  # or ${PERMISSION_REDIS_DATABASE}

# View all permission service keys
keys microservice:permission:*

# Check account version (master key for invalidation)
get "microservice:permission:version:user-123"

# Get version and use it to check specific cache
SET myver 'microservice:permission:version:user-123'
GET $myver  # Returns version like "v1710000000000"
GET "microservice:permission:menutree:user-123:$(GET microservice:permission:version:user-123)"

# Check specific cached menus
get "microservice:permission:menutree:user-123:v1710000000000"

# Check specific cached roles
get "microservice:permission:roles:user-123:v1710000000000"

# Check permission check cache
get "microservice:permission:check:user-123:v1710000000000:GET:/api/users"

# Get cache TTL (time to live in seconds)
ttl "microservice:permission:menutree:user-123:v1710000000000"
# Returns: -1 (no expiry), -2 (key doesn't exist), or remaining seconds

# Inspect cache value structure (CacheValue wrapper)
get "microservice:permission:roles:user-123:v1710000000000"
# Returns JSON like: {"data":[...],"expireAt":1710000300000}

# Get key size in bytes
memory usage "microservice:permission:roles:user-123:v1710000000000"

# Monitor in real-time
monitor

# Check connection info
info

# Find all version keys (to see which accounts have cached data)
keys "microservice:permission:version:*"

# Clear all permission service caches
EVAL "return redis.call('del', unpack(redis.call('keys', ARGV[1])))" 0 "microservice:permission:*"

# Clear only cache for one account (safe cleanup)
EVAL "return redis.call('del', unpack(redis.call('keys', ARGV[1])))" 0 "microservice:permission:*:user-123"
```

### Cache Performance Tips

1. **Use JSON serialization** — Efficient for complex objects
2. **Set appropriate TTLs** — Balance between consistency and performance
3. **Monitor cache hit ratio** — Lower hit rates indicate cache thrashing
4. **Implement cache warming** — Pre-load frequently accessed data on startup
5. **Use async cache operations** — Don't block on cache operations
6. **Implement cache layers** — Local cache for speed, Redis for distribution

## Event Streaming (Kafka)

The service integrates with Kafka for:
- **Publishing** permission change events to other services
- **Consuming** user and role update events from auth-service
- **Asynchronous** event processing for eventual consistency
- **Event-driven** architecture enabling loose coupling between services

### Kafka Topics

#### Published Topics (Producer)

| Topic | Description | Event Type | Publish When |
|-------|-------------|-----------|--------------|
| `permission.changed` | Permission data has been modified | Generic permission update | Create/update/delete roles, menus, or resources |
| `permission.role.assigned` | Role assigned to account | Role assignment | After successfully assigning a role to an account |
| `permission.role.revoked` | Role revoked from account | Role revocation | After successfully revoking a role from an account |

#### Subscribed Topics (Consumer)

| Topic | Description | Consumer Action |
|-------|-------------|-----------------|
| `account.created` | New account created in auth-service | (Optional) Initialize default permissions |
| `account.deleted` | Account deleted from auth-service | Remove all role assignments for that account |

### Consumer Configuration

- **Consumer Group**: `permission-service`
- **Auto Offset Reset**: `earliest` (process all messages from beginning if offset is lost)
- **Enable Auto Commit**: `false` (manual commit for reliability)
- **Deserialization**: JSON format with trusted packages `com.example.demo.domain.event`

### Producer Configuration

- **Acknowledgment Level**: `all` (wait for all in-sync replicas to acknowledge)
- **Retries**: `3` (retry failed sends up to 3 times)
- **Serialization**: String key, String value
- **Idempotence**: `true` (prevent duplicate messages)

### Example Event Messages

#### Permission Changed Event
```json
{
  "eventId": "evt-123456789",
  "eventType": "PERMISSION_CHANGED",
  "timestamp": "2026-03-25T10:30:00Z",
  "source": "permission-service",
  "resourceId": 45,
  "resourcePath": "/api/v1/users",
  "resourceMethod": "POST",
  "action": "CREATE_USER",
  "changeType": "CREATED"
}
```

#### Role Assigned Event
```json
{
  "eventId": "evt-987654321",
  "eventType": "ROLE_ASSIGNED",
  "timestamp": "2026-03-25T10:30:00Z",
  "source": "permission-service",
  "accountId": "acc-user-123",
  "roleId": 2,
  "roleName": "MANAGER",
  "assignedBy": "admin-user",
  "reason": "Promotion"
}
```

#### Role Revoked Event
```json
{
  "eventId": "evt-555666777",
  "eventType": "ROLE_REVOKED",
  "timestamp": "2026-03-25T10:30:00Z",
  "source": "permission-service",
  "accountId": "acc-user-456",
  "roleId": 3,
  "roleName": "SUPERVISOR",
  "revokedBy": "admin-user",
  "reason": "Department change"
}
```

### Kafka Monitoring

Check Kafka topics and consumer lag:

```bash
# List all topics
docker exec -it microservice-demo-kafka-1 kafka-topics.sh --list --bootstrap-server localhost:9092

# Check consumer group status
docker exec -it microservice-demo-kafka-1 kafka-consumer-groups.sh \
  --bootstrap-server localhost:9092 \
  --group permission-service \
  --describe

# View messages in a topic (last 10)
docker exec -it microservice-demo-kafka-1 kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic permission.changed \
  --from-beginning \
  --max-messages 10
```

### Event Flow Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                      Permission Service                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Role Assignment/Revocation                                     │
│         ↓                                                        │
│  [RoleService.assignRole()]                                     │
│         ↓                                                        │
│  [KafkaProducer]                                                │
│         ↓                                                        │
│  Publishes:                                                      │
│  - permission.role.assigned                                     │
│  - permission.role.revoked                                      │
│         ↓                                                        │
│  ┌─────────────────────────────────────────────────────────┐  │
│  │ Kafka Topics                                            │  │
│  │ - permission.changed                                    │  │
│  │ - permission.role.assigned                              │  │
│  │ - permission.role.revoked                               │  │
│  └─────────────────────────────────────────────────────────┘  │
│         ↓                                                        │
│  [KafkaConsumer - permission-service group]                     │
│  Consumes from: account.created, account.deleted                │
│         ↓                                                        │
│  Other services receive permission events                        │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## Configuration Properties

Key properties in `src/main/resources/Application.yaml`:

```yaml
server:
  port: ${PERMISSION_SERVICE_PORT}
  servlet:
    context-path: /${PERMISSION_SERVICE_PATH}

spring:
  application:
    name: permission-service
  
  datasource:
    url: jdbc:mysql://${PERMISSION_DB_HOST}:${PERMISSION_DB_PORT}/${PERMISSION_MYSQL_DATABASE}?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=Asia/Ho_Chi_Minh
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: ${PERMISSION_MYSQL_USER}
    password: ${PERMISSION_MYSQL_PASSWORD}
    
  jpa:
    hibernate:
      ddl-auto: none  # Let Flyway manage schema
    show-sql: true
    
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS}
    consumer:
      group-id: permission-service
      auto-offset-reset: earliest
    producer:
      acks: all
      retries: 3
      
  data:
    redis:
      host: ${REDIS_HOST}
      port: ${REDIS_PORT}
      timeout: ${REDIS_TIMEOUT}
      database: ${PERMISSION_REDIS_DATABASE}
      
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
    out-of-order: true
    clean-disabled: true
```

## Common Tasks

### Add a New Migration

1. Create a new file in `src/main/resources/db/migration/`:
   ```
   V1_XXX__description_of_change.sql
   ```

2. Write your SQL migration

3. Build and run the application — Flyway will automatically apply the migration

### Access the Database Directly

```powershell
# Using MySQL CLI
mysql -h localhost -u permission_user -p permission_db

# Or use Docker
docker exec -it microservice-demo-mysql-1 mysql -u permission_user -p permission_db
```

### Check Redis Cache

```powershell
# Using redis-cli
redis-cli

# Connect to specific database
select 0

# List all keys
keys *

# Get a value
get <key>
```

## Troubleshooting

### Service won't start

1. **Check environment variables** — Ensure all required variables are set
2. **Verify MySQL is running** — Check database connectivity
3. **Review logs** — Look for detailed error messages in console output
4. **Port already in use** — Change `PERMISSION_SERVICE_PORT`

### Database migration fails

1. **Check migration files** — Ensure SQL syntax is correct
2. **Review Flyway configuration** — Check `Application.yaml`
3. **Manual cleanup** — If needed, check the `flyway_schema_history` table

### Cache issues

1. **Redis not responding** — Verify Redis is running and accessible
2. **Clear cache** — Use `redis-cli FLUSHDB` to clear the entire database
3. **Connection timeout** — Increase `REDIS_TIMEOUT` in environment variables

## Project Structure

```
permission-service/
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── core/
│   │   │   ├── infrastructure/
│   │   │   ├── common/
│   │   │   └── PermissionServiceApplication.java
│   │   └── resources/
│   │       ├── Application.yaml
│   │       └── db/migration/
│   └── test/
│       └── java/com/example/demo/
├── target/                    # Build artifacts
├── pom.xml                    # Maven configuration
├── mvnw & mvnw.cmd           # Maven wrapper
└── README.md                  # This file
```

## Contributing

When contributing to this service:

1. Follow the layered architecture pattern
2. Write tests for new features
3. Add database migrations for schema changes
4. Update this README with new endpoints or features
5. Ensure all tests pass before submitting

## Related Services

- **Auth Service** — Handles authentication and JWT tokens
- **API Gateway** — Routes requests to appropriate microservices
- **Other Services** — Additional domain-specific microservices


