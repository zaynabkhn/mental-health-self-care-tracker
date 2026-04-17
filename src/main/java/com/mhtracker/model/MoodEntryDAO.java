package com.mhtracker.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MoodEntryDAO {

    public static void insert(MoodEntry entry) {
        String sql = "INSERT INTO mood_entries (username, mood, note, timestamp) VALUES (?, ?, ?, ?)";

        try {
            Connection conn = Database.getConnection();

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, entry.getUsername());
                stmt.setString(2, entry.getMood());
                stmt.setString(3, entry.getNote());
                stmt.setString(4, entry.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                stmt.executeUpdate();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<MoodEntry> getEntriesForUser(String username) {
        List<MoodEntry> entries = new ArrayList<>();

        String sql = "SELECT mood, note, timestamp FROM mood_entries WHERE username = ? ORDER BY timestamp DESC";

        try {
            Connection conn = Database.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, username);

                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String mood = rs.getString("mood");
                    String note = rs.getString("note");
                    String ts = rs.getString("timestamp").trim();

                    LocalDateTime parsed = LocalDateTime.parse(ts, MoodEntry.DB_FORMAT);

                    MoodEntry entry = new MoodEntry(mood, note, parsed);
                    entry.setUsername(username);

                    entries.add(entry);
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return entries;
    }

    public static boolean hasEntryForToday(String username) {
        String sql = """
            SELECT COUNT(*) FROM mood_entries
            WHERE username = ?
            AND date(timestamp) = date('now', 'localtime')
        """;

        try {
            Connection conn = Database.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, username);

                ResultSet rs = stmt.executeQuery();
                return rs.getInt(1) > 0;

            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<MoodEntry> getEntriesForUserLast7Days(String username) {
        List<MoodEntry> entries = new ArrayList<>();

        String sql = """
            SELECT mood, note, timestamp
            FROM mood_entries
            WHERE username = ?
            AND timestamp >= datetime('now', '-7 days', 'localtime')
            ORDER BY timestamp DESC
        """;

        try {
            Connection conn = Database.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String mood = rs.getString("mood");
                    String note = rs.getString("note");
                    String ts = rs.getString("timestamp").trim();

                    LocalDateTime parsed = LocalDateTime.parse(ts, MoodEntry.DB_FORMAT);

                    MoodEntry entry = new MoodEntry(mood, note, parsed);
                    entry.setUsername(username);

                    entries.add(entry);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return entries;
    }

    public static int moodToScore(String mood) {
        return switch (mood) {
            case "Happy" -> 8;
            case "Excited" -> 7;
            case "Calm" -> 6;
            case "Tired" -> 5;
            case "Stressed" -> 4;
            case "Anxious" -> 3;
            case "Sad" -> 2;
            case "Angry" -> 1;
            default -> 0;
        };
    }

    public static String scoreToMood(double score) {
        if (score >= 7.5) return "Happy";
        if (score >= 6.5) return "Excited";
        if (score >= 5.5) return "Calm";
        if (score >= 4.5) return "Tired";
        if (score >= 3.5) return "Stressed";
        if (score >= 2.5) return "Anxious";
        if (score >= 1.5) return "Sad";
        return "Angry";
    }

    public static String moodToColor(String mood) {
        return switch (mood) {
            case "Happy" -> "#4CAF50";
            case "Excited" -> "#FF9800";
            case "Calm" -> "#2196F3";
            case "Tired" -> "#9C27B0";
            case "Stressed" -> "#F44336";
            case "Anxious" -> "#E91E63";
            case "Sad" -> "#3F51B5";
            case "Angry" -> "#B71C1C";
            default -> "#000000";
        };
    }

    public static String getWeeklyAverageMood(String username) {
        List<MoodEntry> entries = getEntriesForUserLast7Days(username);

        if (entries.isEmpty()) {
            return "No data";
        }

        int total = 0;

        for (MoodEntry entry : entries) {
            total += moodToScore(entry.getMood());
        }

        double avg = total / (double) entries.size();
        return scoreToMood(avg);
    }

    public static void clearAll() {
        String sql = "DELETE FROM mood_entries";

        try {
            Connection conn = Database.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.executeUpdate();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}