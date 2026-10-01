# AGENTS.md - RHSoft

This file gives OpenCode (and similar agents) the minimal repo-specific context to work effectively without guessing. Trust executable sources (pom.xml, configs, scripts) over docs.

## Repository layout (high-signal)

- `codigo/app/` - Spring Boot backend (Java 21, Maven). Main app: `com.exemplo.app.AppApplication`.
- `codigo/frontend/src/` - Vanilla JS/HTML/CSS frontend (static files; no build tooling visible). Served from this directory in dev.
- `docs/` - Requirements, API docs, design/UML, wireframes. Useful context but not executable.
- `data/` - H2 DB files (`testdb.mv.db`, `testdb.trace.db`) at repo root; app also writes to `codigo/app/data/testdb.mv.db`.
- `codigo/app/esquema.sql`, `banco.sql` - DB schema references.

## Backend (Spring Boot / Maven)

**Build & run (from `codigo/app/`):**
- Run dev server: `./mvnw spring-boot:run` (Maven wrapper; Linux/macOS). On Windows use `mvnw.cmd`.
- Compile only: `./mvnw compile`
- Package: `./mvnw package` (produces JAR in `target/`)
- Tests: `./mvnw test` (runs JUnit; also `./mvnw -Dtest=ClassName test` for a single class)

**Tech/runtime facts (verify from config):**
- Java 21 (see `pom.xml`). Maven wrapper present (`.mvn/wrapper/maven-wrapper.properties`).
- Spring Boot 3.5.6 with: web, JPA, validation, security, springdoc-openapi (UI at `/swagger-ui.html` or `/swagger-ui/index.html`), JWT (`com.auth0:java-jwt:4.5.0`).
- DB defaults to H2 file: `jdbc:h2:file:./data/testdb` (see `application.properties`). MySQL/SQL Server deps exist but commented. H2 console enabled (per SecurityConfig) at `/h2-console/**` (permitAll in dev config). 
- DDL: `spring.jpa.hibernate.ddl-auto=update` (auto-migrates schema). Format SQL on.
- JWT secret from env `JWT_SECRET` with fallback `my-secret-key` (do not rely on fallback in non-local contexts).
- CORS allows only `http://127.0.0.1:5500` (explicit in `SecurityConfig`). Frontend must run on that origin when calling API.
- On startup, `DatabaseLoader` (CommandLineRunner) seeds CBO occupations from `src/main/resources/CBO2002 - Ocupacao.json` if `cargo` table is empty. If cargos exist, it logs and skips.

**Key packages (entry points):**
- Controllers: `controller/` (Auth, Vaga, Candidatura, FolhaPagamento, Funcionario, Dashboard, Comunicados, Configuracoes, etc.). REST base paths include `/api/v1/*` in routes (see SecurityConfig).
- Security: `infra/security/` - `SecurityConfig`, `SecurityFilter`, `TokenService`. Stateless JWT. Roles used: `ADMIN`, `USER`, `CANDIDATO`.
- Services/repositories/models/dto as expected. Global exceptions in `exception/`.

## Frontend (vanilla JS)

- Location: `codigo/frontend/src/`. All HTML files at `src/` root (e.g. `login.html`, `vagas.html`, `portalAdministrador.html`, `dashboard.html`).
- Assets: `assets/css/`, `assets/js/`, `assets/images/` following `frontend.md` (global vs pages). Many pages have paired JS under `assets/js/pages/` and CSS under `assets/css/pages/`.
- Conventions (from `frontend.md`): global shared styles in `assets/css/global/global.css`, global JS in `assets/js/global/setup.js`. Per-page CSS/JS in `pages/<pagina>/` folders as a convention (some files also live directly under `pages/`).
- Dev serving: since CORS is locked to `http://127.0.0.1:5500`, run a local static server from `codigo/frontend/src/` (e.g. VS Code Live Server, `python -m http.server 5500`, or simple http server) and open via that origin.
- API calls go to backend (likely `http://localhost:8080` based on typical setup) - check page JS/services (e.g. `assets/js/services/apiLogin.js`). Frontend does not appear to use a bundler/package.json in this tree.

## Testing

- Backend tests under `codigo/app/src/test/java/com/exemplo/app/`. Current: `AppApplicationTests.java` (context load), `DepartamentoServiceTest.java`.
- Run all: `cd codigo/app && ./mvnw test`. Run single test class: `./mvnw -Dtest=DepartamentoServiceTest test`. H2 file DB is used in tests unless overridden.
- No obvious frontend test suite visible; changes to frontend are typically verified manually via browser.

## Commands to prefer (exact, high-signal)

| Action | Command | Notes |
|---|---|---|
| Start backend | `cd codigo/app && ./mvnw spring-boot:run` | Uses H2 file DB at `./data/testdb`. |
| Run all backend tests | `cd codigo/app && ./mvnw test` | Fast, no external services required by default. |
| Run single backend test | `cd codigo/app && ./mvnw -Dtest=ClassName test` | e.g. `AppApplicationTests` or `DepartamentoServiceTest`. |
| Compile backend | `cd codigo/app && ./mvnw compile` | Useful quick check. |
| Build JAR | `cd codigo/app && ./mvnw package` | Outputs to `codigo/app/target/`. |
| Serve frontend (dev) | Serve `codigo/frontend/src/` on `http://127.0.0.1:5500` | Matches CORS; any static server works. |

## Working conventions & gotchas

- **CORS is strict**: only `127.0.0.1:5500` allowed. Do not change origin arbitrarily without updating `SecurityConfig.corsConfigurationSource()`.
- **Stateless security**: JWT via `TokenService` and `SecurityFilter`. Most write endpoints require roles; public routes include auth login/register-candidato, public vagas, H2 console in dev. Be careful not to weaken auth when editing.
- **H2 console**: accessible at `/h2-console/` (frames disabled in one config block but re-enabled with sameOrigin later; behavior is H2-specific). Use only in local dev.
- **DatabaseLoader runs on startup**: if you need to reload CBO data, clear `cargo` table (or truncate) so loader re-runs. It reads from classpath JSON.
- **Auto-DDL update**: `ddl-auto=update` can evolve schema in dev; production likely uses different profile (MySQL/SQL Server). Don't assume destructive migrations.
- **Static frontend, no bundler**: edits to HTML/CSS/JS in `src/` are immediate when served. Follow existing page organization; see `frontend.md`.
- **Path sensitivity**: backend data files live under `codigo/app/data/` (H2) while repo also has `data/` at root. Application.properties points to `./data/testdb` relative to app working dir (so when running from `codigo/app`, it's `codigo/app/data/`).
- **No root-level build scripts**: use Maven wrapper inside `codigo/app`. Frontend has no npm scripts in repo.

## When in doubt
Read the executable sources first (pom.xml, SecurityConfig, application.properties, DatabaseLoader) before making structural changes. If something conflicts with docs (README/docs), trust the config/code you see here.