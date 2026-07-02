package com.grocerypos.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a single employee login record.
 *
 * Loaded from employees.json via Jackson, same pattern as Product/products.json.
 * Fields are intentionally simple for now (no permissions/tags yet â€” that's a
 * later step). userId is the numeric PIN but kept as a String so leading
 * zeros ("0007") are preserved instead of silently becoming 7.
 *
 * NOTE: password is stored PLAINTEXT for now. This is a placeholder â€”
 * encryption/hashing comes later once the basic login flow is working.
 */
public class Employee {
    private String name;
    private String firstName;
    private double scanRate;
    private String userId;
    private String password;
    private String status;
    private boolean testUser;

    // Jackson needs a no-arg constructor (or an @JsonCreator) to deserialize.
    // Fields are mutable (no `final`, has setters) because, unlike Product,
    // employee status/scanRate change at runtime (login/logout, future stats).
    @JsonCreator
    public Employee(
            @JsonProperty("name") String name,
            @JsonProperty("firstName") String firstName,
            @JsonProperty("scanRate") double scanRate,
            @JsonProperty("userId") String userId,
            @JsonProperty("password") String password,
            @JsonProperty("status") String status,
            @JsonProperty("testUser") boolean testUser) {
        this.name = name;
        this.firstName = firstName;
        this.scanRate = scanRate;
        this.userId = userId;
        this.password = password;
        this.status = status;
        this.testUser = testUser;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public double getScanRate() { return scanRate; }
    public void setScanRate(double scanRate) { this.scanRate = scanRate; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isTestUser() { return testUser; }
    public void setTestUser(boolean testUser) { this.testUser = testUser; }

    /** Convenience for UI greetings: "Test" + "User" -> "Test User". */
    public String getFullName() {
        return firstName + " " + name;
    }
}
