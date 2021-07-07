package com.example.moopnotes.rest;

import com.example.moopnotes.model.AuthCheck;
import com.example.moopnotes.model.BasicApi;
import com.example.moopnotes.model.DeleteNote;
import com.example.moopnotes.model.EditNote;
import com.example.moopnotes.model.GetNoteList;
import com.example.moopnotes.model.Login;
import com.example.moopnotes.model.Register;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;
import retrofit2.http.Path;

public interface ApiInterface {
    // Auth Routes
    @POST("login")
    @Headers({"Content-Type: application/json"})
    Call<Login> login(@Body JsonObject body);

    @POST("register")
    @Headers({"Content-Type: application/json"})
    Call<Register> register(@Body JsonObject body);

    @GET("auth/check")
    Call<AuthCheck> checkAuth(@Header("Authorization") String auth);

    @PUT("users/change-password")
    @Headers({"Content-Type: application/json"})
    Call<Login> changePassword(@Header("Authorization") String auth, @Body JsonObject body);

    // Notes Routes
    @GET("notes/all")
    Call<GetNoteList> allNote(@Header("Authorization") String auth);

    @POST("notes")
    @Headers({"Content-Type: application/json"})
    Call<EditNote> postNote(@Header("Authorization") String auth, @Body JsonObject body);

    @GET("notes/{id}")
    Call<EditNote> showNote(@Header("Authorization") String auth, @Path("id") String id);

    @PUT("notes/{id}")
    @Headers({"Content-Type: application/json"})
    Call<EditNote> updateNote(@Header("Authorization") String auth, @Path("id") String id, @Body JsonObject body);

    @DELETE("notes/{id}")
    Call<DeleteNote> deleteNote(@Header("Authorization") String auth, @Path("id") String id);
}