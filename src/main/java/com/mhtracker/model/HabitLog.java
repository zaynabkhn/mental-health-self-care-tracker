package com.mhtracker.model;

import java.time.LocalDate;

public class HabitLog {

    private long id;
    private long habitId;
    private String username;
    private LocalDate logDate;
    private double value;
    private boolean completed;

    public HabitLog(long habitId, String username, LocalDate logDate, double value, boolean completed) {
        this.habitId = habitId;
        this.username = username;
        this.logDate = logDate;
        this.value = value;
        this.completed = completed;
    }

    public long getId() {
        return id;
    }

    public long getHabitId() {
        return habitId;
    }

    public String getUsername() {
        return username;
    }

    public LocalDate getLogDate() {
        return logDate;
    }

    public double getValue() {
        return value;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setId(long id) {
        this.id = id;
    }
}