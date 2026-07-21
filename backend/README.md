# Backend - Bready (Bakery Cost Calculator)

Backend service for the Bready application, built with Java 11 and Spring Boot 2.7.15 (hexagonal architecture: domain / application / infrastructure / web).

## Setup

- Ensure you have Java 11+ installed.
- Ensure you have Maven installed.
- Have a PostgreSQL instance running on `localhost:5433` and create the database:
  ```sql
  CREATE DATABASE bakery_cost_calculator;
  ```
  (Only the database itself needs to be created manually — tables are created automatically by Flyway on startup. Default credentials `postgres`/`postgres`, see `src/main/resources/application.properties`.)
- Run `mvn clean install` to build the project (runs the full test suite).
- Run `mvn spring-boot:run` to start the API on `http://localhost:8080`. Endpoints are under `/api/**`; `/api/auth/register` and `/api/auth/login` are public, everything else requires a `Bearer` JWT.

See the repo root [`README.md`](../README.md) for the full setup (including the Angular frontend) and `../docs/` for architecture and domain documentation.
