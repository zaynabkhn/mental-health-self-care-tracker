package com.mhtracker.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JournalEntryDAO 
{

    public static void insertEntry(JournalEntry entry) 
    {
        String sql = """
            INSERT INTO journal_entries (username, title, content, timestamp)
            VALUES (?, ?, ?, ?)
        """;

        Connection conn = null;
        try 
        {
            conn = Database.getConnection();

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) 
            {
                stmt.setString(1, entry.getUsername());
                stmt.setString(2, entry.getTitle());
                stmt.setString(3, entry.getContent());
                stmt.setString(4, entry.getTimestampString());

                stmt.executeUpdate();

                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) 
                {
                    entry.setId(keys.getLong(1));
                }
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        } 
        finally 
        {
            try 
            {
                if (conn != null && !Database.isUsingTestConnection()) 
                {
                    conn.close();
                }
            } 
            catch (SQLException e) 
            {
                e.printStackTrace();
            }
        }
    }

    public static List<JournalEntry> getEntriesForUser(String username) 
    {
        List<JournalEntry> entries = new ArrayList<>();

        String sql = """
            SELECT id, username, title, content, timestamp
            FROM journal_entries
            WHERE username = ?
            ORDER BY timestamp DESC
        """;

        Connection conn = null;
        try 
        {
            conn = Database.getConnection();

            try (PreparedStatement stmt = conn.prepareStatement(sql)) 
            {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) 
                {
                    JournalEntry entry = new JournalEntry(
                            rs.getLong("id"),
                            rs.getString("username"),
                            rs.getString("title"),
                            rs.getString("content"),
                            LocalDateTime.parse(rs.getString("timestamp"), JournalEntry.DB_FORMAT)
                    );
                    entries.add(entry);
                }
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        } 
        finally 
        {
            try 
            {
                if (conn != null && !Database.isUsingTestConnection()) 
                {
                    conn.close();
                }
            } 
            catch (SQLException e) 
            {
                e.printStackTrace();
            }
        }
        return entries;
    }

    /**
     * Keyword-only search (case-insensitive, wildcard-escaped).
     * If keyword is null or empty, returns all entries for the user.
     */
    public static List<JournalEntry> searchEntries(String username, String keyword) 
    {
        //Do an overload with no bounds.
        return searchEntries(username, keyword, (LocalDateTime) null, (LocalDateTime) null);
    }

    /**
     * Search by username, optional keyword, and optional LocalDate bounds.
     * startDate and endDate are LocalDate (date-only) and will be converted to start/end of day.
     * If startDate or endDate is null, that bound is ignored.
     */
    public static List<JournalEntry> searchEntries(String username, String keyword, LocalDateTime start, LocalDateTime end) 
    {
        List<JournalEntry> entries = new ArrayList<>();
        if (username == null) return entries;

        String raw = (keyword == null) ? "" : keyword.trim();
        boolean hasKeyword = !raw.isEmpty();
        boolean hasStart = start != null;
        boolean hasEnd = end != null;

        StringBuilder sql = new StringBuilder("""
            SELECT id, username, title, content, timestamp
            FROM journal_entries
            WHERE username = ?
            """);

        if (hasKeyword) 
        {
            sql.append("\n  AND (LOWER(title) LIKE ? ESCAPE '\\' OR LOWER(content) LIKE ? ESCAPE '\\')");
        }

        if (hasStart) 
        {
            sql.append("\n  AND timestamp >= ?");
        }

        if (hasEnd) 
        {
            sql.append("\n  AND timestamp <= ?");
        }

        sql.append("\n  ORDER BY timestamp DESC");

        Connection conn = null;
        try 
        {
            conn = Database.getConnection();

            try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) 
            {
                int idx = 1;
                stmt.setString(idx++, username);

                if (hasKeyword) 
                {
                    // Escape backslash first, then % and _
                    String escaped = raw
                            .replace("\\", "\\\\")
                            .replace("%", "\\%")
                            .replace("_", "\\_");
                    String pattern = "%" + escaped.toLowerCase() + "%";
                    //Bind for title and content
                    stmt.setString(idx++, pattern);
                    stmt.setString(idx++, pattern);
                }

                if (hasStart) 
                {
                    stmt.setString(idx++, start.format(JournalEntry.DB_FORMAT));
                }

                if (hasEnd) 
                {
                    stmt.setString(idx++, end.format(JournalEntry.DB_FORMAT));
                }

                try (ResultSet rs = stmt.executeQuery()) 
                {
                    while (rs.next()) 
                    {
                        JournalEntry entry = new JournalEntry(
                                rs.getLong("id"),
                                rs.getString("username"),
                                rs.getString("title"),
                                rs.getString("content"),
                                LocalDateTime.parse(rs.getString("timestamp"), JournalEntry.DB_FORMAT)
                        );
                        entries.add(entry);
                    }
                }
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        } 
        finally 
        {
            try 
            {
                if (conn != null && !Database.isUsingTestConnection()) 
                {
                    conn.close();
                }
            } 
            catch (SQLException e) 
            {
                e.printStackTrace();
            }
        }
        return entries;
    }
}