package com.mhtracker.model;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HabitDAO {

    public static void insertHabit(String username, Habit habit) {
        String sql = """
            INSERT INTO habits (username, name, description, type, target_value, unit, category, weekly_goal, created_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        Connection conn = null;
        try {
            conn = Database.getConnection();

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, username);
                stmt.setString(2, habit.getName());
                stmt.setString(3, habit.getDescription());
                stmt.setString(4, habit.getType().name());
                stmt.setDouble(5, habit.getTargetValue());
                stmt.setString(6, habit.getUnit());
                stmt.setString(7, habit.getCategory());
                stmt.setInt(8, habit.getWeeklyGoal());
                stmt.setString(9, habit.getCreatedDate().toString());

                stmt.executeUpdate();

                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    habit.setId(keys.getLong(1));
                }
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

    public static List<Habit> getHabitsForUser(String username) {
        List<Habit> habits = new ArrayList<>();

        String sql = """
            SELECT id, name, description, type, target_value, unit, category, weekly_goal, created_date
            FROM habits
            WHERE username = ?
            ORDER BY id ASC
        """;

        Connection conn = null;
        try {
            conn = Database.getConnection();

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    Habit habit = new Habit(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            HabitType.valueOf(rs.getString("type")),
                            rs.getDouble("target_value"),
                            rs.getString("unit"),
                            LocalDate.parse(rs.getString("created_date")),
                            rs.getString("category")
                    );

                    habit.setWeeklyGoal(rs.getInt("weekly_goal"));
                    habits.add(habit);
                }
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

        return habits;
    }

    public static void updateHabit(Habit habit) {
        String sql = """
            UPDATE habits
            SET name = ?, description = ?, type = ?, target_value = ?, unit = ?, category = ?, weekly_goal = ?
            WHERE id = ?
        """;

        Connection conn = null;
        try {
            conn = Database.getConnection();

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, habit.getName());
                stmt.setString(2, habit.getDescription());
                stmt.setString(3, habit.getType().name());
                stmt.setDouble(4, habit.getTargetValue());
                stmt.setString(5, habit.getUnit());
                stmt.setString(6, habit.getCategory());
                stmt.setInt(7, habit.getWeeklyGoal());
                stmt.setLong(8, habit.getId());

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

    public static void deleteHabit(long habitId) {
        String sql = "DELETE FROM habits WHERE id = ?";

        Connection conn = null;
        try {
            conn = Database.getConnection();

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, habitId);
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
}