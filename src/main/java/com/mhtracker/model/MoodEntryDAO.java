package com.mhtracker.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MoodEntryDAO {

    public static void insert(MoodEntry entry) {

        String sql = "INSERT INTO mood_entries (username, mood, notes, timestamp) VALUES (?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entry.getUsername());
            stmt.setString(2, entry.getMood());
            stmt.setString(3, entry.getNote());
            stmt.setString(4, entry.getTimestamp().toString());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
