package com.djdjsn.emochat.data.messagenotice;

public class MessageNotice {

    private String messageId;
    private String chatId;
    private String senderUid;
    private String receiverUid;
    private long created;

    public MessageNotice() {
    }

    public MessageNotice(String messageId, String chatId, String senderUid, String receiverUid) {
        this.messageId = messageId;
        this.chatId = chatId;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.created = System.currentTimeMillis();
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }
}
