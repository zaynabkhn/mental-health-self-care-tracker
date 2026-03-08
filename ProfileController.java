package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import model.UserProfile;
import service.ProfileService;

public class ProfileController {

    @FXML
    private TextField displayNameField;

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
}