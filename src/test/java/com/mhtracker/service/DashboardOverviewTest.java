package com.mhtracker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mhtracker.model.DashboardSummary;
import com.mhtracker.model.Database;
import com.mhtracker.model.Habit;
import com.mhtracker.model.HabitDAO;
import com.mhtracker.model.HabitLog;
import com.mhtracker.model.HabitLogDAO;
import com.mhtracker.model.HabitType;
import com.mhtracker.model.MoodEntry;
import com.mhtracker.model.MoodEntryDAO;

public class DashboardOverviewTest {

    private DashboardService dashboardService;

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

        dashboardService = new DashboardService();
    }

    @Test
    public void testDashboardSummaryWithMoodAndHabitData() {
        MoodEntry mood1 = new MoodEntry("Happy", "Feeling good", LocalDateTime.now().minusDays(1));
        mood1.setUsername("testuser");
        MoodEntryDAO.insert(mood1);

        MoodEntry mood2 = new MoodEntry("Anxious", "Busy day", LocalDateTime.now());
        mood2.setUsername("testuser");
        MoodEntryDAO.insert(mood2);

        MoodEntry mood3 = new MoodEntry("Anxious", "A little stressed", LocalDateTime.now().minusHours(2));
        mood3.setUsername("testuser");
        MoodEntryDAO.insert(mood3);

        Habit habit = new Habit(
                "Drink Water",
                "Stay hydrated",
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

        DashboardSummary summary = dashboardService.getDashboardSummary("testuser");

        assertNotNull(summary);
        assertEquals(3, summary.getTotalMoodEntries());
        assertEquals("Anxious", summary.getMostFrequentMood());
        assertEquals(1, summary.getTotalHabitCompletions());
        assertFalse(summary.getRecentMoodEntries().isEmpty());
    }

    @Test
    public void testDashboardSummaryWithNoData() {
        DashboardSummary summary = dashboardService.getDashboardSummary("testuser");

        assertNotNull(summary);
        assertEquals(0, summary.getTotalMoodEntries());
        assertEquals("No Data", summary.getMostFrequentMood());
        assertEquals(0, summary.getTotalHabitCompletions());
        assertEquals(0, summary.getRecentMoodEntries().size());
    }
}