# Digital Crime Reporting System

Starter project built with Java 17, Spring Boot, Spring Data JPA, PostgreSQL, and plain HTML/CSS/JavaScript.

## Features
- Citizen registration and login (passwords are BCrypt-hashed).
- Session-based authentication.
- Citizens can create and view their own reports.
- Police/admin roles can view all reports and update status.
- Admin role can add an FIR number to a report.
- Dashboard totals and status filter.
- The public registration endpoint always creates a `CITIZEN`; users cannot choose a staff role from the registration form.

## 1. Requirements
- Java 17 or newer
- Maven 3.9+
- PostgreSQL

## 2. Create the database
Open pgAdmin Query Tool or `psql` and run:

```sql
CREATE DATABASE digital_reporting_db;
```

If the database already exists, do not run that command again.

## 3. Configure the database password
Edit `src/main/resources/application.properties` or set environment variables.

PowerShell:
```powershell
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="YOUR_REAL_POSTGRES_PASSWORD"
mvn spring-boot:run
```

macOS/Linux:
```bash
export DB_USERNAME=postgres
export DB_PASSWORD='YOUR_REAL_POSTGRES_PASSWORD'
mvn spring-boot:run
```

Default connection URL: `jdbc:postgresql://localhost:5432/digital_reporting_db`.

## 4. Start the application
Run from the folder containing `pom.xml`:
```bash
mvn clean spring-boot:run
```
Open http://localhost:8080

Hibernate creates/updates the tables for this starter project. Do not use `ddl-auto=update` as a production migration strategy.

## 5. Create a staff/admin account
For safety, public registration only creates `CITIZEN` accounts. After registering a citizen account and stopping the app, use pgAdmin/psql to promote a trusted account:

```sql
-- Replace citizen_username with the exact username you registered.
UPDATE app_users SET role = 'POLICE' WHERE username = 'citizen_username';

-- Or, for an administrator account:
UPDATE app_users SET role = 'ADMIN' WHERE username = 'citizen_username';

SELECT id, username, email, role FROM app_users;
```
Log out and log in again after changing the role. Never give ADMIN access to untrusted users.

## API endpoints
- `POST /api/auth/register` JSON: `{"username":"citizen1","email":"citizen@example.com","password":"StrongPass123"}`
- `POST /api/auth/login` JSON: `{"username":"citizen1","password":"StrongPass123"}`
- `POST /api/auth/logout`
- `GET /api/auth/me`
- `GET /api/dashboard`
- `POST /api/reports` JSON: `{"eventType":"Theft","description":"Description here","loc":"Area, city","dateTime":"2026-10-08T14:30:00"}`
- `GET /api/reports`
- `GET /api/reports?status=PENDING`
- `GET /api/reports/{id}`
- `PATCH /api/reports/{id}/status` for police/admin JSON: `{"status":"INVESTIGATING"}`
- Admin can also include `firNumber`: `{"status":"INVESTIGATING","firNumber":"FIR-EXAMPLE"}`

## Suggested SQL practice
```sql
-- SELECT and ORDER BY
SELECT id, event_type, status, loc, date_time FROM reports ORDER BY created_at DESC;

-- WHERE
SELECT * FROM reports WHERE status = 'PENDING';

-- GROUP BY and COUNT
SELECT status, COUNT(*) AS total FROM reports GROUP BY status;

-- HAVING
SELECT event_type, COUNT(*) AS total
FROM reports GROUP BY event_type HAVING COUNT(*) > 1;

-- JOIN
SELECT r.id, r.event_type, r.status, u.username
FROM reports r JOIN app_users u ON r.reporter_id = u.id;

-- UPDATE (example)
UPDATE reports SET status = 'UNDER_REVIEW' WHERE id = 1;

-- DELETE (test database only; there is no delete endpoint in this starter)
DELETE FROM reports WHERE id = 1;
```

## Important security and operational notes
This is an educational starter, not a production police case-management platform. Before public deployment, add CSRF protection appropriate to session cookies, HTTPS, rate limiting, email verification, audit/history records, secure evidence uploads, access reviews, retention policies, backups, monitoring, and security testing. Do not upload sensitive evidence or real victim information to an unreviewed demo. Only authorized police personnel should determine official FIR registration. Do not rely on this site for emergency response; in India, call 112 for immediate danger.
