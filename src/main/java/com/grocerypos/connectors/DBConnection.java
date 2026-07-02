package com.grocerypos.connectors;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    private static final String URL =
            "jdbc:postgresql://localhost:5433/grocery_store_db";

    private static final Properties fileProps = loadPropertiesFile();

    private static final String USER =
            firstNonBlank(System.getenv("GROCERY_DB_USER"), fileProps.getProperty("db.user"), "postgres");

    private static final String PASSWORD =
            firstNonBlank(System.getenv("GROCERY_DB_PASSWORD"), fileProps.getProperty("db.password"), null);

    public static Connection getConnection() throws SQLException {
        if (PASSWORD == null) {
            throw new SQLException(
                    "No DB password found. Set db.password in db.properties (project root) " +
                            "or the GROCERY_DB_PASSWORD environment variable."
            );
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static Properties loadPropertiesFile() {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream("db.properties")) {
            props.load(in);
        } catch (IOException e) {
            // File is optional — env vars can be used instead. No error here.
        }
        return props;
    }

    private static String firstNonBlank(String... values) {
        for (String v : values) {
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }
}