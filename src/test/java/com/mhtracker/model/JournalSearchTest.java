package com.mhtracker.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JournalSearchTest {

    @BeforeEach
    public void setup() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:file:memdb_journalsearch?mode=memory&cache=shared");
        Database.useTestConnection(conn);
        conn.createStatement().execute("PRAGMA foreign_keys = ON;");
        Database.initialize();

        conn.createStatement().execute("DELETE FROM journal_entries;");
        conn.createStatement().execute("DELETE FROM users;");

        conn.createStatement().execute("INSERT INTO users (username, password) VALUES ('testuser', 'pw');");
    }

    @Test
    public void testSearchEntriesByKeyword() {
        JournalEntryDAO.insertEntry(new JournalEntry("testuser", "happy thoughts are the best"));
        JournalEntryDAO.insertEntry(new JournalEntry("testuser", "looks like its rainy"));
        JournalEntryDAO.insertEntry(new JournalEntry("testuser", "i worked on my group project"));

        List<JournalEntry> results = JournalEntryDAO.searchEntries("testuser", "best");

        assertEquals(1, results.size());
        assertEquals("happy thoughts are the best", results.get(0).getContent());
    }

    @Test
    public void testSearchEntriesNoMatch() {
        JournalEntryDAO.insertEntry(new JournalEntry("testuser", "happy thoughts are the best"));
        JournalEntryDAO.insertEntry(new JournalEntry("testuser", "looks like its rainy"));

        List<JournalEntry> results = JournalEntryDAO.searchEntries("testuser", "banana");

        assertTrue(results.isEmpty());
    }
}