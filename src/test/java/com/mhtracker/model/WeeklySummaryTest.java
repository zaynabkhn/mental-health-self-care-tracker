package com.mhtracker.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WeeklySummaryTest {

    @BeforeEach
    public void setup() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:file:memdb1?mode=memory&cache=shared");
        Database.useTestConnection(conn);
        conn.createStatement().execute("PRAGMA foreign_keys = ON;");
        Database.initialize();

        conn.createStatement().execute("DELETE FROM mood_entries;");
        conn.createStatement().execute("DELETE FROM habit_logs;");
        conn.createStatement().execute("DELETE FROM users;");
        conn.createStatement().execute("DELETE FROM habits;");

        conn.createStatement().execute("INSERT INTO users (username, password) VALUES ('testuser', 'pw');");
    }

    @Test
    public void testAddMoodEntriesAcrossMultipleDays() {
        MoodEntryDAO.insert(new MoodEntry("testuser", "Happy", "Today"));

        MoodEntry e1 = new MoodEntry("Sad", "Yesterday", LocalDateTime.now().minusDays(1));
        e1.setUsername("testuser");
        MoodEntryDAO.insert(e1);

        MoodEntry e2 = new MoodEntry("Calm", "5 days ago", LocalDateTime.now().minusDays(5));
        e2.setUsername("testuser");
        MoodEntryDAO.insert(e2);

        MoodEntry e3 = new MoodEntry("Angry", "10 days ago", LocalDateTime.now().minusDays(10));
        e3.setUsername("testuser");
        MoodEntryDAO.insert(e3);

        List<MoodEntry> entries = MoodEntryDAO.getEntriesForUser("testuser");

        assertEquals(4, entries.size());
        assertTrue(entries.stream().anyMatch(e -> e.getMood().equals("Happy")));
        assertTrue(entries.stream().anyMatch(e -> e.getMood().equals("Angry")));
    }

    @Test
    public void testWeeklySummaryFiltersToLast7Days() {
        MoodEntry e1 = new MoodEntry("Happy", "1 day", LocalDateTime.now().minusDays(1));
        e1.setUsername("testuser");
        MoodEntryDAO.insert(e1);

        MoodEntry e2 = new MoodEntry("Sad", "6 days", LocalDateTime.now().minusDays(6));
        e2.setUsername("testuser");
        MoodEntryDAO.insert(e2);

        MoodEntry e3 = new MoodEntry("Calm", "10 days", LocalDateTime.now().minusDays(10));
        e3.setUsername("testuser");
        MoodEntryDAO.insert(e3);

        List<MoodEntry> last7 = MoodEntryDAO.getEntriesForUserLast7Days("testuser");

        assertEquals(2, last7.size());
        assertFalse(last7.stream().anyMatch(e -> e.getMood().equals("Calm")));
    }

    @Test
    public void testAverageMoodCalculation() {
        MoodEntryDAO.insert(new MoodEntry("testuser", "Happy", "Today"));

        MoodEntry e1 = new MoodEntry("Sad", "Yesterday", LocalDateTime.now().minusDays(1));
        e1.setUsername("testuser");
        MoodEntryDAO.insert(e1);

        MoodEntry e2 = new MoodEntry("Calm", "2 days ago", LocalDateTime.now().minusDays(2));
        e2.setUsername("testuser");
        MoodEntryDAO.insert(e2);

        String avgMood = MoodEntryDAO.getWeeklyAverageMood("testuser");

        assertNotNull(avgMood);
        assertEquals("Tired", avgMood);
    }

    @Test
    public void testWeeklyHabitCompletionCount() {
        Habit habit = new Habit(
                "Test Habit",
                "A habit for testing",
                HabitType.BOOLEAN,
                1.0,
                "unit",
                "General"
        );
        habit.setWeeklyGoal(3);

        HabitDAO.insertHabit("testuser", habit);
        long habitId = habit.getId();

        HabitLog log1 = new HabitLog(
                habitId,
                "testuser",
                LocalDate.now(),
                0.0,
                true
        );
        HabitLogDAO.insertLog(log1);

        HabitLog log2 = new HabitLog(
                habitId,
                "testuser",
                LocalDate.now().minusDays(3),
                0.0,
                true
        );
        HabitLogDAO.insertLog(log2);

        HabitLog log3 = new HabitLog(
                habitId,
                "testuser",
                LocalDate.now().minusDays(8),
                0.0,
                true
        );
        HabitLogDAO.insertLog(log3);

        int count = HabitLogDAO.getWeeklyCompletionCount("testuser");

        assertEquals(2, count);
    }

    @Test
    public void testSummaryFormatting() {
        String avgMood = "Happy";
        int habitCount = 4;

        String summary = String.format(
                "In the past 7 days, your average mood was %s and you completed %d habits.",
                avgMood, habitCount
        );

        assertTrue(summary.contains("average mood was Happy"));
        assertTrue(summary.contains("completed 4 habits"));
        assertFalse(summary.isBlank());
    }
}