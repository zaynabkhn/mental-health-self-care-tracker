package com.mhtracker.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JournalEntryTest {

    @BeforeEach
    public void setup() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:file:memdb_journal?mode=memory&cache=shared");
        Database.useTestConnection(conn);
        conn.createStatement().execute("PRAGMA foreign_keys = ON;");
        Database.initialize();

        conn.createStatement().execute("DELETE FROM journal_entries;");
        conn.createStatement().execute("DELETE FROM users;");

        conn.createStatement().execute("INSERT INTO users (username, password) VALUES ('testuser', 'pw');");
    }

    @Test
    public void testInsertAndRetrieveJournalEntry() {
        JournalEntry entry = new JournalEntry("testuser", "Today was a good day.");
        JournalEntryDAO.insertEntry(entry);

        List<JournalEntry> entries = JournalEntryDAO.getEntriesForUser("testuser");

        assertEquals(1, entries.size());
        assertEquals("testuser", entries.get(0).getUsername());
        assertEquals("Today was a good day.", entries.get(0).getContent());
        assertNotNull(entries.get(0).getTimestamp());
    }

    @Test
    public void testMultipleJournalEntriesAreRetrieved() {
        JournalEntryDAO.insertEntry(new JournalEntry("testuser", "First entry"));
        JournalEntryDAO.insertEntry(new JournalEntry("testuser", "Second entry"));

        List<JournalEntry> entries = JournalEntryDAO.getEntriesForUser("testuser");

        assertEquals(2, entries.size());

        boolean foundFirst = entries.stream()
                .anyMatch(entry -> entry.getContent().equals("First entry"));

        boolean foundSecond = entries.stream()
                .anyMatch(entry -> entry.getContent().equals("Second entry"));

        assertTrue(foundFirst);
        assertTrue(foundSecond);
    }
}