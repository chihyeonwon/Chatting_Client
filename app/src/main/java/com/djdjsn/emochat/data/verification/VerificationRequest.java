package com.djdjsn.emochat.data.verification;

public class VerificationRequest {

    private String id;
    private String phone;
    private String code;
    private long created;


    public VerificationRequest() {}

    public VerificationRequest(String phone, String code) {
        this.phone = phone;
        this.code = code;
        this.created = System.currentTimeMillis();
        this.id = created + "-" + phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

}
