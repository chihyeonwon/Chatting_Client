package com.djdjsn.emochat.data.message;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Message {

    private String id;
    private String chatId;
    private String uid;
    private String emojiUrl;
    private String content;
    private boolean isRead;
    private long created;

    public Message() {
    }

    public Message(String chatId, String uid, String emojiUrl, @NonNull String content) {
        this.chatId = chatId;
        this.uid = uid;
        this.emojiUrl = emojiUrl;
        this.content = content;
        this.isRead = false;
        this.created = System.currentTimeMillis();
        this.id = created + "-" + chatId + "-" + uid;
    }

    public String getId() {
        return id;
    }

    public String getChatId() {
        return chatId;
    }

    public String getUid() {
        return uid;
    }

    public String getEmojiUrl() {
        return emojiUrl;
    }

    public String getContent() {
        return content;
    }

    public boolean isRead() {
        return isRead;
    }

    public long getCreated() {
        return created;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setEmojiUrl(String emojiUrl) {
        this.emojiUrl = emojiUrl;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return isRead == message.isRead && created == message.created && id.equals(message.id) && chatId.equals(message.chatId) && uid.equals(message.uid) && Objects.equals(emojiUrl, message.emojiUrl) && content.equals(message.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chatId, uid, emojiUrl, content, isRead, created);
    }
}
















