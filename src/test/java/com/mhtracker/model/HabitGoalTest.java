package com.mhtracker.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HabitGoalTest {

    @BeforeEach
    public void setup() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:file:memdb_habitgoal?mode=memory&cache=shared");
        Database.useTestConnection(conn);
        conn.createStatement().execute("PRAGMA foreign_keys = ON;");
        Database.initialize();

        conn.createStatement().execute("DELETE FROM habit_logs;");
        conn.createStatement().execute("DELETE FROM habits;");
        conn.createStatement().execute("DELETE FROM users;");

        conn.createStatement().execute("INSERT INTO users (username, password) VALUES ('testuser', 'pw');");
    }

    @Test
    public void testWeeklyGoalSetOnHabitObject() {
        Habit habit = new Habit(
                "Drink Water",
                "Drink water daily",
                HabitType.BOOLEAN,
                0,
                "",
                "Health"
        );

        habit.setWeeklyGoal(7);

        assertEquals(7, habit.getWeeklyGoal());
    }

    @Test
    public void testWeeklyGoalSavedAndLoadedFromDatabase() {
        Habit habit = new Habit(
                "Exercise",
                "Workout during the week",
                HabitType.BOOLEAN,
                0,
                "",
                "Fitness"
        );
        habit.setWeeklyGoal(3);

        HabitDAO.insertHabit("testuser", habit);

        List<Habit> habits = HabitDAO.getHabitsForUser("testuser");

        assertEquals(1, habits.size());
        assertEquals("Exercise", habits.get(0).getName());
        assertEquals(3, habits.get(0).getWeeklyGoal());
    }

    @Test
    public void testWeeklyGoalUpdatedInDatabase() {
        Habit habit = new Habit(
                "Meditate",
                "Meditate regularly",
                HabitType.BOOLEAN,
                0,
                "",
                "Mental"
        );
        habit.setWeeklyGoal(2);

        HabitDAO.insertHabit("testuser", habit);

        habit.setWeeklyGoal(5);
        HabitDAO.updateHabit(habit);

        List<Habit> habits = HabitDAO.getHabitsForUser("testuser");

        assertEquals(1, habits.size());
        assertEquals(5, habits.get(0).getWeeklyGoal());
    }
}