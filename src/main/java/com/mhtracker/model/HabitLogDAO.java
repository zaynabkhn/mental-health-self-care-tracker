package com.mhtracker.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class HabitLogDAO {

    public static boolean hasLogForToday(long habitId) {
        String sql = """
            SELECT COUNT(*) FROM habit_logs
            WHERE habit_id = ?
            AND log_date = date('now', 'localtime')
        """;

        Connection conn = null;
        try {
            conn = Database.getConnection();

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, habitId);

                ResultSet rs = stmt.executeQuery();
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null && !Database.isUsingTestConnection()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean hasAnyHabitLoggedToday(String username) {
        String sql = """
            SELECT COUNT(*) FROM habit_logs
            WHERE username = ?
            AND completed = 1
            AND log_date = date('now', 'localtime')
        """;

        Connection conn = null;
        try {
            conn = Database.getConnection();

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);

                ResultSet rs = stmt.executeQuery();
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null && !Database.isUsingTestConnection()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void insertLog(HabitLog log) {
        String sql = """
            INSERT INTO habit_logs (habit_id, username, log_date, value, completed)
            VALUES (?, ?, ?, ?, ?)
        """;

        Connection conn = null;
        try {
            conn = Database.getConnection();

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, log.getHabitId());
                stmt.setString(2, log.getUsername());
                stmt.setString(3, log.getLogDate().toString());
                stmt.setDouble(4, log.getValue());
                stmt.setInt(5, log.isCompleted() ? 1 : 0);

                stmt.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null && !Database.isUsingTestConnection()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getStreak(long habitId, String username) {
        String sql = """
            SELECT log_date FROM habit_logs
            WHERE habit_id = ? AND username = ?
        """;

        Set<LocalDate> dates = new HashSet<>();

        Connection conn = null;
        try {
            conn = Database.getConnection();

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, habitId);
                stmt.setString(2, username);

                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    dates.add(LocalDate.parse(rs.getString("log_date")));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            try {
                if (conn != null && !Database.isUsingTestConnection()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        int streak = 0;
        LocalDate current = LocalDate.now();

        while (dates.contains(current)) {
            streak++;
            current = current.minusDays(1);
        }

        return streak;
    }

    public static int getWeeklyCompletionCount(String username) {
        String sql = """
            SELECT COUNT(*) FROM habit_logs
            WHERE username = ?
            AND completed = 1
            AND log_date >= date('now', '-7 days', 'localtime')
        """;

        Connection conn = null;
        try {
            conn = Database.getConnection();

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);

                ResultSet rs = stmt.executeQuery();
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            try {
                if (conn != null && !Database.isUsingTestConnection()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}