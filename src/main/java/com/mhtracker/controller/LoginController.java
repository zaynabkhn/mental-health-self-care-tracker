package com.mhtracker.controller;

import com.mhtracker.model.Session;
import com.mhtracker.model.UserDAO;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter username and password");
            return;
        }

        if (UserDAO.authenticate(username, password)) {
            Session.setLoggedInUsername(username);

            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/mhtracker/view/DashboardView.fxml"));

                Parent root = loader.load();

                Stage stage = (Stage) usernameField.getScene().getWindow();
                Scene scene = new Scene(root, 900, 600);
                scene.getStylesheets().add(
                        getClass().getResource("/com/mhtracker/view/AppStyles.css").toExternalForm()
                );

                stage.setTitle("Mental Health Tracker - Dashboard");
                stage.setScene(scene);

                usernameField.clear();
                passwordField.clear();
                messageLabel.setText("");

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            messageLabel.setText("Invalid username or password");
            passwordField.clear();
        }
    }

    @FXML
    private void openSignup() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/mhtracker/view/SignupView.fxml"));

            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(
                    getClass().getResource("/com/mhtracker/view/AppStyles.css").toExternalForm()
            );

            stage.setTitle("Mental Health Tracker - Sign Up");
            stage.setScene(scene);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}