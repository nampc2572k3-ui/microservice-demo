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
│   └── intergration/                    # External service integrations
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
- `REDIS_TIMEOUT` — Connection timeout in seconds (optional)
- `REDIS_DATABASE` — Database index (optional, default: `0`)

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
REDIS_DATABASE=0
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
.\mvnw.cmd clean build
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

The service typically exposes RESTful endpoints for managing:
- Roles (CRUD operations)
- Menus (CRUD operations with hierarchical support)
- Resources (CRUD operations for API endpoints)
- Permission mappings (role-menu, role-resource associations)

### Example Endpoints (typical structure)

```
GET    /permission-service/api/v1/roles           - List all roles
POST   /permission-service/api/v1/roles           - Create a new role
GET    /permission-service/api/v1/roles/{id}      - Get role details
PUT    /permission-service/api/v1/roles/{id}      - Update a role
DELETE /permission-service/api/v1/roles/{id}      - Delete a role

GET    /permission-service/api/v1/menus           - List all menus
POST   /permission-service/api/v1/menus           - Create a new menu
GET    /permission-service/api/v1/menus/{id}      - Get menu details
PUT    /permission-service/api/v1/menus/{id}      - Update a menu
DELETE /permission-service/api/v1/menus/{id}      - Delete a menu

GET    /permission-service/api/v1/resources       - List all resources
POST   /permission-service/api/v1/resources       - Create a new resource
GET    /permission-service/api/v1/resources/{id}  - Get resource details
PUT    /permission-service/api/v1/resources/{id}  - Update a resource
DELETE /permission-service/api/v1/resources/{id}  - Delete a resource
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

The service uses a two-level caching strategy for optimal performance:

### 1. Local Cache (Caffeine)
- Fast in-memory caching within the service
- Used for frequently accessed data (roles, menus)
- Automatic expiration and refresh

### 2. Distributed Cache (Redis)
- Shared cache across multiple service instances
- Used for session data and distributed state
- Enables cache invalidation across instances

## Event Streaming (Kafka)

The service integrates with Kafka for:
- Publishing permission change events
- Subscribing to user and role update events from other services
- Asynchronous event processing

Consumer group: `permission-service`

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
    url: jdbc:mysql://${PERMISSION_DB_HOST}:${PERMISSION_DB_PORT}/${PERMISSION_MYSQL_DATABASE}
    
  jpa:
    hibernate:
      ddl-auto: none  # Let Flyway manage schema
    show-sql: true
    
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS}
    consumer:
      group-id: permission-service
    producer:
      acks: all
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


