package com.example.moopnotes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moopnotes.model.Login;
import com.example.moopnotes.model.LoginData;
import com.example.moopnotes.model.User;
import com.example.moopnotes.rest.ApiClient;
import com.example.moopnotes.rest.ApiInterface;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    ApiInterface apiInterface;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    protected void setButtonStatus(boolean status){
        Button button1 = findViewById(R.id.loginButton);
        Button button2 = findViewById(R.id.goToRegisterButton);

        button1.setEnabled(status);
        button2.setEnabled(status);
    }

    public void onClickLogin(View view){
        setButtonStatus(false);
        EditText usernameInput = findViewById(R.id.inputUsername);
        EditText passwordInput = findViewById(R.id.inputPassword);

        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        if( username.matches("") || password.matches("") ){
            Toast.makeText(LoginActivity.this, "Please fill required input!", Toast.LENGTH_SHORT).show();
            setButtonStatus(true);
            return;
        }

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        try {
            JsonObject paramObject = new JsonObject();
            paramObject.addProperty("username", username);
            paramObject.addProperty("password", password);

            Call<Login> loginCall = apiInterface.login(paramObject);
            loginCall.enqueue(new Callback<Login>() {
                @Override
                public void onResponse(Call<Login> call, Response<Login> response) {
                    if(response.body() != null && response.isSuccessful() ){
                        String token = response.body().getToken();
                        String expiresIn = response.body().getExpiresIn();
                        User user = response.body().getUser();

                        sessionManager = new SessionManager(LoginActivity.this);
                        LoginData loginData = new LoginData();
                        loginData.setUsername(user.getUsername());
                        loginData.setId(user.getId());
                        loginData.setToken(token);
                        loginData.setCreatedAt(user.getCreatedAt());

                        sessionManager.createLoginSession(loginData);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(LoginActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Login> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JsonIOException e) {
            e.printStackTrace();
        }
        setButtonStatus(true);
    }

    public void onClickGoToRegister(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }
}