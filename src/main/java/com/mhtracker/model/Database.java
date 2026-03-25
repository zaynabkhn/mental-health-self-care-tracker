package com.mhtracker.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static final String URL = "jdbc:sqlite:sqlite.db";
    private static Connection testConnection = null;

    public static void useTestConnection(Connection conn) {
        testConnection = conn;
    }

    public static boolean isUsingTestConnection() {
        return testConnection != null;
    }

    public static Connection getConnection() throws SQLException {
        if (testConnection != null) {
            return testConnection;
        }
        return DriverManager.getConnection(URL);
    }

    public static void initialize() {

        String sqlUsers = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL
            );
        """;

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

        String sqlHabits = """
            CREATE TABLE IF NOT EXISTS habits (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL,
                name TEXT NOT NULL,
                description TEXT,
                type TEXT NOT NULL,
                target_value REAL DEFAULT 0,
                unit TEXT,
                category TEXT,
                weekly_goal INTEGER DEFAULT 0,
                created_date TEXT NOT NULL,
                FOREIGN KEY (username) REFERENCES users(username)
            );
        """;

        String sqlHabitLogs = """
            CREATE TABLE IF NOT EXISTS habit_logs (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                habit_id INTEGER NOT NULL,
                username TEXT NOT NULL,
                log_date TEXT NOT NULL,
                value REAL DEFAULT 1,
                completed INTEGER NOT NULL DEFAULT 1,
                UNIQUE(habit_id, log_date),
                FOREIGN KEY (habit_id) REFERENCES habits(id),
                FOREIGN KEY (username) REFERENCES users(username)
            );
        """;

        String sqlJournalEntries = """
            CREATE TABLE IF NOT EXISTS journal_entries (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL,
                content TEXT NOT NULL,
                timestamp TEXT NOT NULL,
                FOREIGN KEY (username) REFERENCES users(username)
            );
        """;

        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();

            stmt.execute(sqlUsers);

            stmt.execute("""
                INSERT OR IGNORE INTO users (username, password)
                VALUES ('admin', 'password');
            """);

            stmt.execute(sqlMoodEntries);
            stmt.execute(sqlHabits);
            stmt.execute(sqlHabitLogs);
            stmt.execute(sqlJournalEntries);

            if (!isUsingTestConnection()) {
                stmt.close();
                conn.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}