package com.mhtracker.controller;

import java.util.List;

import com.mhtracker.model.HabitLogDAO;
import com.mhtracker.model.MoodEntry;
import com.mhtracker.model.MoodEntryDAO;
import com.mhtracker.model.Session;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    private BarChart<String, Number> moodChart;

    @FXML
    private NumberAxis moodYAxis;

    @FXML
    public void initialize() {
        if (avgMoodLabel != null && habitCompletionLabel != null && weeklySummaryLabel != null
                && moodChart != null && moodYAxis != null) {
            loadWeeklySummary();

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
    private void openHabits() {
        try {
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
    private void handleOpenMoodTracker(Event event) {
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

    @FXML
    private void openJournal() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/mhtracker/view/JournalView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) profileButton.getScene().getWindow();
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(
                    getClass().getResource("/com/mhtracker/view/AppStyles.css").toExternalForm()
            );

            stage.setTitle("Mental Health Tracker - Journal");
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}