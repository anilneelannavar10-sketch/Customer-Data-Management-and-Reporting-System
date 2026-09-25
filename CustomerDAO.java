package com.customerapp.dao;

import com.customerapp.db.DatabaseConnection;
import com.customerapp.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Data access object for customer records.
 * Handles CRUD operations, search, and validation using JDBC + SQL.
 */
public class CustomerDAO {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /** Validates required fields and email format before writing to the DB. */
    public void validate(Customer c) {
        if (c.getFirstName() == null || c.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required.");
        }
        if (c.getLastName() == null || c.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required.");
        }
        if (c.getEmail() == null || !EMAIL_PATTERN.matcher(c.getEmail()).matches()) {
            throw new IllegalArgumentException("A valid email is required.");
        }
    }

    /** Inserts a new customer record. Returns the generated customer id. */
    public int addCustomer(Customer c) throws SQLException {
        validate(c);
        String sql = "INSERT INTO customers (first_name, last_name, email, phone, address, city, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, c.getFirstName());
            stmt.setString(2, c.getLastName());
            stmt.setString(3, c.getEmail());
            stmt.setString(4, c.getPhone());
            stmt.setString(5, c.getAddress());
            stmt.setString(6, c.getCity());
            stmt.setString(7, c.getStatus() == null ? "Active" : c.getStatus());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    /** Updates an existing customer record by id. */
    public boolean updateCustomer(Customer c) throws SQLException {
        validate(c);
        String sql = "UPDATE customers SET first_name=?, last_name=?, email=?, phone=?, " +
                     "address=?, city=?, status=? WHERE customer_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getFirstName());
            stmt.setString(2, c.getLastName());
            stmt.setString(3, c.getEmail());
            stmt.setString(4, c.getPhone());
            stmt.setString(5, c.getAddress());
            stmt.setString(6, c.getCity());
            stmt.setString(7, c.getStatus());
            stmt.setInt(8, c.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    /** Deletes a customer record by id. */
    public boolean deleteCustomer(int id) throws SQLException {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /** Retrieves a single customer by id. */
    public Customer getCustomerById(int id) throws SQLException {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    /** Retrieves all customers, most recently created first. */
    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    /** Searches customers by first name, last name, email, or city (partial match). */
    public List<Customer> searchCustomers(String keyword) throws SQLException {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE first_name LIKE ? OR last_name LIKE ? " +
                     "OR email LIKE ? OR city LIKE ? ORDER BY last_name";
        String like = "%" + keyword + "%";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, like);
            stmt.setString(2, like);
            stmt.setString(3, like);
            stmt.setString(4, like);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    /** Builds a summary report: total customers, active/inactive counts, counts by city. */
    public String getSummaryReportJson() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            int total = 0, active = 0, inactive = 0;
            try (ResultSet rs = stmt.executeQuery("SELECT status, COUNT(*) c FROM customers GROUP BY status")) {
                while (rs.next()) {
                    int c = rs.getInt("c");
                    total += c;
                    if ("Active".equalsIgnoreCase(rs.getString("status"))) active = c;
                    else inactive = c;
                }
            }

            StringBuilder byCity = new StringBuilder("[");
            try (ResultSet rs = stmt.executeQuery(
                    "SELECT city, COUNT(*) c FROM customers GROUP BY city ORDER BY c DESC")) {
                boolean first = true;
                while (rs.next()) {
                    if (!first) byCity.append(",");
                    byCity.append("{\"city\":\"").append(rs.getString("city"))
                          .append("\",\"count\":").append(rs.getInt("c")).append("}");
                    first = false;
                }
            }
            byCity.append("]");

            return "{\"totalCustomers\":" + total + ","
                    + "\"active\":" + active + ","
                    + "\"inactive\":" + inactive + ","
                    + "\"byCity\":" + byCity + "}";
        }
    }

    private Customer mapRow(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getInt("customer_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("address"),
                rs.getString("city"),
                rs.getString("status"),
                String.valueOf(rs.getTimestamp("created_at"))
        );
    }
}
