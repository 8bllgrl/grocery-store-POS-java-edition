package com.grocerypos.repository;

import com.grocerypos.connectors.DBConnection;
import com.grocerypos.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {

    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT barcode, name, unit_price FROM products";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                products.add(new Product(
                        rs.getString("barcode"),
                        rs.getString("name"),
                        rs.getBigDecimal("unit_price")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Failed to load products from database: " + e.getMessage());
            e.printStackTrace();
        }

        return products;
    }
}