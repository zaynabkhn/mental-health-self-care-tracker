package com.mhtracker.model;

import java.util.List;

public class DashboardSummary {

    private final int totalMoodEntries;
    private final String mostFrequentMood;
    private final int totalHabitCompletions;
    private final List<MoodEntry> recentMoodEntries;

    public DashboardSummary(int totalMoodEntries,
                            String mostFrequentMood,
                            int totalHabitCompletions,
                            List<MoodEntry> recentMoodEntries) {
        this.totalMoodEntries = totalMoodEntries;
        this.mostFrequentMood = mostFrequentMood;
        this.totalHabitCompletions = totalHabitCompletions;
        this.recentMoodEntries = recentMoodEntries;
    }

    public int getTotalMoodEntries() {
        return totalMoodEntries;
    }

    public String getMostFrequentMood() {
        return mostFrequentMood;
    }

    public int getTotalHabitCompletions() {
        return totalHabitCompletions;
    }

    public List<MoodEntry> getRecentMoodEntries() {
        return recentMoodEntries;
    }
}