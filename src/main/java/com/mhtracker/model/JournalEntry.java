package com.mhtracker.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JournalEntry {

    private long id;
    private String username;
    private String content;
    private LocalDateTime timestamp;

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

    public String getTimestampString() {
        return timestamp.format(DB_FORMAT);
    }

    public void setId(long id) {
        this.id = id;
    }
}