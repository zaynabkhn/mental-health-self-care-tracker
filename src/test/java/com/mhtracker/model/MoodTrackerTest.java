package com.mhtracker.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MoodTrackerTest
{
    //This makes a test database to  use specifically for testing.
    //Don't want to interfere with teh actual app.
    @BeforeEach
    public void setup() throws SQLException 
    {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:file:memdb1?mode=memory&cache=shared");
        Database.useTestConnection(conn);
        conn.createStatement().execute("PRAGMA foreign_keys = ON;");
        //This creates tables in memory.
        Database.initialize(); 

        //Clear users table so testuser can be inserted.
        conn.createStatement().execute("DELETE FROM mood_entries;");
        conn.createStatement().execute("DELETE FROM users;");

        //Add the test user so foreign key checks pass.
        conn.createStatement().execute("INSERT INTO users (username, password) VALUES ('testuser', 'pw');");
    }

    //MoodEntry Feature Tests
    //Test inserting a mood entry.
    @Test
    public void testInsertMoodEntry() 
    {
        MoodEntry entry = new MoodEntry("testuser", "Happy", "Feeling good");
        MoodEntryDAO.insert(entry);

        List<MoodEntry> entries = MoodEntryDAO.getEntriesForUser("testuser");

        assertEquals(1, entries.size());
        assertEquals("Happy", entries.get(0).getMood());
        assertEquals("Feeling good", entries.get(0).getNote());
    }

    //Tests the 1-entry-per-day rule.
    @Test
    public void testOneEntryPerDay() 
    {
        MoodEntry entry1 = new MoodEntry("testuser", "Happy", "First entry");
        MoodEntryDAO.insert(entry1);

        assertTrue(MoodEntryDAO.hasEntryForToday("testuser"));

        MoodEntry entry2 = new MoodEntry("testuser", "Sad", "Second entry");
        MoodEntryDAO.insert(entry2);

        // Should still only count 1 entry for today
        assertTrue(MoodEntryDAO.hasEntryForToday("testuser"));

        List<MoodEntry> entries = MoodEntryDAO.getEntriesForUser("testuser");
        assertEquals(2, entries.size()); // Both exist, but rule blocks UI
    }

    //Test timestamp formatting (needed for the 1 entry rule).
    @Test
    public void testTimestampFormat() 
    {
        MoodEntry entry = new MoodEntry("testuser", "Calm", "Relaxing");
        MoodEntryDAO.insert(entry);

        List<MoodEntry> entries = MoodEntryDAO.getEntriesForUser("testuser");
        String ts = entries.get(0).getTimestampString();

        assertTrue(ts.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
    }

    //Test loading entries preserves timestamp.
    @Test
    public void testLoadTimestampMatchesDatabase() 
    {
        MoodEntry entry = new MoodEntry("testuser", "Tired", "Long day");
        MoodEntryDAO.insert(entry);

        List<MoodEntry> entries = MoodEntryDAO.getEntriesForUser("testuser");

        LocalDateTime original = entry.getTimestamp();
        LocalDateTime loaded = entries.get(0).getTimestamp();

        assertEquals(original.getYear(), loaded.getYear());
        assertEquals(original.getMonth(), loaded.getMonth());
        assertEquals(original.getDayOfMonth(), loaded.getDayOfMonth());
    }

    //Test clearing the mood table.
    @Test
    public void testClearAll() {
        MoodEntryDAO.insert(new MoodEntry("testuser", "Happy", "Test"));
        MoodEntryDAO.clearAll();

        List<MoodEntry> entries = MoodEntryDAO.getEntriesForUser("testuser");
        assertEquals(0, entries.size());
    }
}