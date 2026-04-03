package com.mhtracker.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JournalSearchTest 
{

    private static final String DB_URL = "jdbc:sqlite:file:memdb_journalsearch?mode=memory&cache=shared";

    @BeforeEach
    public void setup() throws SQLException 
    {
        Connection conn = DriverManager.getConnection(DB_URL);
        try (Statement stmt = conn.createStatement()) 
        {
            Database.useTestConnection(conn);
            stmt.execute("PRAGMA foreign_keys = ON;");
            Database.initialize();

            //To make sure each test starts cleared.
            stmt.execute("DELETE FROM journal_entries;");
            stmt.execute("DELETE FROM users;");

            stmt.execute("INSERT INTO users (username, password) VALUES ('testuser', 'pw');");
            stmt.execute("INSERT INTO users (username, password) VALUES ('otheruser', 'pw');");
        } 
        finally 
        {
            // Do not close the connection if Database.useTestConnection expects it open.
            // We leave it open to preserve the shared in-memory DB for the test.
        }
    }

    @Test
    public void testSearchEntriesByKeyword() 
    {
        JournalEntryDAO.insertEntry(new JournalEntry("testuser", "happy thoughts are the best"));
        JournalEntryDAO.insertEntry(new JournalEntry("testuser", "looks like its rainy"));
        JournalEntryDAO.insertEntry(new JournalEntry("testuser", "i worked on my group project"));

        List<JournalEntry> results = JournalEntryDAO.searchEntries("testuser", "best");

        assertEquals(1, results.size(), "Should find exactly one entry containing 'best'");
        assertEquals("happy thoughts are the best", results.get(0).getContent());
    }

    @Test
    public void testSearchEntriesNoMatch() 
    {
        JournalEntryDAO.insertEntry(new JournalEntry("testuser", "happy thoughts are the best"));
        JournalEntryDAO.insertEntry(new JournalEntry("testuser", "looks like its rainy"));

        List<JournalEntry> results = JournalEntryDAO.searchEntries("testuser", "banana");

        assertTrue(results.isEmpty(), "No entries should match the keyword 'banana'");
    }

    @Test
    public void testSearchEntriesMultipleMatches() 
    {
        JournalEntryDAO.insertEntry(new JournalEntry("testuser", "best day ever"));
        JournalEntryDAO.insertEntry(new JournalEntry("testuser", "the best coffee"));
        JournalEntryDAO.insertEntry(new JournalEntry("testuser", "ordinary day"));

        List<JournalEntry> results = JournalEntryDAO.searchEntries("testuser", "best");

        assertEquals(2, results.size(), "Should find two entries containing 'best'");

        //Ensure both expected contents are present (order not needed)
        assertTrue(results.stream().anyMatch(e -> e.getContent().equals("best day ever")));
        assertTrue(results.stream().anyMatch(e -> e.getContent().equals("the best coffee")));
    }

    @Test
    public void testSearchEntriesCaseInsensitive() 
    {
        JournalEntryDAO.insertEntry(new JournalEntry("testuser", "I love Best practices"));
        JournalEntryDAO.insertEntry(new JournalEntry("testuser", "nothing relevant"));

        List<JournalEntry> resultsLower = JournalEntryDAO.searchEntries("testuser", "best");
        List<JournalEntry> resultsUpper = JournalEntryDAO.searchEntries("testuser", "BEST");

        assertEquals(1, resultsLower.size(), "Lowercase search should match");
        assertEquals(1, resultsUpper.size(), "Uppercase search should match (case-insensitive)");
        assertEquals("I love Best practices", resultsLower.get(0).getContent());
    }

    @Test
    public void testSearchEntriesUserIsolation() 
    {
        JournalEntryDAO.insertEntry(new JournalEntry("testuser", "shared keyword here"));
        JournalEntryDAO.insertEntry(new JournalEntry("otheruser", "shared keyword here"));

        List<JournalEntry> resultsTestUser = JournalEntryDAO.searchEntries("testuser", "shared");
        List<JournalEntry> resultsOtherUser = JournalEntryDAO.searchEntries("otheruser", "shared");

        assertEquals(1, resultsTestUser.size(), "testuser should only see their own entries");
        assertEquals(1, resultsOtherUser.size(), "otheruser should only see their own entries");
        assertEquals("shared keyword here", resultsTestUser.get(0).getContent());
        assertEquals("shared keyword here", resultsOtherUser.get(0).getContent());
    }

    @Test
    public void testSearchEntriesEmptyKeywordReturnsAllForUser() 
    {
        //Assumption: empty keyword returns all entries for the user
        JournalEntryDAO.insertEntry(new JournalEntry("testuser", "entry one"));
        JournalEntryDAO.insertEntry(new JournalEntry("testuser", "entry two"));
        JournalEntryDAO.insertEntry(new JournalEntry("otheruser", "other entry"));

        List<JournalEntry> results = JournalEntryDAO.searchEntries("testuser", "");

        assertEquals(2, results.size(), "Empty keyword should return all entries for the user");
        assertTrue(results.stream().anyMatch(e -> e.getContent().equals("entry one")));
        assertTrue(results.stream().anyMatch(e -> e.getContent().equals("entry two")));
    }

    @Test
    public void testCreateMultipleEntries() {
        // Insert multiple entries for testuser
        JournalEntryDAO.insertEntry(new JournalEntry("testuser", "first entry"));
        JournalEntryDAO.insertEntry(new JournalEntry("testuser", "second entry"));
        JournalEntryDAO.insertEntry(new JournalEntry("testuser", "third entry"));

        // Insert one for another user to ensure isolation
        JournalEntryDAO.insertEntry(new JournalEntry("otheruser", "other user's entry"));

        // Fetch and assert
        List<JournalEntry> entries = JournalEntryDAO.getEntriesForUser("testuser");
        assertEquals(3, entries.size(), "Should return three entries for testuser");

        // Verify contents are present
        assertTrue(entries.stream().anyMatch(e -> e.getContent().equals("first entry")));
        assertTrue(entries.stream().anyMatch(e -> e.getContent().equals("second entry")));
        assertTrue(entries.stream().anyMatch(e -> e.getContent().equals("third entry")));
    }

    @Test
    public void testSearchEntriesWhitespaceKeywordReturnsAllForUser() {
        // Insert entries for testuser and another user
        JournalEntryDAO.insertEntry(new JournalEntry("testuser", "alpha entry"));
        JournalEntryDAO.insertEntry(new JournalEntry("testuser", "beta entry"));
        JournalEntryDAO.insertEntry(new JournalEntry("otheruser", "other entry"));

        // Search using a whitespace-only keyword (should be treated as empty and return all for user)
        List<JournalEntry> results = JournalEntryDAO.searchEntries("testuser", "   ");

        assertEquals(2, results.size(), "Whitespace-only keyword should return all entries for the user");
        assertTrue(results.stream().anyMatch(e -> e.getContent().equals("alpha entry")));
        assertTrue(results.stream().anyMatch(e -> e.getContent().equals("beta entry")));
    }
}
