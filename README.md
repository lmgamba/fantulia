# Fantulia

A personal project in early development: a Spring Boot API, an Angular app and a local Postgres.

Requirements: Java 25, Node LTS, Docker.

## Run locally

```bash
git clone <repo-url> && cd fantulia
cp .env.example .env                  # local dev values only; .env is git-ignored
docker compose up -d db               # Postgres 17 on localhost:5432
```

Then, in two separate terminals:

```bash
cd backend && ./mvnw spring-boot:run  # API: http://localhost:8080/api/health
cd frontend && npm ci && npm start    # app: http://localhost:4200
```

## Tests and checks

```bash
cd backend && ./mvnw verify           # unit + integration tests (Testcontainers, needs Docker)
cd frontend && npm run lint && npm test -- --watch=false && npm run build
```

CI runs the backend `verify`, plus frontend lint, tests and production build, on every pull request.
