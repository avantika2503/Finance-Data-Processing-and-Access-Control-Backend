# Finance Data Processing and Access Control Backend

Backend for a finance dashboard: **JWT auth**, **role-based access**, **financial record CRUD** (with filters and soft delete), and **dashboard analytics**. Built with Spring Boot 3 for an internship assignment.

## Tech stack

| Area | Technology |
|------|------------|
| Runtime | Java 17 |
| Framework | Spring Boot 3.2 |
| Persistence | Spring Data JPA, Hibernate, **H2** (file DB `./data/financedb`) |
| Security | Spring Security, **JWT** (jjwt 0.12.x) |
| Validation | Jakarta Bean Validation |
| API docs | SpringDoc OpenAPI 2 (Swagger UI) |

## Prerequisites

- **JDK 17+**
- Windows: use included **`mvnw.cmd`** (no global Maven required)

## Run locally

```bash
cd finance-backend
.\mvnw.cmd clean spring-boot:run
```

The API base URL is **`http://localhost:8080`**.

- **H2 console:** `http://localhost:8080/h2-console` — JDBC URL `jdbc:h2:file:./data/financedb`, user `sa`, empty password.
- **Swagger UI:** `http://localhost:8080/swagger-ui/index.html` or shortcut `http://localhost:8080/api/docs`
- **OpenAPI JSON:** `http://localhost:8080/api-docs` or `http://localhost:8080/v3/api-docs`

Protected routes need header: `Authorization: Bearer <token>` (use **Authorize** in Swagger after login).

## Configuration

| Property | Purpose |
|----------|---------|
| `app.jwt.secret` | HS256 signing key (use a long random secret in production). |
| `app.jwt.expiration-ms` | Token lifetime (default 24h). |
| `app.seed-data` | `true` = on first empty DB, seed demo users and records (see below). |

## Demo users (when seed runs)

If `app.seed-data=true` and the **`users`** table is empty, the app creates:

| Email | Password | Role |
|-------|----------|------|
| `admin@test.com` | `password123` | ADMIN |
| `analyst@test.com` | `password123` | ANALYST |
| `viewer@test.com` | `password123` | VIEWER |

Plus **18** sample financial records (mixed income/expense, categories, dates).  
**Register** via `POST /api/auth/register` creates users with role **VIEWER** by default; promote roles with **ADMIN** user APIs or H2.

## API overview

### Roles and rules

- **VIEWER** — read health, auth, and **GET** financial records only.
- **ANALYST** — same as viewer **plus** dashboard analytics.
- **ADMIN** — full access: user management, record writes, dashboard.

### Endpoints

| Method | Path | Description | Auth / role |
|--------|------|-------------|-------------|
| GET | `/api/health` | Liveness | Public |
| POST | `/api/auth/register` | Create user (default VIEWER) | Public |
| POST | `/api/auth/login` | Returns JWT | Public |
| GET | `/api/users` | List users | ADMIN |
| GET | `/api/users/{id}` | Get user | ADMIN |
| PATCH | `/api/users/{id}/role` | Set role | ADMIN |
| PATCH | `/api/users/{id}/status` | ACTIVE / INACTIVE | ADMIN |
| GET | `/api/records` | List/filter/paginate records | VIEWER+ |
| GET | `/api/records/{id}` | Get one record | VIEWER+ |
| POST | `/api/records` | Create record | ADMIN |
| PUT | `/api/records/{id}` | Update record | ADMIN |
| DELETE | `/api/records/{id}` | Soft delete | ADMIN |
| GET | `/api/dashboard/summary` | Totals + net | ANALYST, ADMIN |
| GET | `/api/dashboard/category-breakdown` | By category | ANALYST, ADMIN |
| GET | `/api/dashboard/monthly-trends` | Monthly income vs expense | ANALYST, ADMIN |
| GET | `/api/dashboard/recent-activity` | Recent records (`?limit=`) | ANALYST, ADMIN |

**Query params for `GET /api/records`:** `type` (INCOME \| EXPENSE), `category`, `startDate`, `endDate` (ISO date), `page`, `size` (max 100).

**Record JSON:** use field **`date`** (not `entryDate`) in request/response bodies.

## Assumptions

- **H2 file DB** is acceptable for local/dev; production would use PostgreSQL (or similar) and externalize secrets.
- **JWT** is stateless; logout is client-side (discard token). No refresh-token flow.
- **Soft delete** hides records from list/detail; DB row remains (`is_deleted`).
- **Monthly trends** use **H2-specific** SQL (`FORMATDATETIME`); another dialect would need a profile-specific query.
- **Email** is stored lowercased; uniqueness is on normalized email.

## What I would improve with more time

- PostgreSQL + Flyway/Liquibase, Spring profiles (`dev` / `prod`).
- Refresh tokens, password reset, audit log for admin actions.
- Pagination metadata consistency, integration tests, CI pipeline.
- Rate limiting on `/api/auth/login`, structured JSON logging, metrics.


Assessment project — not intended as production-ready without hardening (secrets, DB, monitoring, tests).
