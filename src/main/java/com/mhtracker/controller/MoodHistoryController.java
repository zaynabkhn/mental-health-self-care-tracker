package com.mhtracker.controller;

import com.mhtracker.model.MoodEntry;
import com.mhtracker.model.MoodEntryDAO;
import com.mhtracker.model.Session;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class MoodHistoryController 
{

    @FXML
    private TableView<MoodEntry> historyTable;

    @FXML
    private TableColumn<MoodEntry, String> moodColumn;

    @FXML
    private TableColumn<MoodEntry, String> notesColumn;

    @FXML
    private TableColumn<MoodEntry, String> timeColumn;

    @FXML
    public void initialize() 
    {

        moodColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getMood()));
        notesColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNote()));
        timeColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTimestamp().toString()));

        String username = Session.getLoggedInUsername();

        historyTable.setItems(FXCollections.observableArrayList(
                MoodEntryDAO.getEntriesForUser(username)
        ));
    }

    @FXML
    private void handleBack(ActionEvent event) 
    {
        try 
        {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/mhtracker/view/MoodTrackerView.fxml"));

            Parent root = loader.load();

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
