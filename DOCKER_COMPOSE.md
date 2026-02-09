# Local Development with Docker Compose

This project includes a Docker Compose configuration for running a local PostgreSQL database for development.

## Quick Start

### Start PostgreSQL

```bash
docker-compose up -d
```

This will:
- Start PostgreSQL 16 on port 5432
- Create database `drei_alben`
- Use credentials: `postgres/postgres`
- Persist data in a Docker volume

### Stop PostgreSQL

```bash
docker-compose down
```

### Reset Database (Delete All Data)

```bash
docker-compose down -v
```

### Run the Backend

After starting PostgreSQL:

```bash
./mvnw spring-boot:run
```

The application will automatically:
- Connect to the local PostgreSQL instance
- Run Flyway migrations to create tables
- Start on port 8080

### Access PostgreSQL Directly

```bash
# Using docker exec
docker exec -it drei-alben-postgres psql -U postgres -d drei_alben

# Using psql from host (if installed)
psql -h localhost -U postgres -d drei_alben
```

### View Logs

```bash
docker-compose logs -f postgres
```

## Configuration

The default configuration in `application.yaml` matches the Docker Compose setup:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/drei_alben
    username: postgres
    password: postgres
```

No additional configuration needed for local development!

## Troubleshooting

### Port 5432 Already in Use

If you have another PostgreSQL instance running:

```bash
# Stop system PostgreSQL (macOS with Homebrew)
brew services stop postgresql

# Or change the port in docker-compose.yml
ports:
  - "5433:5432"

# Then update application.yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/drei_alben
```

### Database Connection Error

Check if PostgreSQL is running:

```bash
docker-compose ps
```

If not healthy, check logs:

```bash
docker-compose logs postgres
```

### Reset Flyway Migration History

If you need to reset migrations:

```bash
docker exec -it drei-alben-postgres psql -U postgres -d drei_alben -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;"
```

Then restart the backend to re-run migrations.
