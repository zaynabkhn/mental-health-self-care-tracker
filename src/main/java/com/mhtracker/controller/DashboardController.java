package com.mhtracker.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;

public class DashboardController {

    @FXML
    private Button profileButton;

    @FXML
    private Button habitsButton;

    @FXML
    private void openProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/mhtracker/view/ProfileView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) profileButton.getScene().getWindow();
            stage.setScene(new Scene(root, 400, 300));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openHabits() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/mhtracker/view/HabitsView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) habitsButton.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}