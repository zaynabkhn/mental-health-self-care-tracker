package com.mhtracker.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static final String URL = "jdbc:sqlite:sqlite.db";
    private static Connection testConnection = null;

    public static void useTestConnection(Connection conn) 
    {
        testConnection = conn;
    }

    public static Connection getConnection() throws SQLException {
        if (testConnection != null) {
            return testConnection;
        }
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

        //Creates a mood entries table.
        String sqlMoodEntries = """
            CREATE TABLE IF NOT EXISTS mood_entries (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL,
                mood TEXT NOT NULL,
                note TEXT,
                timestamp TEXT NOT NULL,
                FOREIGN KEY (username) REFERENCES users(username)
            );
        """;

        //Try-catch block editted to work with test cases.
        try 
        {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();

            stmt.execute(sqlUsers);
            stmt.execute("""
                INSERT OR IGNORE INTO users (username, password)
                VALUES ('admin', 'password');
            """);
            stmt.execute(sqlMoodEntries);

            //Only close the connection if it's NOT the test DB
            if (testConnection == null) 
            {
                stmt.close();
                conn.close();
            }

        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }
}
