package com.mhtracker.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import com.mhtracker.model.UserProfile;
import com.mhtracker.service.ProfileService;

public class ProfileController {

    @FXML
    private TextField displayNameField;

    @FXML
    private Button backButton;

    private final ProfileService profileService = new ProfileService();

    @FXML
    public void initialize() {
        loadProfile();
    }

    private void loadProfile() {
        UserProfile profile = profileService.loadProfile();
        displayNameField.setText(profile.getDisplayName());
    }

    @FXML
    private void saveProfile() {
        String name = displayNameField.getText();

        if (name == null || name.trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Invalid Name");
            alert.setContentText("Display name cannot be empty.");
            alert.showAndWait();
            return;
        }

        UserProfile profile = new UserProfile(name);
        profileService.saveProfile(profile);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Success");
        alert.setContentText("Profile saved successfully.");
        alert.showAndWait();
    }

    @FXML
        private void goBackToDashboard() {
        try {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/mhtracker/view/DashboardView.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) displayNameField.getScene().getWindow();

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
}