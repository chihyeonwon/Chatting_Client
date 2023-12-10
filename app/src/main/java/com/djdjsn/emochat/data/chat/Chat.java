package com.djdjsn.emochat.data.chat;

import java.util.Objects;

public class Chat {

    private String id;
    private String uid1;
    private String uid2;
    private long created;

    public Chat() {}

    public Chat(String uid1, String uid2) {
        this.id = uid1 + "-" + uid2;
        this.uid1 = uid1;
        this.uid2 = uid2;
        this.created = System.currentTimeMillis();
    }

    public boolean hasUser(String uid) {
        return uid1.equals(uid) || uid2.equals(uid);
    }

    public String getId() {
        return id;
    }

    public String getUid1() {
        return uid1;
    }

    public String getUid2() {
        return uid2;
    }

    public long getCreated() {
        return created;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUid1(String uid1) {
        this.uid1 = uid1;
    }

    public void setUid2(String uid2) {
        this.uid2 = uid2;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chat chat = (Chat) o;
        return created == chat.created && id.equals(chat.id) && uid1.equals(chat.uid1) && uid2.equals(chat.uid2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uid1, uid2, created);
    }
}














