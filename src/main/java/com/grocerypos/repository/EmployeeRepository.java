package com.grocerypos.repository;

import com.grocerypos.connectors.DBConnection;
import com.grocerypos.model.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepository {

    public List<Employee> findAll() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT user_id, name, first_name, scan_rate, password, status, test_user FROM employees";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                employees.add(new Employee(
                        rs.getString("name"),
                        rs.getString("first_name"),
                        rs.getDouble("scan_rate"),
                        rs.getString("user_id"),
                        rs.getString("password"),
                        rs.getString("status"),
                        rs.getBoolean("test_user")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Failed to load employees from database: " + e.getMessage());
            e.printStackTrace();
        }

        return employees;
    }
}