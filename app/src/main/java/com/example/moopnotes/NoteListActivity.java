package com.example.moopnotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.moopnotes.adapter.NoteAdapter;
import com.example.moopnotes.model.GetNoteList;
import com.example.moopnotes.model.Note;
import com.example.moopnotes.rest.ApiClient;
import com.example.moopnotes.rest.ApiInterface;
import com.google.gson.JsonObject;

import net.steamcrafted.materialiconlib.MaterialMenuInflater;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoteListActivity extends AppCompatActivity {
    Button btIns, logoutBtn;
    ApiInterface mApiInterface;
    SessionManager sessionManager;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public static NoteListActivity ma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);

        sessionManager = new SessionManager(NoteListActivity.this);

        btIns = (Button) findViewById(R.id.addButton);
        btIns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoteListActivity.this, NoteEditActivity.class);
                intent.putExtra("noteId", "");
                startActivity(intent);
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.noteListRecyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        ma=this;
        refresh();
    }

    @Override
    public void onResume(){
        super.onResume();
        refresh();
    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.note_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logoutButton) {
            sessionManager.logoutSession();
            startActivity(new Intent(NoteListActivity.this, MainActivity.class));
            finish();
        }else if( id == R.id.syncButton ){
            this.refresh();
        }else if( id == R.id.accountButton ){
            startActivity(new Intent(NoteListActivity.this, AccountActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }


    public void refresh() {
        Call<GetNoteList> noteListCall = mApiInterface.allNote(sessionManager.getToken());
        noteListCall.enqueue(new Callback<GetNoteList>() {
            @Override
            public void onResponse(Call<GetNoteList> call, Response<GetNoteList>
                    response) {
                if( response.body() != null && response.isSuccessful() ){
                    List<Note> noteList = response.body().getData();
                    Log.d("Retrofit Get", "Jumlah data Kontak: " +
                            String.valueOf(noteList.size()));
                    mAdapter = new NoteAdapter(noteList);
                    mRecyclerView.setAdapter(mAdapter);
                }else{
                    Toast.makeText(NoteListActivity.this, "Failed to fetch", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetNoteList> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());
                Toast.makeText(NoteListActivity.this, "Failed to fetch", Toast.LENGTH_SHORT).show();
            }
        });
    }
}