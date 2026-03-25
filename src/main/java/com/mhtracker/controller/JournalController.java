package com.mhtracker.controller;

import com.mhtracker.model.JournalEntry;
import com.mhtracker.model.JournalEntryDAO;
import com.mhtracker.model.Session;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class JournalController {

    @FXML
    private TextArea journalTextArea;

    @FXML
    private TableView<JournalEntry> journalTable;

    @FXML
    private TableColumn<JournalEntry, String> timestampColumn;

    @FXML
    private TableColumn<JournalEntry, String> contentColumn;

    @FXML
    public void initialize() {
        timestampColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTimestampString()));
        contentColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getContent()));

        refreshJournalEntries();
    }

    @FXML
    private void handleSaveEntry() {
        String content = journalTextArea.getText().trim();

        if (content.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Empty Entry");
            alert.setHeaderText(null);
            alert.setContentText("Please write something before saving.");
            alert.showAndWait();
            return;
        }

        JournalEntry entry = new JournalEntry(Session.getLoggedInUsername(), content);
        JournalEntryDAO.insertEntry(entry);

        journalTextArea.clear();
        refreshJournalEntries();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Saved");
        alert.setHeaderText(null);
        alert.setContentText("Journal entry saved successfully.");
        alert.showAndWait();
    }

    @FXML
    private void handleBackToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/mhtracker/view/DashboardView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) journalTextArea.getScene().getWindow();
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

    @FXML
    private TextField searchField;

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            refreshJournalEntries();
            return;
        }

        String username = Session.getLoggedInUsername();

        journalTable.setItems(FXCollections.observableArrayList(
            JournalEntryDAO.searchEntries(username, keyword)
        ));
    }

    @FXML
    private void handleClearSearch() {
        searchField.clear();
        refreshJournalEntries();
    }

    private void refreshJournalEntries() {
        String username = Session.getLoggedInUsername();
        journalTable.setItems(FXCollections.observableArrayList(
                JournalEntryDAO.getEntriesForUser(username)
        ));
    }
}