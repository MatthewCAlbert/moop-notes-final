package com.example.moopnotes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.moopnotes.model.Login;
import com.example.moopnotes.model.LoginData;
import com.example.moopnotes.model.Register;
import com.example.moopnotes.model.User;
import com.example.moopnotes.rest.ApiClient;
import com.example.moopnotes.rest.ApiInterface;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    ApiInterface apiInterface;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.no_auth_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if ( id == R.id.aboutButton ){
            startActivity(new Intent(RegisterActivity.this, AboutActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    protected void setButtonStatus(boolean status){
        Button button1 = findViewById(R.id.registerButton);
        Button button2 = findViewById(R.id.goToLoginButton);

        button1.setEnabled(status);
        button2.setEnabled(status);
    }

    public void onClickRegister(View view){
        setButtonStatus(false);
        EditText usernameInput = findViewById(R.id.inputUsername);
        EditText passwordInput = findViewById(R.id.inputPassword);

        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        if( username.matches("") || password.matches("") ){
            Toast.makeText(RegisterActivity.this, "Please fill required input!", Toast.LENGTH_SHORT).show();
            setButtonStatus(true);
            return;
        }

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        try {
            JsonObject paramObject = new JsonObject();
            paramObject.addProperty("username", username);
            paramObject.addProperty("password", password);

            Call<Register> loginCall = apiInterface.register(paramObject);
            loginCall.enqueue(new Callback<Register>() {
                @Override
                public void onResponse(Call<Register> call, Response<Register> response) {
                    if(response.body() != null && response.isSuccessful() ){
                        String token = response.body().getToken();
                        String expiresIn = response.body().getExpiresIn();
                        User user = response.body().getUser();

                        sessionManager = new SessionManager(RegisterActivity.this);
                        LoginData loginData = new LoginData();
                        loginData.setUsername(user.getUsername());
                        loginData.setId(user.getId());
                        loginData.setToken(token);
                        loginData.setCreatedAt(user.getCreatedAt());

                        sessionManager.createLoginSession(loginData);

                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(RegisterActivity.this, "Register Failed!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Register> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JsonIOException e) {
            e.printStackTrace();
        }
        setButtonStatus(true);
    }

    public void onClickGoToLogin(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}