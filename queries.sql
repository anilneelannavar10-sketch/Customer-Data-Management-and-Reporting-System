-- ============================================================
-- Customer Data Management System - Reporting Queries
-- ============================================================
USE customer_management;

-- 1. Retrieve all active customers
SELECT customer_id, first_name, last_name, email, city
FROM customers
WHERE status = 'Active'
ORDER BY last_name;

-- 2. Filter customers by city
SELECT * FROM customers WHERE city = 'Chicago';

-- 3. Search customers by name (partial match, used by the search bar)
SELECT * FROM customers
WHERE first_name LIKE CONCAT('%', 'jo', '%')
   OR last_name LIKE CONCAT('%', 'jo', '%')
   OR email LIKE CONCAT('%', 'jo', '%');

-- 4. Update a customer's status
UPDATE customers SET status = 'Inactive' WHERE customer_id = 3;

-- 5. Summary report: customer count by city
SELECT city, COUNT(*) AS total_customers
FROM customers
GROUP BY city
ORDER BY total_customers DESC;

-- 6. Summary report: active vs inactive customer counts
SELECT status, COUNT(*) AS total
FROM customers
GROUP BY status;

-- 7. Recently added customers (last 30 days)
SELECT first_name, last_name, email, created_at
FROM customers
WHERE created_at >= (CURRENT_DATE - INTERVAL 30 DAY)
ORDER BY created_at DESC;

-- 8. Customers added per month (trend report)
SELECT DATE_FORMAT(created_at, '%Y-%m') AS month, COUNT(*) AS new_customers
FROM customers
GROUP BY DATE_FORMAT(created_at, '%Y-%m')
ORDER BY month;
