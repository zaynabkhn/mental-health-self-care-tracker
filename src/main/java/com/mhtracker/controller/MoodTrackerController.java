package com.mhtracker.controller;

import com.mhtracker.model.MoodEntry;
import com.mhtracker.model.MoodEntryDAO;
import com.mhtracker.model.Session;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class MoodTrackerController 
{
    @FXML
    private ComboBox<String> moodComboBox;

    @FXML
    private TextArea notesArea;

    @FXML
    public void initialize() 
    {
        moodComboBox.getItems().addAll(
                "Happy",
                "Sad",
                "Anxious",
                "Stressed",
                "Calm",
                "Angry",
                "Tired",
                "Excited"
        );
    }

    @FXML
    private void handleSave(Event event) 
    {
        String mood = moodComboBox.getValue();
        String notes = notesArea.getText();

        if (mood == null) 
        {
            System.out.println("Please select a mood.");
            return;
        }

        String username = Session.getLoggedInUsername();

        MoodEntry entry = new MoodEntry(username, mood, notes);
        MoodEntryDAO.insert(entry);

        System.out.println("Mood entry saved for: " + username);

        // Later: call MoodEntryDAO.insert(...)
    }

    @FXML
    private void handleBack(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/mhtracker/view/DashboardView.fxml"));

            Parent root = loader.load();

            // Get the current stage from the button that was clicked
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root, 400, 300));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
