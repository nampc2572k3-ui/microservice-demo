# Auth Service

This is the authentication service (`auth-service`) of the microservice-demo project.
This README explains how to run the service, required environment variables, and the main endpoints (including the refresh token endpoint).

## Key features
- Register account (`/api/v1/auth/register`)
- Login and return access token + refresh token (`/api/v1/auth/login`)
- Logout (blacklist access token) (`/api/v1/auth/logout`)
- Refresh token — exchange a valid refresh token for new access and refresh tokens (`/api/v1/auth/refresh`)
- Store refresh tokens in Redis and blacklist access tokens on logout
- Fine-grained authorization via `Role` / `RoleMenu` (CustomAuthorizationManager)

## Requirements
- Java 21
- Maven (the project includes `./mvnw`, use `./mvnw.cmd` on Windows)
- Docker & Docker Compose (optional, for running MySQL and Redis locally)

## Environment variables (required)
Provide these variables via a `.env` file or your environment management system.

- `AUTH_DB_HOST` — MySQL host
- `AUTH_DB_PORT` — MySQL port
- `AUTH_MYSQL_DATABASE` — database name
- `AUTH_MYSQL_USER` — database user
- `AUTH_MYSQL_PASSWORD` — database password
- `AUTH_MYSQL_ROOT_PASSWORD` — (used if running MySQL via `docker-compose`)

- `JWT_SECRET` — secret key used to sign JWTs (MUST be strong and never committed to VCS)
- `JWT_ACCESS_TOKEN_EXPIRATION_MS` — access token expiration in milliseconds
- `JWT_REFRESH_TOKEN_EXPIRATION_MS` — refresh token expiration in milliseconds

- `REDIS_HOST` — Redis host
- `REDIS_PORT` — Redis port
- `REDIS_TIMEOUT` — Redis connection timeout (optional)
- `REDIS_DATABASE` — Redis database index (optional)

Example `.env` (example only — do not use in production):


## Run locally
You can run the service directly with Maven or run supporting services with Docker Compose.

1) Run with Maven (development)

```powershell
cd auth-service
# On Windows PowerShell
.\mvnw.cmd spring-boot:run
```

2) Run with Docker Compose (MySQL + Redis)

```powershell
# From project root (where docker-compose.yml is located)
# Create an .env file with the environment variables shown above
docker-compose up -d
# Then build and run the service
cd auth-service
.\mvnw.cmd -DskipTests package
java -jar target\demo-0.0.1-SNAPSHOT.jar
```

> Note: The service runs by default on port 8080 with context-path `/auth` → base URL: `http://localhost:8080/auth`.

## Main endpoints & examples
All endpoints return a `BaseResponse<T>` shape: { success, data, message, errorCode }.

1) POST /api/v1/auth/register
- Purpose: create a new account
- Example body:
```json
{
  "userName": "nam2",
  "email": "nam2@example.com",
  "password": "P@ssw0rd",
  "roleIds": [1]
}
```

2) POST /api/v1/auth/login
- Purpose: login and receive accessToken + refreshToken in the response data
- Example body:
```json
{
  "input": "nam2",    // username or email
  "password": "P@ssw0rd"
}
```

3) POST /api/v1/auth/logout
- Purpose: blacklist the access token. Send the access token in the `Authorization` header: `Authorization: Bearer <accessToken>`

4) POST /api/v1/auth/refresh
- Purpose: exchange a valid refresh token for a new access token and refresh token
- Example body:
```json
{ "refreshToken": "<refresh-token-here>" }
```
- Example response data:
```json
{
  "accessToken": "<new-access-token>",
  "refreshToken": "<new-refresh-token>"
}
```

## Security notes
- DO NOT commit `JWT_SECRET` or any secrets to source control.
- `JWT_SECRET` should be sufficiently long (at least 32 bytes) or consider using asymmetric keys (RS256 + JWKS) in production.
- Use a secrets manager / vault in production environments.
- Protect `/login` and `/refresh` with rate-limiting to mitigate brute-force attacks.

## Debug & troubleshooting
- If you see hashed passwords or parameter bindings in logs: disable `spring.jpa.show-sql` and lower `org.hibernate.orm.jdbc.bind` logging (this repository already configures those settings in `Application.yaml`).
- For invalid/expired JWT errors, verify `JWT_SECRET` and the token expiration values.
- If tokens don’t match, check Redis — refresh tokens are stored under `microservice:auth:refresh-token:<username>`.

## Tests
Run unit tests:

```powershell
cd auth-service
.\mvnw.cmd test
```

