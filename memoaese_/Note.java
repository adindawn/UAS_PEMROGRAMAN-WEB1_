package com.example.memoaese_;

public class Note {
    private long id;
    private String title;
    private String content;

    // Konstruktor
    public Note(long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    // Getter (dan Setter jika diperlukan nanti)
    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
