package com.mhtracker.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class HabitLogTest {

    @BeforeEach
    public void setup() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:file:memdb_habitlog?mode=memory&cache=shared");
        Database.useTestConnection(conn);
        conn.createStatement().execute("PRAGMA foreign_keys = ON;");
        Database.initialize();

        conn.createStatement().execute("DELETE FROM habit_logs;");
        conn.createStatement().execute("DELETE FROM habits;");
        conn.createStatement().execute("DELETE FROM users;");

        conn.createStatement().execute("INSERT INTO users (username, password) VALUES ('testuser', 'pw');");
    }

    @Test
    public void testInsertHabitLogAndCheckToday() {
        Habit habit = new Habit(
                "Drink Water",
                "Drink water daily",
                HabitType.BOOLEAN,
                0,
                "",
                "Health"
        );
        HabitDAO.insertHabit("testuser", habit);

        assertFalse(HabitLogDAO.hasLogForToday(habit.getId()));

        HabitLog log = new HabitLog(
                habit.getId(),
                "testuser",
                java.time.LocalDate.now(),
                1,
                true
        );

        HabitLogDAO.insertLog(log);

        assertTrue(HabitLogDAO.hasLogForToday(habit.getId()));
    }

    @Test
    public void testDuplicateHabitLogSameDayBlockedByDatabaseConstraint() throws SQLException {
        Habit habit = new Habit(
                "Meditate",
                "Meditate today",
                HabitType.BOOLEAN,
                0,
                "",
                "Mental"
        );
        HabitDAO.insertHabit("testuser", habit);

        HabitLog firstLog = new HabitLog(
                habit.getId(),
                "testuser",
                java.time.LocalDate.now(),
                1,
                true
        );

        HabitLogDAO.insertLog(firstLog);
        assertTrue(HabitLogDAO.hasLogForToday(habit.getId()));

        Connection conn = Database.getConnection();

        String sql = """
            INSERT INTO habit_logs (habit_id, username, log_date, value, completed)
            VALUES (?, ?, ?, ?, ?)
        """;

        boolean duplicateBlocked = false;

        try (var stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, habit.getId());
            stmt.setString(2, "testuser");
            stmt.setString(3, java.time.LocalDate.now().toString());
            stmt.setDouble(4, 1);
            stmt.setInt(5, 1);
            stmt.executeUpdate();
        } catch (SQLException e) {
            duplicateBlocked = true;
        }

        assertTrue(duplicateBlocked);
    }
}