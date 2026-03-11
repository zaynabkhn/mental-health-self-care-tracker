package com.mhtracker.model;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MoodEntry 
{
    private String username;
    private String mood;              //"Happy", "Sad", "Stressed"
    private String note;              //Optional user note
    private LocalDateTime timestamp;  //When the entry was created
    //So that the mood history doesn't get borked
    public static final DateTimeFormatter DB_FORMAT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public MoodEntry(String username, String mood, String note) 
    {
        this.username = username;
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
    public String getUsername() 
    { 
        return username; 
    }
    
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

    public String getTimestampString() 
    {
        return timestamp.format(DB_FORMAT);
    }

    // Setters
    public void setUsername(String username) 
    {
        this.username = username;
    }

    public void setMood(String mood) 
    {
        this.mood = mood;
    }

    public void setNote(String note) 
    {
        this.note = note;
    }

    public void setTimestamp(String timestampString) 
    {
        this.timestamp = LocalDateTime.parse(timestampString.trim(), DB_FORMAT);
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
