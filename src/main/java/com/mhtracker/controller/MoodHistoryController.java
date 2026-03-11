package com.mhtracker.controller;

import com.mhtracker.model.MoodEntry;
import com.mhtracker.model.MoodEntryDAO;
import com.mhtracker.model.Session;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class MoodHistoryController {

    @FXML
    private TableView<MoodEntry> historyTable;

    @FXML
    private TableColumn<MoodEntry, String> moodColumn;

    @FXML
    private TableColumn<MoodEntry, String> notesColumn;

    @FXML
    private TableColumn<MoodEntry, String> timeColumn;

    @FXML
    private TextArea selectedNoteArea;

    @FXML
    public void initialize() {
        moodColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMood()));
        notesColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNote()));
        timeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTimestamp().toString()));

        String username = Session.getLoggedInUsername();

        historyTable.setItems(FXCollections.observableArrayList(
                MoodEntryDAO.getEntriesForUser(username)
        ));

        historyTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedNoteArea.setText(newSelection.getNote());
            } else {
                selectedNoteArea.clear();
            }
        });
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/mhtracker/view/MoodTrackerView.fxml"));

            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(
                    getClass().getResource("/com/mhtracker/view/AppStyles.css").toExternalForm()
            );

            stage.setTitle("Mental Health Tracker - Mood Tracker");
            stage.setScene(scene);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}