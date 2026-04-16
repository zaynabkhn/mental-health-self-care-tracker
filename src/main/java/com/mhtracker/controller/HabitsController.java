package com.mhtracker.controller;

import com.mhtracker.model.Habit;
import com.mhtracker.model.HabitDAO;
import com.mhtracker.model.HabitLog;
import com.mhtracker.model.HabitLogDAO;
import com.mhtracker.model.HabitType;
import com.mhtracker.model.Session;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;

public class HabitsController {

    @FXML
    private ListView<Habit> habitsListView;

    @FXML
    private TextField nameField, targetField, unitField, categoryField, weeklyGoalField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private ComboBox<HabitType> typeCombo;

    @FXML
    private Button editButton, deleteButton;

    @FXML
    private TitledPane habitPane;

    @FXML
    private Label streakLabel;

    private ObservableList<Habit> habits = FXCollections.observableArrayList();
    private Habit selectedHabit = null;

    @FXML
    public void initialize() {
        habitsListView.setItems(habits);
        typeCombo.setItems(FXCollections.observableArrayList(HabitType.values()));

        typeCombo.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(HabitType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(getDisplayName(item));
                }
            }
        });

        typeCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(HabitType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(getDisplayName(item));
                }
            }
        });

        habitsListView.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            selectedHabit = newVal;
            editButton.setDisable(newVal == null);
            deleteButton.setDisable(newVal == null);

            if (newVal != null) {
                fillFormForEdit(newVal);

                int streak = HabitLogDAO.getStreak(
                        newVal.getId(),
                        Session.getLoggedInUsername()
                );
                streakLabel.setText("🔥 Current Streak: " + streak + " days");
            } else {
                streakLabel.setText("🔥 Current Streak: 0 days");
            }
        });

        refreshHabits();

        editButton.setDisable(true);
        deleteButton.setDisable(true);
        streakLabel.setText("🔥 Current Streak: 0 days");
    }

    @FXML
    private void handleAddHabit() {
        selectedHabit = null;
        habitsListView.getSelectionModel().clearSelection();
        clearForm();
        editButton.setDisable(true);
        deleteButton.setDisable(true);
        streakLabel.setText("🔥 Current Streak: 0 days");

        habitPane.setExpanded(true);
    }

    @FXML
    private void handleEditHabit() {
        if (selectedHabit != null) {
            fillFormForEdit(selectedHabit);
        }
    }

    @FXML
    private void handleSaveHabit() {
        String name = nameField.getText().trim();

        if (name.isEmpty()) {
            showAlert("Error", "Habit name is required.");
            return;
        }

        HabitType type = typeCombo.getValue();
        if (type == null) {
            type = HabitType.BOOLEAN;
        }

        double target = 0;
        try {
            if (!targetField.getText().trim().isEmpty()) {
                target = Double.parseDouble(targetField.getText().trim());
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Target must be a number.");
            return;
        }

        int weeklyGoal = 0;
        try {
            if (!weeklyGoalField.getText().trim().isEmpty()) {
                weeklyGoal = Integer.parseInt(weeklyGoalField.getText().trim());
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Weekly goal must be a number.");
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

        habit.setWeeklyGoal(weeklyGoal);

        if (selectedHabit != null) {
            habit.setId(selectedHabit.getId());
            HabitDAO.updateHabit(habit);
        } else {
            String username = Session.getLoggedInUsername();
            HabitDAO.insertHabit(username, habit);
        }

        refreshHabits();

        selectedHabit = null;
        habitsListView.getSelectionModel().clearSelection();
        clearForm();
        editButton.setDisable(true);
        deleteButton.setDisable(true);
        streakLabel.setText("🔥 Current Streak: 0 days");
    }

    @FXML
    private void handleDeleteHabit() {
        if (selectedHabit != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Delete Habit");
            confirm.setHeaderText("Delete selected habit?");
            confirm.setContentText(selectedHabit.getName());

            if (confirm.showAndWait().get() == ButtonType.OK) {
                HabitDAO.deleteHabit(selectedHabit.getId());
                refreshHabits();

                selectedHabit = null;
                habitsListView.getSelectionModel().clearSelection();
                clearForm();

                editButton.setDisable(true);
                deleteButton.setDisable(true);
                streakLabel.setText("🔥 Current Streak: 0 days");
            }
        }
    }

    @FXML
    private void handleCancelEdit() {
        selectedHabit = null;
        habitsListView.getSelectionModel().clearSelection();
        clearForm();
        editButton.setDisable(true);
        deleteButton.setDisable(true);
        streakLabel.setText("🔥 Current Streak: 0 days");
    }

    @FXML
    private void goBackToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/mhtracker/view/DashboardView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) habitsListView.getScene().getWindow();

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
    private void handleMarkCompleteToday() {
        if (selectedHabit == null) {
            showAlert("Error", "Please select a habit first.");
            return;
        }

        if (HabitLogDAO.hasLogForToday(selectedHabit.getId())) {
            showAlert("Info", "This habit is already logged for today.");
            return;
        }

        HabitLog log = new HabitLog(
                selectedHabit.getId(),
                Session.getLoggedInUsername(),
                java.time.LocalDate.now(),
                1,
                true
        );

        HabitLogDAO.insertLog(log);

        int streak = getSelectedHabitStreak();
        streakLabel.setText("🔥 Current Streak: " + streak + " days");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(
                "Habit marked as completed!\n🔥 Current Streak: " + streak + " days"
        );
        alert.showAndWait();
    }

    private void fillFormForEdit(Habit habit) {
        nameField.setText(habit.getName());
        descriptionArea.setText(habit.getDescription());
        typeCombo.setValue(habit.getType());
        targetField.setText(habit.getTargetValue() > 0 ? String.valueOf(habit.getTargetValue()) : "");
        unitField.setText(habit.getUnit());
        categoryField.setText(habit.getCategory());

        weeklyGoalField.setText(
                habit.getWeeklyGoal() > 0 ? String.valueOf(habit.getWeeklyGoal()) : ""
        );
    }

    private void clearForm() {
        nameField.clear();
        descriptionArea.clear();
        typeCombo.setValue(null);
        targetField.clear();
        unitField.clear();
        categoryField.clear();
        weeklyGoalField.clear();
    }

    private void showAlert(String title, String message) {
        Alert.AlertType type = title.equals("Error") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION;
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void refreshHabits() {
        String username = Session.getLoggedInUsername();
        habits.setAll(HabitDAO.getHabitsForUser(username));
    }

    private String getDisplayName(HabitType type) {
        return switch (type) {
            case BOOLEAN -> "Yes/No";
            case NUMERIC -> "Countable";
        };
    }

    private int getSelectedHabitStreak() {
        if (selectedHabit == null) return 0;

        return HabitLogDAO.getStreak(
                selectedHabit.getId(),
                Session.getLoggedInUsername()
        );
    }
}