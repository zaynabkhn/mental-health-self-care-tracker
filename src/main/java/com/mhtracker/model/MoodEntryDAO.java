package com.mhtracker.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MoodEntryDAO 
{

    public static void insert(MoodEntry entry) 
    {
        String sql = "INSERT INTO mood_entries (username, mood, note, timestamp) VALUES (?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entry.getUsername());
            stmt.setString(2, entry.getMood());
            stmt.setString(3, entry.getNote());
            stmt.setString(4, entry.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<MoodEntry> getEntriesForUser(String username) 
    {
        List<MoodEntry> entries = new ArrayList<>();

        String sql = "SELECT mood, note, timestamp FROM mood_entries WHERE username = ? ORDER BY timestamp DESC";

        try (Connection conn = Database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) 
        {

            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) 
            {
                MoodEntry entry = new MoodEntry(
                        username,
                        rs.getString("mood"),
                        rs.getString("note")
                );

                // Override timestamp with DB value
                entry.setTimestamp(rs.getString("timestamp"));

                entries.add(entry);
            }

        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }

        return entries;
    }

    //Ensures only one mood entry a day
    public static boolean hasEntryForToday(String username) 
    {
        String sql = """
            SELECT COUNT(*) FROM mood_entries
            WHERE username = ?
            AND date(timestamp) = date('now')
        """;

        try (Connection conn = Database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();
            return rs.getInt(1) > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void clearAll() {
        String sql = "DELETE FROM mood_entries";

        try (Connection conn = Database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
