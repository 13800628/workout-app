This repository contains a Vite + React frontend (under `frontend/test-React`) and a Spring Boot backend (top-level `pom.xml`, sources under `src/main/java/com/workout`).

Keep instructions short and concrete. The sections below highlight project structure, dev/run commands, important patterns, and examples that help an AI agent be productive quickly.

1) Big picture
- Frontend: `frontend/test-React` — Vite + React 19 SPA. Built output is copied into Spring Boot's `src/main/resources/static/` in the Dockerfile build stage and served as static content by Spring Boot in production.
- Backend: Spring Boot 3.5 application (Java 17) in `src/main/java/com/workout`. Exposes REST APIs under `/api/users` and `/api/workouts` consumed by the SPA.
- Database: PostgreSQL (docker-compose service `db`). The app uses Spring Data JPA and custom repository queries for some operations.

2) How to build & run (developer workflows)
- Local (Docker): The supported, canonical dev workflow is via docker compose:
  - Build & start (first time or after code changes that affect the image): `docker compose up --build`
  - Start (no rebuild): `docker compose up`
  - Stop and cleanup: `docker compose down`

- Build artifacts created by Maven: `mvn clean package -DskipTests` (the Dockerfile runs this during image build). The final runnable artifact is the assembled JAR copied to the runtime image as `app.jar`.

- Frontend commands (when working only on front code): inside `frontend/test-React`
  - Dev server: `npm run dev` (Vite)
  - Build: `npm run build` (produces `dist/`)
  - Lint: `npm run lint`

3) Key integration points and traps
- Static asset pipeline: The Dockerfile copies `frontend/test-React/dist/` into `src/main/resources/static/` before building the Java app. When testing locally without Docker, ensure you either run the React dev server (port 5173) or build and copy `dist/` into the backend resources so Spring Boot serves the SPA.
- Security/CORS: `SecurityConfig` allows requests from `http://localhost:5173` (Vite dev server). If you add new dev hosts, update `SecurityConfig.configurationSource()`.
- Database connection: `application.properties` points to localhost Postgres by default. When using docker-compose, `SPRING_DATASOURCE_URL` is set to `jdbc:postgresql://db:5432/workout_db` in `docker-compose.yml`.

4) Project-specific patterns worth noting (with file examples)
- Bulk-delete pattern (single-query delete): repositories define `deleteDirectlyById` using `@Modifying` JPQL to avoid extra select calls. Example: `UserRepository.deleteDirectlyById` and `WorkoutRepository.deleteDirectlyById`.
  - Services check the returned deleted count and throw an `IllegalArgumentException` when 0, e.g. `UserService.deleteUser` and `WorkoutService.deletedWorkout`.

- Partial update template using Consumer<T>: `WorkoutService.update(Long id, Consumer<Workout> updateLogic)` and `updateField(...)` provide a canonical way to perform partial updates without creating many redundant methods. Tests exercise calls like `workoutService.update(id, w -> w.setName("after"));`.

- Entity-graph for eager association loading: `WorkoutRepository.findByUserId` is annotated with `@EntityGraph(attributePaths = {"user"})` to fetch the owning `User` with workouts in one query.

- Unified error response: `GlobalExceptionHandle` / `ErrorResponse` return a JSON shape with fields `status`, `message`, `details` (list). Use this format when adding new controllers or validation flows so the frontend can map messages consistently.

5) Where to look for tests and how they are organized
- Unit tests are under `src/test/java/com/workout/...`. Service tests mock repositories and verify behavior like delete counts and update templates (see `UserServiceTest` and `WorkoutServiceTest`).
- Use `mvn -Dtest=... test` to run tests selectively.

6) Examples agents should use when generating or changing code
- When adding a delete endpoint: call repository `deleteDirectlyById(id)` and check returned int, throw an IllegalArgumentException when 0 (match existing services).
- When adding partial update behavior: prefer `update(Long id, Consumer<Workout> updateLogic)` style to keep consistency with `WorkoutService`.
- When adding new REST controllers: follow existing controllers for response types (use `ResponseEntity`, appropriate status codes, and `@Valid` on request bodies). Mirror `UserController`/`WorkoutController` patterns.

7) Files to reference quickly
- Backend entry: `src/main/java/com/workout/WorkoutApplication.java`
- Security config: `src/main/java/com/workout/config/SecurityConfig.java`
- Global error handling: `src/main/java/com/workout/exception/GlobalExceptionHandle.java` and `ErrorResponse.java`
- Repositories: `src/main/java/com/workout/repository/UserRepository.java`, `WorkoutRepository.java`
- Services: `src/main/java/com/workout/service/UserService.java`, `WorkoutService.java`
- Frontend entry: `frontend/test-React/src/main.tsx`, `frontend/test-React/package.json`, `frontend/test-React/vite.config.ts`
- Docker + compose: `Dockerfile`, `docker-compose.yml`

8) Small implementation rules for generated code
- Preserve existing JSON error shape (status/message/details) in new controllers and exception handlers.
- Use existing DTOs (records) under `dto/` when available; controllers typically accept `@Valid` record request objects.
- Prefer JPA repository patterns already in use: `@EntityGraph` when loading associations for read endpoints, `@Modifying` + `@Query` for bulk deletes.

9) Ask the user before making infra changes
- Don't change CORS origins, Docker compose database credentials, or ports without asking — these are deliberate for local dev.

If a section is unclear or you'd like extra details (for example, common test fixtures, the shape of DTO records, or the exact JSON error schema), tell me which part to expand and I'll update this file.
