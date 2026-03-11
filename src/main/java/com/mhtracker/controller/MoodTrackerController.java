package com.mhtracker.controller;

import com.mhtracker.model.MoodEntry;
import com.mhtracker.model.MoodEntryDAO;
import com.mhtracker.model.Session;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class MoodTrackerController {

    @FXML
    private ComboBox<String> moodComboBox;

    @FXML
    private TextArea notesArea;

    @FXML
    public void initialize() {
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
    private void handleSave(Event event) {
        String mood = moodComboBox.getValue();
        String notes = notesArea.getText();
        String username = Session.getLoggedInUsername();

        if (mood == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Mood");
            alert.setHeaderText(null);
            alert.setContentText("Please select a mood before saving.");
            alert.showAndWait();
            return;
        }

        if (MoodEntryDAO.hasEntryForToday(username)) {
            new Alert(Alert.AlertType.INFORMATION,
                    "You have already saved a mood entry today.").showAndWait();
            return;
        }

        MoodEntry entry = new MoodEntry(username, mood, notes);
        MoodEntryDAO.insert(entry);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Mood Saved");
        alert.setHeaderText(null);
        alert.setContentText("Your mood entry has been saved successfully!");
        alert.showAndWait();

        moodComboBox.setValue(null);
        notesArea.clear();
    }

    @FXML
    private void handleBack(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/mhtracker/view/DashboardView.fxml"));

            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(
                    getClass().getResource("/com/mhtracker/view/AppStyles.css").toExternalForm()
            );

            stage.setTitle("Mental Health Tracker - Dashboard");
            stage.setScene(scene);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewHistory(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/mhtracker/view/MoodHistoryView.fxml"));

            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(
                    getClass().getResource("/com/mhtracker/view/AppStyles.css").toExternalForm()
            );

            stage.setTitle("Mental Health Tracker - Mood History");
            stage.setScene(scene);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}