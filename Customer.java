package com.customerapp.model;

/**
 * Represents a customer record.
 */
public class Customer {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String status;
    private String createdAt;

    public Customer() {
    }

    public Customer(int id, String firstName, String lastName, String email,
                     String phone, String address, String city, String status, String createdAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    /** Serializes this customer to a JSON object string. */
    public String toJson() {
        return "{"
                + "\"id\":" + id + ","
                + "\"firstName\":\"" + esc(firstName) + "\","
                + "\"lastName\":\"" + esc(lastName) + "\","
                + "\"email\":\"" + esc(email) + "\","
                + "\"phone\":\"" + esc(phone) + "\","
                + "\"address\":\"" + esc(address) + "\","
                + "\"city\":\"" + esc(city) + "\","
                + "\"status\":\"" + esc(status) + "\","
                + "\"createdAt\":\"" + esc(createdAt) + "\""
                + "}";
    }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
