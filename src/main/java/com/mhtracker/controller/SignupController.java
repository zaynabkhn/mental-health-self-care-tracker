package com.mhtracker.controller;

import com.mhtracker.model.UserDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SignupController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label messageLabel;

    @FXML
    private void handleSignup() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("Please fill in all fields");
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match");
            return;
        }

        if (UserDAO.usernameExists(username)) {
            messageLabel.setText("Username already exists");
            return;
        }

        boolean success = UserDAO.createUser(username, password);

        if (success) {
            messageLabel.setStyle("-fx-text-fill: green; -fx-font-size: 13px; -fx-font-weight: bold;");
            messageLabel.setText("Account created successfully! Please log in.");

            usernameField.clear();
            passwordField.clear();
            confirmPasswordField.clear();
        } else {
            messageLabel.setText("Failed to create account");
        }
    }

    @FXML
    private void goBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/mhtracker/view/LoginView.fxml"));

            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(
                    getClass().getResource("/com/mhtracker/view/AppStyles.css").toExternalForm()
            );

            stage.setTitle("Mental Health Tracker - Login");
            stage.setScene(scene);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}