-- Optional DBMS-course schema reference. Spring JPA creates the tables at startup.
CREATE TABLE IF NOT EXISTS users (id BIGSERIAL PRIMARY KEY, username VARCHAR(50) NOT NULL UNIQUE, password_hash VARCHAR(100) NOT NULL, role VARCHAR(20) NOT NULL CHECK (role IN ('STUDENT','STAFF','ADMIN')));
CREATE TABLE IF NOT EXISTS categories (id BIGSERIAL PRIMARY KEY, name VARCHAR(100) NOT NULL UNIQUE, description VARCHAR(500));
CREATE TABLE IF NOT EXISTS reports (id BIGSERIAL PRIMARY KEY, event_type VARCHAR(100) NOT NULL, description VARCHAR(4000) NOT NULL, loc VARCHAR(200) NOT NULL, date_time TIMESTAMP NOT NULL, status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING','IN_PROGRESS','RESOLVED','REJECTED')), category_id BIGINT REFERENCES categories(id), created_by_id BIGINT REFERENCES users(id), assigned_staff_id BIGINT REFERENCES users(id), created_at TIMESTAMP NOT NULL, updated_at TIMESTAMP NOT NULL);
CREATE TABLE IF NOT EXISTS comments (id BIGSERIAL PRIMARY KEY, report_id BIGINT NOT NULL REFERENCES reports(id) ON DELETE CASCADE, user_id BIGINT NOT NULL REFERENCES users(id), message VARCHAR(3000) NOT NULL, created_at TIMESTAMP NOT NULL);
CREATE TABLE IF NOT EXISTS report_history (id BIGSERIAL PRIMARY KEY, report_id BIGINT NOT NULL REFERENCES reports(id) ON DELETE CASCADE, changed_by_id BIGINT REFERENCES users(id), old_status VARCHAR(20), new_status VARCHAR(20), note VARCHAR(1000), changed_at TIMESTAMP NOT NULL);
CREATE INDEX IF NOT EXISTS idx_reports_status ON reports(status);
CREATE INDEX IF NOT EXISTS idx_reports_date_time ON reports(date_time);
