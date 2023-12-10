package com.djdjsn.emochat.data.chatannotation;

import java.util.Objects;

public class ChatAnnotation {

    private String chatId;
    private String recentUserUid;
    private String recentMessageContent;
    private long recentMessageMillis;


    public ChatAnnotation() {

    }

    public ChatAnnotation(String chatId, String recentUserUid, String recentMessageContent, long recentMessageMillis) {
        this.chatId = chatId;
        this.recentUserUid = recentUserUid;
        this.recentMessageContent = recentMessageContent;
        this.recentMessageMillis = recentMessageMillis;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getRecentUserUid() {
        return recentUserUid;
    }

    public void setRecentUserUid(String recentUserUid) {
        this.recentUserUid = recentUserUid;
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
        ChatAnnotation that = (ChatAnnotation) o;
        return recentMessageMillis == that.recentMessageMillis && chatId.equals(that.chatId) && recentUserUid.equals(that.recentUserUid) && recentMessageContent.equals(that.recentMessageContent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, recentUserUid, recentMessageContent, recentMessageMillis);
    }

    @Override
    public String toString() {
        return "ChatAnnotation{" +
                "chatId='" + chatId + '\'' +
                ", recentUserUid='" + recentUserUid + '\'' +
                ", recentMessageContent='" + recentMessageContent + '\'' +
                ", recentMessageMillis=" + recentMessageMillis +
                '}';
    }
}
