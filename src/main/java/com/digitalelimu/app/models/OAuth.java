package com.digitalelimu.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OAuth {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("phone")
    @Expose
    private String phone;

    @SerializedName("password")
    @Expose
    private String password;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("agent")
    @Expose
    private String agent;

    @SerializedName("accesstoken")
    @Expose
    private String accesstoken;

    @SerializedName("resetcode")
    @Expose
    private String resetcode;

    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public OAuth(String phone, String password, String email,String agent) {
        super();
        this.phone = phone;
        this.password = password;
        this.email = email;
        this.agent = agent;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAccesstoken() {
        return accesstoken;
    }

    public void setAccessToken(String accessToken) {
        this.accesstoken = accessToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getResetcode() {
        return resetcode;
    }

    public void setResetcode(String resetcode) {
        this.resetcode = resetcode;
    }

    public Object getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Object getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}