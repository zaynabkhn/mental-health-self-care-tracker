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
            INSERT INTO journal_entries (username, content, timestamp)
            VALUES (?, ?, ?)
        """;

        Connection conn = null;
        try 
        {
            conn = Database.getConnection();

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) 
            {
                stmt.setString(1, entry.getUsername());
                stmt.setString(2, entry.getContent());
                stmt.setString(3, entry.getTimestampString());

                stmt.executeUpdate();

                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
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
            SELECT id, username, content, timestamp
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

    public static List<JournalEntry> searchEntries(String username, String keyword) 
    {
        List<JournalEntry> entries = new ArrayList<>();

        if (username == null) 
        {
            return entries;
        }

        //Normalize keyword: treat null as empty and trim any whitespace.
        String raw = (keyword == null) ? "" : keyword.trim();
        boolean matchAll = raw.isEmpty();

        String sql;
        if (matchAll) 
        {
            sql = """
                SELECT id, username, content, timestamp
                FROM journal_entries
                WHERE username = ?
                ORDER BY timestamp DESC
            """;
        } 
        else 
        {
            //Uses case-insensitive search and escape wildcards.
            sql = """
                SELECT id, username, content, timestamp
                FROM journal_entries
                WHERE username = ?
                AND LOWER(content) LIKE ? ESCAPE '\\'
                ORDER BY timestamp DESC
            """;
        }

        Connection conn = null;
        try 
        {
            conn = Database.getConnection();

            try (PreparedStatement stmt = conn.prepareStatement(sql)) 
            {
                stmt.setString(1, username);

                if (!matchAll) 
                {
                    // Escape backslash first, then % and _
                    String escaped = raw
                            .replace("\\", "\\\\")
                            .replace("%", "\\%")
                            .replace("_", "\\_");
                    String pattern = "%" + escaped.toLowerCase() + "%";
                    stmt.setString(2, pattern);
                }

                try (ResultSet rs = stmt.executeQuery()) 
                {
                    while (rs.next()) 
                    {
                        JournalEntry entry = new JournalEntry(
                                rs.getLong("id"),
                                rs.getString("username"),
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