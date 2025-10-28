# Web API

## Run

```bash
# Start PostgreSQL database
docker compose up -d --wait

# Run the application
./mvnw spring-boot:run

# Cleanup
docker compose down -t 0 -v --remove-orphans
```

## Testing

```bash
./mvnw test
```

## API Examples

```bash
# Health check
curl http://localhost:8080/api/health

# List users
curl http://localhost:8080/api/users

# Create user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice","email":"alice@example.com"}'

# Get user by ID
curl http://localhost:8080/api/users/1

# Update user
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice Updated","email":"alice.updated@example.com"}'

# Delete user
curl -X DELETE http://localhost:8080/api/users/1
```
