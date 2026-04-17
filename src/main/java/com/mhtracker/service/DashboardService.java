package com.mhtracker.service;

import java.util.List;

import com.mhtracker.model.DashboardSummary;
import com.mhtracker.model.HabitLogDAO;
import com.mhtracker.model.MoodEntry;
import com.mhtracker.model.MoodEntryDAO;

public class DashboardService {

    public DashboardSummary getDashboardSummary(String username) {
        int totalMoodEntries = MoodEntryDAO.getMoodEntryCount(username);
        String mostFrequentMood = MoodEntryDAO.getMostFrequentMood(username);
        int totalHabitCompletions = HabitLogDAO.getWeeklyCompletionCount(username);
        List<MoodEntry> recentEntries = MoodEntryDAO.getRecentMoodEntries(username);

        return new DashboardSummary(
                totalMoodEntries,
                mostFrequentMood,
                totalHabitCompletions,
                recentEntries
        );
    }
}