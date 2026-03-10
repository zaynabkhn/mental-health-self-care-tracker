package com.mhtracker.controller;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MoodTrackerController {

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
