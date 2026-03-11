package com.mhtracker.controller;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
public class DashboardController {

    @FXML
    private Button profileButton;

    @FXML
    private Button habitsButton;

    @FXML
    private void openProfile() 
    {
        try 
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mhtracker/view/ProfileView.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) profileButton.getScene().getWindow();
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(
                    getClass().getResource("/com/mhtracker/view/AppStyles.css").toExternalForm()
            );

            stage.setTitle("Mental Health Tracker - Profile");
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openHabits() 
    {
        try 
        {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/mhtracker/view/HabitsView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) habitsButton.getScene().getWindow();
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(
                    getClass().getResource("/com/mhtracker/view/AppStyles.css").toExternalForm()
            );

            stage.setTitle("Mental Health Tracker - Habits");
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void logout(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/mhtracker/view/LoginView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
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
    @FXML
    private void handleOpenMoodTracker(Event event) 
    {
        try 
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mhtracker/view/MoodTrackerView.fxml"));
            Parent root = loader.load();

            // Get the current stage from the button that was clicked
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root, 400, 300));
            stage.show();
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}