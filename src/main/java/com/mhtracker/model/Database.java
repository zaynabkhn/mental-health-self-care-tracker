package com.mhtracker.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static final String URL = "jdbc:sqlite:sqlite.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initialize() {
        //Creates a table of users.
        String sqlUsers = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL
            );
        """;

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement())
        {
            System.out.println("Connected to: " + conn.getMetaData().getURL());
            stmt.execute(sqlUsers);
            stmt.execute("""
                INSERT OR IGNORE INTO users (username, password)
                VALUES ('admin', 'password');
            """);

            System.out.println("Database initialized.");
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }
}
