package com.example.moopnotes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity {

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(MainActivity.this);

        if( !sessionManager.isLoggedIn() ){
            // Go To Login Page
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else{
            // Go To Notes
            if( !sessionManager.isValid() ){
                // Invalid Token, ask for login again
                sessionManager.logoutSession();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return;
            }
            Intent intent = new Intent(this, NoteListActivity.class);
            startActivity(intent);
            finish();
        }
    }
}