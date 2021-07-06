package com.example.moopnotes;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.moopnotes.model.AuthCheck;
import com.example.moopnotes.model.Login;
import com.example.moopnotes.model.LoginData;
import com.example.moopnotes.rest.ApiClient;
import com.example.moopnotes.rest.ApiInterface;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SessionManager {

    ApiInterface apiInterface;
    private boolean valid;

    private Context _context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public static final String IS_LOGGED_IN = "isLoggedIn";
    public static final String USER_ID = "user_id";
    public static final String USERNAME = "username";
    public static final String CREATED_AT = "createdAt";
    public static final String TOKEN = "user_token";

    public SessionManager (Context context){
        this._context = context;
        this.valid = true;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
    }

    public boolean checkTokenValidity(){
        String token = getToken();

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<AuthCheck> authCheckCall = apiInterface.checkAuth(token);
        authCheckCall.enqueue(new Callback<AuthCheck>() {
            @Override
            public void onResponse(Call<AuthCheck> call, Response<AuthCheck> response) {
                if(response.body() != null  ) {
                    if(response.isSuccessful() && response.body().isSuccess()){
                        SessionManager.this.setValid(true);
                    }else{
                        SessionManager.this.setValid(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthCheck> call, Throwable t) {

            }
        });

        return isValid();
    }

    public void createLoginSession(LoginData user){
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString(USER_ID, user.getId());
        editor.putString(USERNAME, user.getUsername());
        editor.putString(TOKEN, user.getToken());
        editor.putString(CREATED_AT, user.getCreatedAt());
        editor.commit();
    }

    public String getToken(){
        return sharedPreferences.getString(TOKEN,null);
    }

    public HashMap<String,String> getUserDetail(){
        HashMap<String,String> user = new HashMap<>();
        user.put(USER_ID, sharedPreferences.getString(USER_ID,null));
        user.put(USERNAME, sharedPreferences.getString(USERNAME,null));
        user.put(CREATED_AT, sharedPreferences.getString(CREATED_AT,null));
        return user;
    }

    public void logoutSession(){
        editor.clear();
        editor.commit();
    }

    public boolean isLoggedIn(){
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false);
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}