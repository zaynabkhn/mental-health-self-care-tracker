package com.mhtracker.controller;

import com.mhtracker.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    private User demoUser = new User("admin", "password");

    @FXML
    private void handleLogin() {

        String username = usernameField.getText();
        String password = passwordField.getText();

        if (demoUser.authenticate(username, password)) {

            try {

                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/mhtracker/view/DashboardView.fxml"));

                Parent root = loader.load();

                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root, 400, 300));

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            messageLabel.setText("Invalid username or password");
        }
    }
}