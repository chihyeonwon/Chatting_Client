package com.djdjsn.emochat.data.userrelation;

import java.util.Objects;

public class UserRelation {

    private String uid;
    private String other;

    public UserRelation() {}

    public UserRelation(String uid, String other) {
        this.uid = uid;
        this.other = other;
    }

    public String getUid() {
        return uid;
    }

    public String getOther() {
        return other;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRelation userRelation = (UserRelation) o;
        return uid.equals(userRelation.uid) && other.equals(userRelation.other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, other);
    }
}
