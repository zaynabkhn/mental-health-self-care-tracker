package com.mhtracker.model;
import java.time.LocalDateTime;

public class MoodEntry 
{
    private String mood;              //"Happy", "Sad", "Stressed"
    private String note;              //Optional user note
    private LocalDateTime timestamp;  //When the entry was created

    public MoodEntry(String mood, String note) 
    {
        this.mood = mood;
        this.note = note;
        this.timestamp = LocalDateTime.now();
    }

    public MoodEntry(String mood, String note, LocalDateTime timestamp) 
    {
        this.mood = mood;
        this.note = note;
        this.timestamp = timestamp;
    }

    // Getters
    public String getMood() 
    {
        return mood;
    }

    public String getNote() 
    {
        return note;
    }

    public LocalDateTime getTimestamp() 
    {
        return timestamp;
    }

    // Setters
    public void setMood(String mood) 
    {
        this.mood = mood;
    }

    public void setNote(String note) 
    {
        this.note = note;
    }

    public void setTimestamp(LocalDateTime timestamp) 
    {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() 
    {
        return "MoodEntry{" +
                "mood='" + mood + '\'' +
                ", note='" + note + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
