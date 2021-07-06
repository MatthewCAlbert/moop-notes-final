package com.example.moopnotes.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetNoteList {
    @SerializedName("status")
    String status;
    @SerializedName("data")
    List<Note> data;
    @SerializedName("message")
    String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Note> getData() {
        return data;
    }

    public void setData(List<Note> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}