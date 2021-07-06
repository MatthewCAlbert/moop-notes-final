package com.example.moopnotes.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BasicApi {
    @SerializedName("status")
    String status;
    @SerializedName("data")
    String data;
    @SerializedName("message")
    String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
