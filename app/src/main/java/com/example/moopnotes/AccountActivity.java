package com.example.moopnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moopnotes.model.BasicApi;
import com.example.moopnotes.model.EditNote;
import com.example.moopnotes.model.Login;
import com.example.moopnotes.model.LoginData;
import com.example.moopnotes.model.Note;
import com.example.moopnotes.model.User;
import com.example.moopnotes.rest.ApiClient;
import com.example.moopnotes.rest.ApiInterface;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountActivity extends AppCompatActivity {

    ApiInterface apiInterface;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        sessionManager = new SessionManager(AccountActivity.this);
        HashMap<String,String> userDetail = sessionManager.getUserDetail();

        TextView usernameText = findViewById(R.id.usernameText);
        TextView createdAtText = findViewById(R.id.createdAtText);

        usernameText.setText(userDetail.get(sessionManager.USERNAME));
        createdAtText.setText(userDetail.get(sessionManager.CREATED_AT));

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void setButtonStatus(boolean status){
        Button button1 = findViewById(R.id.changePasswordButton);

        button1.setEnabled(status);
    }

    public void onClickChangePassword(View view){
        setButtonStatus(false);
        EditText oldPasswordInput = findViewById(R.id.oldPasswordInput);
        EditText newPasswordInput = findViewById(R.id.newPasswordInput);
        EditText reNewPasswordInput = findViewById(R.id.reNewPasswordInput);

        String oldpass = oldPasswordInput.getText().toString();
        String newpass = newPasswordInput.getText().toString();
        String repass = reNewPasswordInput.getText().toString();

        if( oldpass.matches("") || newpass.matches("") || repass.matches("") ){
            Toast.makeText(AccountActivity.this, "Please fill required input!", Toast.LENGTH_SHORT).show();
            setButtonStatus(true);
            return;
        }

        if( !repass.equals(newpass) ){
            Toast.makeText(AccountActivity.this, "New password doesn't match", Toast.LENGTH_SHORT).show();
            setButtonStatus(true);
            return;
        }

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        try {
            JsonObject paramObject = new JsonObject();
            paramObject.addProperty("oldPassword", oldpass);
            paramObject.addProperty("newPassword", newpass);
            paramObject.addProperty("rePassword", repass);

            String token = sessionManager.getToken();

            Call<Login> changePasswordCall = apiInterface.changePassword(token, paramObject);
            changePasswordCall.enqueue(new Callback<Login>() {
                @Override
                public void onResponse(Call<Login> call, Response<Login> response) {
                    if(response.body() != null && response.isSuccessful() ){
                        String token = response.body().getToken();
                        String expiresIn = response.body().getExpiresIn();
                        User user = response.body().getUser();

                        sessionManager = new SessionManager(AccountActivity.this);
                        LoginData loginData = new LoginData();
                        loginData.setUsername(user.getUsername());
                        loginData.setId(user.getId());
                        loginData.setToken(token);
                        loginData.setCreatedAt(user.getCreatedAt());

                        sessionManager.createLoginSession(loginData);

                        Toast.makeText(AccountActivity.this, "Change Password Success!", Toast.LENGTH_SHORT).show();

                        finish();
                    }else{
                        Toast.makeText(AccountActivity.this, "Change Password Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Login> call, Throwable t) {
                    Toast.makeText(AccountActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JsonIOException e) {
            e.printStackTrace();
        }
        setButtonStatus(true);
    }

}