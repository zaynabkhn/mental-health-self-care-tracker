package com.mhtracker.model;

import java.time.LocalDate;
import java.util.Objects;

public class Habit {
    private long id;                // Unique ID (auto-generated or from storage)
    private String name;            // e.g., "Drink water", "Exercise"
    private String description;     // Optional longer note
    private HabitType type;         // BOOLEAN or NUMERIC
    private double targetValue;     // For numeric: e.g., 8 (glasses), 30 (minutes)
    private String unit;            // Optional: "glasses", "min", "steps"
    private LocalDate createdDate;
    private String category;        // Optional: "Physical", "Mental", "Hydration", etc.
    private int weeklyGoal;

    // Constructor for new habits
    public Habit(String name, String description, HabitType type, double targetValue, String unit, String category) {
        this.name = name;
        this.description = description != null ? description : "";
        this.type = type != null ? type : HabitType.BOOLEAN;
        this.targetValue = targetValue;
        this.unit = unit != null ? unit : "";
        this.category = category != null ? category : "";
        this.createdDate = LocalDate.now();
    }

    // Full constructor (for loading from storage)
    public Habit(long id, String name, String description, HabitType type, double targetValue,
                 String unit, LocalDate createdDate, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.targetValue = targetValue;
        this.unit = unit;
        this.createdDate = createdDate;
        this.category = category;
    }

    // Getters and setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public HabitType getType() { return type; }
    public void setType(HabitType type) { this.type = type; }

    public double getTargetValue() { return targetValue; }
    public void setTargetValue(double targetValue) { this.targetValue = targetValue; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public LocalDate getCreatedDate() { return createdDate; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getWeeklyGoal() {
    return weeklyGoal;
    }
    public void setWeeklyGoal(int weeklyGoal) {
    this.weeklyGoal = weeklyGoal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Habit habit = (Habit) o;
        return id == habit.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name + (type == HabitType.NUMERIC ? " (" + targetValue + " " + unit + ")" : "");
    }
}
