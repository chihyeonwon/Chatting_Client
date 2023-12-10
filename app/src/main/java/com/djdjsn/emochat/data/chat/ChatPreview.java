package com.djdjsn.emochat.data.chat;

import com.djdjsn.emochat.data.chatannotation.ChatAnnotation;
import com.djdjsn.emochat.data.user.User;

import java.util.Objects;

public class ChatPreview {

    private String chatId;
    private User recentUser;
    private String recentMessageContent;
    private long recentMessageMillis;

    public ChatPreview(String chatId, User recentUser, String recentMessageContent, long recentMessageMillis) {
        this.chatId = chatId;
        this.recentUser = recentUser;
        this.recentMessageContent = recentMessageContent;
        this.recentMessageMillis = recentMessageMillis;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public User getRecentUser() {
        return recentUser;
    }

    public void setRecentUser(User recentUser) {
        this.recentUser = recentUser;
    }

    public String getRecentMessageContent() {
        return recentMessageContent;
    }

    public void setRecentMessageContent(String recentMessageContent) {
        this.recentMessageContent = recentMessageContent;
    }

    public long getRecentMessageMillis() {
        return recentMessageMillis;
    }

    public void setRecentMessageMillis(long recentMessageMillis) {
        this.recentMessageMillis = recentMessageMillis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatPreview that = (ChatPreview) o;
        return recentMessageMillis == that.recentMessageMillis && chatId.equals(that.chatId) && Objects.equals(recentUser, that.recentUser) && recentMessageContent.equals(that.recentMessageContent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, recentUser, recentMessageContent, recentMessageMillis);
    }
}
