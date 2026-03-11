package com.mhtracker;

import com.mhtracker.model.Database;
import com.mhtracker.model.MoodEntryDAO;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application 
{

    @Override
    public void start(Stage stage) throws Exception 
    {
        // Initialize database BEFORE loading UI
        Database.initialize();

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/mhtracker/view/LoginView.fxml")
        );

        Scene scene = new Scene(loader.load(), 900, 600);
        scene.getStylesheets().add(
                getClass().getResource("/com/mhtracker/view/AppStyles.css").toExternalForm()
        );

        stage.setTitle("Mental Health Tracker - Login");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.centerOnScreen();
        stage.show();
        MoodEntryDAO.clearAll();
    }

    public static void main(String[] args) 
    {
        launch();
    }
}