package com.djdjsn.emochat.data.user;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {

    private String uid;
    private String id;
    private String nickname;
    private String phone;
    private long created;

    public User() {
    }

    public User(String uid, String id, String nickname, String phone) {
        this.uid = uid;
        this.id = id;
        this.nickname = nickname;
        this.phone = phone;
        this.created = System.currentTimeMillis();
    }

    public String getUid() {
        return uid;
    }

    public String getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPhone() {
        return phone;
    }

    public long getCreated() {
        return created;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return created == user.created && uid.equals(user.uid) && id.equals(user.id) && nickname.equals(user.nickname) && phone.equals(user.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, id, nickname, phone, created);
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", id='" + id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", phone='" + phone + '\'' +
                ", created=" + created +
                '}';
    }
}





