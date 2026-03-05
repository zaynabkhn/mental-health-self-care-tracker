package com.mhtracker.controller;

import com.mhtracker.model.Habit;
import com.mhtracker.model.HabitType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class HabitsController {

    @FXML private ListView<Habit> habitsListView;
    @FXML private TextField nameField, targetField, unitField, categoryField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<HabitType> typeCombo;
    @FXML private Button editButton, deleteButton;

    private ObservableList<Habit> habits = FXCollections.observableArrayList();
    private Habit selectedHabit = null; // for edit mode

    @FXML
    public void initialize() {
        habitsListView.setItems(habits);
        typeCombo.setItems(FXCollections.observableArrayList(HabitType.values()));

        // Enable/disable edit/delete buttons when selection changes
        habitsListView.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            selectedHabit = newVal;
            editButton.setDisable(newVal == null);
            deleteButton.setDisable(newVal == null);
            if (newVal != null) {
                fillFormForEdit(newVal);
            }
        });

        // TODO: Load habits from storage (JSON/SQLite) here in real version
        // For now: dummy data
        habits.add(new Habit("Drink water", "At least 8 glasses", HabitType.NUMERIC, 8, "glasses", "Hydration"));
        habits.add(new Habit("Meditate", "", HabitType.BOOLEAN, 0, "", "Mental"));
    }

    @FXML
    private void handleAddHabit() {
        clearForm();
        // Optionally expand the titled pane or open dialog
    }

    @FXML
    private void handleSaveHabit() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showAlert("Error", "Habit name is required.");
            return;
        }

        HabitType type = typeCombo.getValue();
        if (type == null) type = HabitType.BOOLEAN;

        double target = 0;
        try {
            if (!targetField.getText().trim().isEmpty()) {
                target = Double.parseDouble(targetField.getText().trim());
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Target must be a number.");
            return;
        }

        Habit habit = new Habit(
                name,
                descriptionArea.getText().trim(),
                type,
                target,
                unitField.getText().trim(),
                categoryField.getText().trim()
        );

        if (selectedHabit != null) {
            // Edit mode – replace
            int index = habits.indexOf(selectedHabit);
            habits.set(index, habit);
        } else {
            // Add new
            habits.add(habit);
        }

        clearForm();
        selectedHabit = null;
    }

    @FXML
    private void handleDeleteHabit() {
        if (selectedHabit != null) {
            habits.remove(selectedHabit);
            clearForm();
            selectedHabit = null;
        }
    }

    @FXML
    private void handleCancelEdit() {
        clearForm();
        selectedHabit = null;
    }

    private void fillFormForEdit(Habit habit) {
        nameField.setText(habit.getName());
        descriptionArea.setText(habit.getDescription());
        typeCombo.setValue(habit.getType());
        targetField.setText(habit.getTargetValue() > 0 ? String.valueOf(habit.getTargetValue()) : "");
        unitField.setText(habit.getUnit());
        categoryField.setText(habit.getCategory());
    }

    private void clearForm() {
        nameField.clear();
        descriptionArea.clear();
        typeCombo.setValue(null);
        targetField.clear();
        unitField.clear();
        categoryField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // TODO: Add navigation back to dashboard, etc.
}
