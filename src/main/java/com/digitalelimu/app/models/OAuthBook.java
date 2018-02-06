package com.digitalelimu.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OAuthBook {
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("accesstoken")
    @Expose
    private String accesstoken;
    @SerializedName("bookid")
    @Expose
    private String bookid;

    public OAuthBook(String phone, String accesstoken, String bookid) {
        super();
        this.phone = phone;
        this.accesstoken = accesstoken;
        this.bookid = bookid;
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

    public void setAccesstoken(String accesstoken) {
        this.accesstoken = accesstoken;
    }

    public void setBookid(String bookid) {
        this.bookid = bookid;
    }

    public String getBookid() {
        return bookid;
    }
}
