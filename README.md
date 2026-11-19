# English Academy Credits & Enrollment API

A transactional RESTful API built with **Java**, **Spring Boot**, and **Oracle Database 26ai Free**, designed to manage student credit balances, immutable financial ledgers, and course enrollments under strict concurrency and security controls.

## Key Engineering Highlights

* **ACID Transactions & Concurrency Control:** Uses `@Transactional` alongside pessimistic database locking (`SELECT ... FOR UPDATE` via `@Lock(LockModeType.PESSIMISTIC_WRITE)`) to prevent race conditions, double-spending, or course overbooking.
* **Principle of Least Privilege (Defense in Depth):** Separates database ownership (`english_admin` for DDL) from application runtime access (`english_app_user` restricted strictly to `SELECT`, `INSERT`, and `UPDATE`, with `DELETE` revoked on audit logs).
* **Role-Based Access Control (RBAC):** Secures endpoints with Spring Security and BCrypt password hashing, isolating `ROLE_ADMIN` operations from `ROLE_STUDENT` personal balance and enrollment queries.
* **Database-Level Integrity:** Enforces `CHECK` and `UNIQUE` constraints directly in Oracle to guarantee non-negative balances and valid course capacities.

## Tech Stack
* **Backend:** Java 21, Spring Boot 3, Spring Web, Spring Data JPA, Spring Security, Bean Validation
* **Database:** Oracle Database 26ai Free (PL/SQL)
* **Infrastructure:** Docker & Docker Compose

## Quick Start

1. **Start Oracle Database Container:**
   ```bash
   docker compose up -d
   ```
2. **Initialize Security & Schema:**
   Run `database/01_security_setup.sql` as `system` and `database/02_schema_tables.sql` as `english_admin`.
3. **Run the Spring Boot API:**
   ```bash
   cd backend
   ./mvnw spring-boot:run