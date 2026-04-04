package com.mhtracker.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.mhtracker.model.JournalEntry;
import com.mhtracker.model.JournalEntryDAO;
import com.mhtracker.model.Session;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class JournalController 
{
    @FXML
    private TextArea journalTextArea;

    @FXML
    private TableView<JournalEntry> journalTable;

    @FXML
    private TableColumn<JournalEntry, String> timestampColumn;

    @FXML
    private TableColumn<JournalEntry, String> contentColumn;

    @FXML
    private TextField titleField;

    @FXML
    private TableColumn<JournalEntry, String> titleColumn;

    // Search UI controls
    @FXML
    private TextField searchField;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    // Optional time fields (format HH:mm)
    @FXML
    private TextField startTimeField;

    @FXML
    private TextField endTimeField;

    // Optional status label for feedback
    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() 
    {
        timestampColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTimestampString()));
        contentColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getContent()));
        titleColumn.setCellValueFactory(data ->
        new SimpleStringProperty(data.getValue().getTitle()));

        refreshJournalEntries();
    }

    @FXML
    private void handleSaveEntry() {
        String title = (titleField == null) ? "" : titleField.getText().trim();
        String content = journalTextArea.getText().trim();

        if (content.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Empty Entry");
            alert.setHeaderText(null);
            alert.setContentText("Please write something before saving.");
            alert.showAndWait();
            return;
        }

        JournalEntry entry = new JournalEntry(Session.getLoggedInUsername(), title, content);
        JournalEntryDAO.insertEntry(entry);

        if(titleField != null) titleField.clear();
        if(journalTextArea != null) journalTextArea.clear();
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

    /**
     * Existing keyword-only search button (keeps backward compatibility).
     * If date pickers are empty, this will perform a keyword-only search.
     * If date pickers contain values, delegate to performSearch which applies date filters.
     */
    @FXML
    private void handleSearch() {
        // If user has provided any date input, delegate to the date-aware search handler
        if ((startDatePicker != null && startDatePicker.getValue() != null) ||
            (endDatePicker != null && endDatePicker.getValue() != null) ||
            (startTimeField != null && !startTimeField.getText().isBlank()) ||
            (endTimeField != null && !endTimeField.getText().isBlank())) {
            performSearch();
            return;
        }

        String keyword = (searchField == null) ? "" : searchField.getText().trim();

        if (keyword.isEmpty()) {
            refreshJournalEntries();
            return;
        }

        String username = Session.getLoggedInUsername();

        journalTable.setItems(FXCollections.observableArrayList(
            JournalEntryDAO.searchEntries(username, keyword)
        ));
        if (statusLabel != null) statusLabel.setText("Search complete: " + journalTable.getItems().size() + " entries");
    }

    /**
     * New handler wired from FXML: onAction="#handleSearchByDate"
     */
    @FXML
    private void handleSearchByDate(ActionEvent event) 
    {
        performSearch();
    }

    /**
     * Clear search inputs and reload all entries.
     */
    @FXML
    private void handleClearSearch() 
    {
        if (searchField != null) searchField.clear();
        if (titleField != null) titleField.clear();
        if (startDatePicker != null) startDatePicker.setValue(null);
        if (endDatePicker != null) endDatePicker.setValue(null);
        if (startTimeField != null) startTimeField.clear();
        if (endTimeField != null) endTimeField.clear();
        refreshJournalEntries();
        if (statusLabel != null) statusLabel.setText("Cleared");
    }

    /**
     * Parse inputs, validate, call DAO overloads, and update the table.
     */
    private void performSearch() 
    {
        try 
        {
            String keyword = (searchField == null) ? "" : searchField.getText().trim();
            LocalDate startDate = (startDatePicker == null) ? null : startDatePicker.getValue();
            LocalDate endDate = (endDatePicker == null) ? null : endDatePicker.getValue();

            LocalDateTime start = null;
            LocalDateTime end = null;

            //Use start time if provided
            if(startDate != null) 
            {
                if(startTimeField != null && !startTimeField.getText().isBlank()) 
                {
                    start = startDate.atTime(LocalTime.parse(startTimeField.getText()));
                } 
                else 
                {
                    start = startDate.atStartOfDay();
                }
            }

            //Use end time if provided
            if(endDate != null) 
            {
                if (endTimeField != null && !endTimeField.getText().isBlank()) 
                {
                    end = endDate.atTime(LocalTime.parse(endTimeField.getText()));
                } 
                else 
                {
                    end = endDate.atTime(LocalTime.MAX);
                }
            }

            // Validate range if both bounds present
            if(start != null && end != null && start.isAfter(end)) 
            {
                if (statusLabel != null) statusLabel.setText("Start must be before or equal to End.");
                return;
            }

            String username = Session.getLoggedInUsername();
            List<JournalEntry> results = JournalEntryDAO.searchEntries(username, keyword, start, end);
            journalTable.getItems().setAll(results);
            if (statusLabel != null) statusLabel.setText("Search complete: " + results.size() + " entries");
        } 
        catch(Exception ex) 
        {
            if (statusLabel != null) statusLabel.setText("Search failed: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void refreshJournalEntries() 
    {
        String username = Session.getLoggedInUsername();
        journalTable.setItems(FXCollections.observableArrayList(
                JournalEntryDAO.getEntriesForUser(username)
        ));
    }
}