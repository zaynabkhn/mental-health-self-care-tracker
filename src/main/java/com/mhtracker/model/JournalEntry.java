package com.mhtracker.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JournalEntry {

    private long id;
    final private String username;
    private String title;
    final private String content;
    final private LocalDateTime timestamp;

    public static final DateTimeFormatter DB_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public JournalEntry(String username, String content) {
        this.username = username;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    public JournalEntry(long id, String username, String content, LocalDateTime timestamp) {
        this.id = id;
        this.username = username;
        this.content = content;
        this.timestamp = timestamp;
    }

    public JournalEntry(String username, String title, String content) 
    {
        this.username = username;
        this.title = title == null ? "" : title;
        this.content = content == null ? "" : content;
        this.timestamp = LocalDateTime.now();
    }

    public JournalEntry(long id, String username, String title, String content, LocalDateTime timestamp) 
    {
        this.id = id;
        this.username = username;
        this.title = title == null ? "" : title;
        this.content = content;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getTitle() 
    { 
        return title; 
    }

    public String getTimestampString() {
        return timestamp.format(DB_FORMAT);
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) 
    { 
        this.title = title == null ? "" : title; 
    }
}