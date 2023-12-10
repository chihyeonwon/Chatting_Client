package com.djdjsn.emochat.data.emoji;

import java.util.Objects;

public class Emoji {

    private String fileName;
    private String description;


    public Emoji() {}

    public Emoji(String fileName, String description) {
        this.fileName = fileName;
        this.description = description;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Emoji emoji = (Emoji) o;
        return fileName.equals(emoji.fileName) && Objects.equals(description, emoji.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, description);
    }
}
