-- Run against the schema after inserting sample rows.
-- INSERT
INSERT INTO categories(name, description) VALUES ('Maintenance','Facilities and equipment') ON CONFLICT(name) DO NOTHING;
-- SELECT + WHERE + ORDER BY
SELECT id, event_type, description, status, date_time FROM reports WHERE status='PENDING' ORDER BY date_time DESC;
-- UPDATE
UPDATE reports SET status='IN_PROGRESS', updated_at=CURRENT_TIMESTAMP WHERE id=1;
-- DELETE (only use with a test record)
DELETE FROM comments WHERE id=1;
-- JOIN
SELECT r.id, r.event_type, u.username AS creator, c.name AS category, r.status FROM reports r LEFT JOIN users u ON u.id=r.created_by_id LEFT JOIN categories c ON c.id=r.category_id ORDER BY r.created_at DESC;
-- GROUP BY + COUNT
SELECT status, COUNT(*) AS report_count FROM reports GROUP BY status;
-- HAVING
SELECT category_id, COUNT(*) AS report_count FROM reports GROUP BY category_id HAVING COUNT(*) > 5;
-- Aggregate functions
SELECT COUNT(*) AS total, MAX(date_time) AS latest_report FROM reports;
-- Subquery: reports newer than the average report timestamp (illustrative)
SELECT id, event_type, category_id FROM reports WHERE category_id IN (SELECT category_id FROM reports GROUP BY category_id HAVING COUNT(*) > 1);
-- Foreign keys and constraints are defined in schema.sql.
