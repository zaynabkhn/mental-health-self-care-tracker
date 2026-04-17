package com.mhtracker.service;

import com.mhtracker.model.HabitLogDAO;
import com.mhtracker.model.MoodEntryDAO;

public class ReminderService {

    public boolean shouldRemindMood(String username) {
        return !MoodEntryDAO.hasEntryForToday(username);
    }

    public boolean shouldRemindHabits(String username) {
        return !HabitLogDAO.hasAnyHabitLoggedToday(username);
    }

    public String buildReminderMessage(String username) {
        boolean missingMood = shouldRemindMood(username);
        boolean missingHabits = shouldRemindHabits(username);

        if (missingMood && missingHabits) {
            return "Reminder: You have not completed your mood check-in or logged any habits today.";
        }

        if (missingMood) {
            return "Reminder: You have not completed your mood check-in today.";
        }

        if (missingHabits) {
            return "Reminder: You have not logged any habits today.";
        }

        return "";
    }
}