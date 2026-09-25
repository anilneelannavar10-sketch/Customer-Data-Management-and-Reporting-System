-- ============================================================
-- Customer Data Management System - Database Schema
-- ============================================================

CREATE DATABASE IF NOT EXISTS customer_management;
USE customer_management;

DROP TABLE IF EXISTS customers;

CREATE TABLE customers (
    customer_id   INT AUTO_INCREMENT PRIMARY KEY,
    first_name    VARCHAR(50)  NOT NULL,
    last_name     VARCHAR(50)  NOT NULL,
    email         VARCHAR(100) NOT NULL UNIQUE,
    phone         VARCHAR(20),
    address       VARCHAR(200),
    city          VARCHAR(50),
    status        ENUM('Active', 'Inactive') DEFAULT 'Active',
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_email ON customers(email);
CREATE INDEX idx_last_name ON customers(last_name);
CREATE INDEX idx_city ON customers(city);

-- Sample seed data
INSERT INTO customers (first_name, last_name, email, phone, address, city, status) VALUES
('John', 'Smith', 'john.smith@email.com', '555-0101', '12 Elm St', 'Chicago', 'Active'),
('Jane', 'Doe', 'jane.doe@email.com', '555-0102', '45 Oak Ave', 'Denver', 'Active'),
('Mike', 'Johnson', 'mike.johnson@email.com', '555-0103', '78 Pine Rd', 'Chicago', 'Inactive'),
('Sara', 'Lee', 'sara.lee@email.com', '555-0104', '90 Maple Dr', 'Austin', 'Active'),
('Tom', 'Brown', 'tom.brown@email.com', '555-0105', '23 Cedar Ln', 'Denver', 'Active');
