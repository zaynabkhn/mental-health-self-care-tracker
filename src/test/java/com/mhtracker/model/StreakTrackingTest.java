package com.mhtracker.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StreakTrackingTest {

    @BeforeEach
    public void setup() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:file:memdb_streak?mode=memory&cache=shared");
        Database.useTestConnection(conn);
        conn.createStatement().execute("PRAGMA foreign_keys = ON;");
        Database.initialize();

        conn.createStatement().execute("DELETE FROM habit_logs;");
        conn.createStatement().execute("DELETE FROM habits;");
        conn.createStatement().execute("DELETE FROM users;");

        conn.createStatement().execute("INSERT INTO users (username, password) VALUES ('testuser', 'pw');");
    }

    @Test
    public void testStreakForConsecutiveDays() {
        Habit habit = new Habit(
                "Meditate",
                "Meditate daily",
                HabitType.BOOLEAN,
                0,
                "",
                "Mental"
        );
        HabitDAO.insertHabit("testuser", habit);

        HabitLogDAO.insertLog(new HabitLog(habit.getId(), "testuser", LocalDate.now(), 1, true));
        HabitLogDAO.insertLog(new HabitLog(habit.getId(), "testuser", LocalDate.now().minusDays(1), 1, true));
        HabitLogDAO.insertLog(new HabitLog(habit.getId(), "testuser", LocalDate.now().minusDays(2), 1, true));

        int streak = HabitLogDAO.getStreak(habit.getId(), "testuser");

        assertEquals(3, streak);
    }

    @Test
    public void testStreakResetsWhenDayIsMissed() {
        Habit habit = new Habit(
                "Read",
                "Read every day",
                HabitType.BOOLEAN,
                0,
                "",
                "Learning"
        );
        HabitDAO.insertHabit("testuser", habit);

        HabitLogDAO.insertLog(new HabitLog(habit.getId(), "testuser", LocalDate.now(), 1, true));
        HabitLogDAO.insertLog(new HabitLog(habit.getId(), "testuser", LocalDate.now().minusDays(2), 1, true));

        int streak = HabitLogDAO.getStreak(habit.getId(), "testuser");

        assertEquals(1, streak);
    }

    @Test
    public void testNoLogsMeansZeroStreak() {
        Habit habit = new Habit(
                "Exercise",
                "Exercise often",
                HabitType.BOOLEAN,
                0,
                "",
                "Fitness"
        );
        HabitDAO.insertHabit("testuser", habit);

        int streak = HabitLogDAO.getStreak(habit.getId(), "testuser");

        assertEquals(0, streak);
    }
}