package com.mhtracker.controller;

import java.util.List;

import com.mhtracker.model.DashboardSummary;
import com.mhtracker.model.HabitLogDAO;
import com.mhtracker.model.MoodEntry;
import com.mhtracker.model.MoodEntryDAO;
import com.mhtracker.model.Session;
import com.mhtracker.service.DashboardService;
import com.mhtracker.service.ReminderService;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class DashboardController {

    @FXML
    private Button profileButton;

    @FXML
    private Button habitsButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Button moodTrackerButton;

    @FXML
    private Label avgMoodLabel;

    @FXML
    private Label habitCompletionLabel;

    @FXML
    private Label weeklySummaryLabel;

    @FXML
    private Label totalMoodEntriesLabel;

    @FXML
    private Label mostFrequentMoodLabel;

    @FXML
    private ListView<String> recentMoodList;

    @FXML
    private BarChart<String, Number> moodChart;

    @FXML
    private NumberAxis moodYAxis;

    private final DashboardService dashboardService = new DashboardService();
    private final ReminderService reminderService = new ReminderService();

    @FXML
    public void initialize() {
        if (avgMoodLabel != null && habitCompletionLabel != null && weeklySummaryLabel != null
                && moodChart != null && moodYAxis != null) {
            loadWeeklySummary();
            loadDashboardOverview();

            moodYAxis.setAutoRanging(false);
            moodYAxis.setLowerBound(1);
            moodYAxis.setUpperBound(8);
            moodYAxis.setTickUnit(1);
            moodYAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(moodYAxis) {
                @Override
                public String toString(Number object) {
                    int value = object.intValue();
                    if (value >= 1 && value <= 8) {
                        return MOOD_LABELS[value];
                    }
                    return "";
                }
            });

            loadMoodChart();
            showReminderIfNeeded();
        }
    }

    private void showReminderIfNeeded() {
        String username = Session.getLoggedInUsername();
        String reminderMessage = reminderService.buildReminderMessage(username);

        if (!reminderMessage.isBlank()) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Daily Reminder");
                alert.setHeaderText("Self-Care Reminder");
                alert.setContentText(reminderMessage);
                alert.showAndWait();
            });
        }
    }

    private void loadWeeklySummary() {
        String username = Session.getLoggedInUsername();

        String avgMoodLabelText = MoodEntryDAO.getWeeklyAverageMood(username);
        avgMoodLabel.setText(avgMoodLabelText);

        String color = MoodEntryDAO.moodToColor(avgMoodLabelText);
        avgMoodLabel.setStyle(
                "-fx-text-fill: " + color + "; -fx-font-size: 28px; -fx-font-weight: bold;"
        );

        int habitCount = HabitLogDAO.getWeeklyCompletionCount(username);
        habitCompletionLabel.setText(String.valueOf(habitCount));

        weeklySummaryLabel.setText(
                "In the past 7 days, your average mood was " + avgMoodLabelText +
                " and you completed " + habitCount + " habits."
        );
    }

    private void loadDashboardOverview() {
        String username = Session.getLoggedInUsername();
        DashboardSummary summary = dashboardService.getDashboardSummary(username);

        totalMoodEntriesLabel.setText(String.valueOf(summary.getTotalMoodEntries()));
        mostFrequentMoodLabel.setText(summary.getMostFrequentMood());

        List<String> recentItems = summary.getRecentMoodEntries().stream()
                .map(entry -> entry.getTimestampString() + " - " + entry.getMood())
                .toList();

        recentMoodList.setItems(FXCollections.observableArrayList(recentItems));
    }

    private void loadMoodChart() {
        String username = Session.getLoggedInUsername();
        List<MoodEntry> entries = MoodEntryDAO.getEntriesForUserLast7Days(username);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Mood");

        for (MoodEntry entry : entries) {
            String mood = entry.getMood();
            String date = entry.getTimestamp().toLocalDate().toString();
            int score = MoodEntryDAO.moodToScore(mood);

            XYChart.Data<String, Number> dp = new XYChart.Data<>(date, score);
            series.getData().add(dp);
        }

        moodChart.getData().clear();
        moodChart.getData().add(series);

        Platform.runLater(() -> {
            for (int i = 0; i < series.getData().size(); i++) {
                XYChart.Data<String, Number> dp = series.getData().get(i);
                String mood = entries.get(i).getMood();
                if (dp.getNode() != null) {
                    dp.getNode().getStyleClass().add("mood-" + mood.toLowerCase());
                }
            }
        });
    }

    private void switchScene(Stage stage, Parent root, String title) {
        boolean wasMaximized = stage.isMaximized();
        double width = stage.getWidth();
        double height = stage.getHeight();

        Scene scene = new Scene(root, width, height);
        scene.getStylesheets().add(
                getClass().getResource("/com/mhtracker/view/AppStyles.css").toExternalForm()
        );

        stage.setTitle(title);
        stage.setScene(scene);
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setMaximized(wasMaximized);
    }

    private static final String[] MOOD_LABELS = {
            "", "Angry", "Sad", "Anxious", "Stressed",
            "Tired", "Calm", "Excited", "Happy"
    };

    @FXML
    private void openProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/mhtracker/view/ProfileView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) profileButton.getScene().getWindow();
            switchScene(stage, root, "Mental Health Tracker - Profile");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openHabits() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/mhtracker/view/HabitsView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) habitsButton.getScene().getWindow();
            switchScene(stage, root, "Mental Health Tracker - Habits");
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
            switchScene(stage, root, "Mental Health Tracker - Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOpenMoodTracker(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/mhtracker/view/MoodTrackerView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            switchScene(stage, root, "Mental Health Tracker - Mood Tracker");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openJournal() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/mhtracker/view/JournalView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) profileButton.getScene().getWindow();
            switchScene(stage, root, "Mental Health Tracker - Journal");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}