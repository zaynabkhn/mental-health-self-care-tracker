package com.mhtracker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mhtracker.model.Database;
import com.mhtracker.model.Habit;
import com.mhtracker.model.HabitDAO;
import com.mhtracker.model.HabitLog;
import com.mhtracker.model.HabitLogDAO;
import com.mhtracker.model.HabitType;
import com.mhtracker.model.MoodEntry;
import com.mhtracker.model.MoodEntryDAO;

public class ReminderNotificationTest {

    private ReminderService reminderService;

    @BeforeEach
    public void setup() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:file:memdb1?mode=memory&cache=shared");
        Database.useTestConnection(conn);
        conn.createStatement().execute("PRAGMA foreign_keys = ON;");
        Database.initialize();

        conn.createStatement().execute("DELETE FROM mood_entries;");
        conn.createStatement().execute("DELETE FROM habit_logs;");
        conn.createStatement().execute("DELETE FROM habits;");
        conn.createStatement().execute("DELETE FROM users;");

        conn.createStatement().execute("INSERT INTO users (username, password) VALUES ('testuser', 'pw');");

        reminderService = new ReminderService();
    }

    @Test
    public void testReminderWhenMoodAndHabitMissing() {
        String message = reminderService.buildReminderMessage("testuser");

        assertEquals(
                "Reminder: You have not completed your mood check-in or logged any habits today.",
                message
        );
    }

    @Test
    public void testReminderWhenOnlyMoodMissing() {
        Habit habit = new Habit(
                "Drink Water",
                "Drink enough water",
                HabitType.BOOLEAN,
                1.0,
                "times",
                "Health"
        );

        HabitDAO.insertHabit("testuser", habit);

        HabitLog log = new HabitLog(
                habit.getId(),
                "testuser",
                LocalDate.now(),
                1.0,
                true
        );
        HabitLogDAO.insertLog(log);

        String message = reminderService.buildReminderMessage("testuser");

        assertEquals(
                "Reminder: You have not completed your mood check-in today.",
                message
        );
    }

    @Test
    public void testReminderWhenOnlyHabitMissing() {
        MoodEntryDAO.insert(new MoodEntry("testuser", "Happy", "Feeling good today"));

        String message = reminderService.buildReminderMessage("testuser");

        assertEquals(
                "Reminder: You have not logged any habits today.",
                message
        );
    }

    @Test
    public void testNoReminderWhenBothDone() {
        MoodEntryDAO.insert(new MoodEntry("testuser", "Calm", "All good"));

        Habit habit = new Habit(
                "Meditate",
                "Meditate daily",
                HabitType.BOOLEAN,
                1.0,
                "times",
                "Mental Health"
        );

        HabitDAO.insertHabit("testuser", habit);

        HabitLog log = new HabitLog(
                habit.getId(),
                "testuser",
                LocalDate.now(),
                1.0,
                true
        );
        HabitLogDAO.insertLog(log);

        String message = reminderService.buildReminderMessage("testuser");

        assertEquals("", message);
    }
}