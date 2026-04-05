# Finance Backend

Spring Boot backend for the finance dashboard assignment. Built **phase by phase** per the project plan (entities, auth, RBAC, records, dashboard, docs).

## Current status

- **Phase 1:** Maven project and dependencies.
- **Phase 2:** File-based H2 (`./data/financedb`), enums (`Role`, `UserStatus`, `RecordType`), `User` and `FinancialRecord` entities, JPA repositories.

### Verify Phase 2

1. Run `.\mvnw.cmd spring-boot:run` — the app should start and Hibernate should create/update tables (`users`, `financial_records`).
2. Optional: open the H2 console at `http://localhost:8080/h2-console` (Spring Security may block this until Phase 3; JDBC URL: `jdbc:h2:file:./data/financedb`, user `sa`, empty password).

## Prerequisites

- JDK 17+

## Run locally

```bash
.\mvnw.cmd spring-boot:run
```

(Or `mvn spring-boot:run` if Maven is on your `PATH`.)

## GitHub

After creating an empty repository on GitHub, connect this folder:

```bash
git remote add origin https://github.com/YOUR_USER/YOUR_REPO.git
git branch -M main
git push -u origin main
```

Replace `YOUR_USER` and `YOUR_REPO` with your account and repository name.
