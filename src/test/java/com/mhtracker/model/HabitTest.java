package com.mhtracker.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HabitTest {

    @Test
    public void testHabitCreation() {
        Habit habit = new Habit(
                "Drink Water",
                "Drink 8 glasses daily",
                HabitType.NUMERIC,
                8,
                "glasses",
                "Health"
        );

        assertEquals("Drink Water", habit.getName());
        assertEquals("Drink 8 glasses daily", habit.getDescription());
        assertEquals(HabitType.NUMERIC, habit.getType());
        assertEquals(8, habit.getTargetValue());
        assertEquals("glasses", habit.getUnit());
        assertEquals("Health", habit.getCategory());
    }

    @Test
    public void testHabitTypeBoolean() {
        Habit habit = new Habit(
                "Meditate",
                "Meditate for calmness",
                HabitType.BOOLEAN,
                0,
                "",
                "Mental Health"
        );

        assertEquals(HabitType.BOOLEAN, habit.getType());
        assertEquals("Meditate", habit.getName());
    }
}