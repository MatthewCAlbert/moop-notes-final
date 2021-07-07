package com.example.moopnotes;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moopnotes.model.DeleteNote;
import com.example.moopnotes.model.EditNote;
import com.example.moopnotes.model.Note;
import com.example.moopnotes.rest.ApiClient;
import com.example.moopnotes.rest.ApiInterface;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoteEditActivity extends AppCompatActivity {

    private String noteId = "";
    private boolean autoSave = false;
    private boolean isSaved = true;
    private SwipeRefreshLayout listMenuRefresh;
    private EditText titleInput;
    private EditText contentInput;
    private TextView noteUpdatedAt;
    private TextView conflictWarningText;
    ApiInterface apiInterface;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);

        sessionManager = new SessionManager(NoteEditActivity.this);

        this.titleInput = findViewById(R.id.inputTitle);
        this.contentInput = findViewById(R.id.inputContent);
        this.noteUpdatedAt = findViewById(R.id.noteUpdatedAt);
        this.conflictWarningText = findViewById(R.id.conflictWarning);

        Intent intent = getIntent();
        this.noteId = intent.getStringExtra("id");
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        String updatedAt = intent.getStringExtra("updatedAt");

        updatedAt = updatedAt != null ? convertDate(updatedAt) : "never";

        this.titleInput.setText(title);
        this.contentInput.setText(content);
        this.noteUpdatedAt.setText(updatedAt);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        setTitle("Edit Note");

        // Add Input Listener
        this.titleInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                NoteEditActivity.this.isSaved = false;
                if( NoteEditActivity.this.autoSave ) saveAction(false);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0){

                }
            }
        });

        this.contentInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                NoteEditActivity.this.isSaved = false;
                if( NoteEditActivity.this.autoSave ) saveAction(false);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0){

                }
            }
        });

        listMenuRefresh = findViewById(R.id.refreshList);
        listMenuRefresh.setEnabled(false);

    }

    @Override
    public void onResume(){
        super.onResume();
        // put your code here...

    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.note_edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.deleteButton) {
            if( this.noteId != null && !this.noteId.isEmpty() )
                onDeleteConfirmation();
            else
                Toast.makeText(NoteEditActivity.this, "This note haven't been saved yet.", Toast.LENGTH_SHORT).show();
        }else if( id == R.id.saveButton ){
            if( !this.isSaved )
                saveAction(true);
        }else if( id == R.id.syncButton ){
            if( this.noteId != null && !this.noteId.isEmpty() )
                syncAction(true);
            else
                Toast.makeText(NoteEditActivity.this, "This note haven't been saved before.", Toast.LENGTH_SHORT).show();
        }else if( id == android.R.id.home ){
            this.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onVersionChangeConfirmation(Note note, boolean verbose){
        this.isSaved = false;
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_round_warning_24)
                .setTitle("Overwrite Note?")
                .setMessage("Are you sure you want to overwrite this note?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        replaceNoteEditor(note);
                        if( verbose ) Toast.makeText(NoteEditActivity.this, "Sync Success!", Toast.LENGTH_SHORT).show();
                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        conflictWarningText.setVisibility(View.VISIBLE);
                        if( verbose ) Toast.makeText(NoteEditActivity.this, "Sync Cancelled!", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    public void replaceNoteEditor(Note note){
        if( note != null ){
            String newTitle = note.getTitle();
            String newContent = note.getContent();
            String newUpdatedAt = note.getUpdatedAt();
            this.titleInput.setText(newTitle);
            this.contentInput.setText(newContent);
            this.noteUpdatedAt.setText(convertDate(newUpdatedAt));
            this.conflictWarningText.setVisibility(View.GONE);
            this.isSaved = true;
        }
    }

    public void syncAction(boolean verbose){
        listMenuRefresh.setRefreshing(true);

        String token = sessionManager.getToken();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        if( this.noteId != null && !this.noteId.isEmpty() ){
            try {
                Call<EditNote> showNoteCall = apiInterface.showNote(token, this.noteId);
                showNoteCall.enqueue(new Callback<EditNote>() {
                    @Override
                    public void onResponse(Call<EditNote> call, Response<EditNote> response) {
                        if(response.body() != null && response.isSuccessful() ){
                            Note curNote = response.body().getData();
                            if( curNote != null ){
                                String serverTitle = curNote.getTitle();
                                String serverContent = curNote.getContent();
                                String localTitle = titleInput.getText().toString();
                                String localContent = contentInput.getText().toString();
                                if( !serverTitle.equals(localTitle) || !serverContent.equals(localContent) ){
                                    if( verbose )
                                        onVersionChangeConfirmation(curNote, verbose);
                                    else{
                                        // TODO: Complex autosave
                                    }
                                }else{
                                    replaceNoteEditor(curNote);
                                    NoteEditActivity.this.isSaved = true;
                                    if( verbose ) Toast.makeText(NoteEditActivity.this, "Sync Success!", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                if( verbose ) Toast.makeText(NoteEditActivity.this, "Sync Went Wrong!", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            if( verbose ) Toast.makeText(NoteEditActivity.this, "Sync Failed!", Toast.LENGTH_SHORT).show();
                        }
                        listMenuRefresh.setRefreshing(false);
                    }
                    @Override
                    public void onFailure(Call<EditNote> call, Throwable t) {
                        if( verbose ) Toast.makeText(NoteEditActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        listMenuRefresh.setRefreshing(false);
                    }
                });
            } catch (JsonIOException e) {
                e.printStackTrace();
                listMenuRefresh.setRefreshing(false);
            }
        }
    }

    public void saveAction(boolean verbose){
        this.conflictWarningText.setVisibility(View.GONE);
        listMenuRefresh.setRefreshing(true);
        EditText titleInput = findViewById(R.id.inputTitle);
        EditText contentInput = findViewById(R.id.inputContent);
        TextView noteUpdatedAt = findViewById(R.id.noteUpdatedAt);

        String title = titleInput.getText().toString();
        String content = contentInput.getText().toString();

        if( title.matches("") || content.matches("") ){
            if( verbose) Toast.makeText(NoteEditActivity.this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = sessionManager.getToken();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject paramObject = new JsonObject();
        paramObject.addProperty("title", title);
        paramObject.addProperty("content", content);

        if( this.noteId == null || this.noteId.isEmpty() ){
            // POST New Note
            try {
                Call<EditNote> postNoteCall = apiInterface.postNote(token, paramObject);
                postNoteCall.enqueue(new Callback<EditNote>() {
                    @Override
                    public void onResponse(Call<EditNote> call, Response<EditNote> response) {
                        if(response.body() != null && response.isSuccessful() ){
                            Note newNote = response.body().getData();
                            if( newNote != null ){
                                NoteEditActivity.this.noteId = newNote.getId();

                                String newUpdatedAt = newNote.getUpdatedAt();
                                noteUpdatedAt.setText(convertDate(newUpdatedAt));

                                if( verbose ) Toast.makeText(NoteEditActivity.this, "Saving New Note Success!", Toast.LENGTH_SHORT).show();
                                NoteEditActivity.this.isSaved = true;
                            }else{
                                if( verbose ) Toast.makeText(NoteEditActivity.this, "Saving New Note Went Wrong!", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            if( verbose ) Toast.makeText(NoteEditActivity.this, "Saving New Note Failed!", Toast.LENGTH_SHORT).show();
                        }
                        listMenuRefresh.setRefreshing(false);
                    }
                    @Override
                    public void onFailure(Call<EditNote> call, Throwable t) {
                        if( verbose ) Toast.makeText(NoteEditActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        listMenuRefresh.setRefreshing(false);
                    }
                });
            } catch (JsonIOException e) {
                e.printStackTrace();
                listMenuRefresh.setRefreshing(false);
            }

        }else{
            // PUT Edit Existing Note
            try {
                Call<EditNote> updateNoteCall = apiInterface.updateNote(token, this.noteId, paramObject);
                updateNoteCall.enqueue(new Callback<EditNote>() {
                    @Override
                    public void onResponse(Call<EditNote> call, Response<EditNote> response) {
                        if(response.body() != null && response.isSuccessful() ){
                            Note curNote = response.body().getData();
                            if( curNote != null ){
                                String newUpdatedAt = curNote.getUpdatedAt();
                                noteUpdatedAt.setText(convertDate(newUpdatedAt));

                                if( verbose ) Toast.makeText(NoteEditActivity.this, "Save Success!", Toast.LENGTH_SHORT).show();
                                NoteEditActivity.this.isSaved = true;
                                syncAction(false);
                            }else{
                                if( verbose ) Toast.makeText(NoteEditActivity.this, "Save Went Wrong!", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            if( verbose ) Toast.makeText(NoteEditActivity.this, "Save Failed!", Toast.LENGTH_SHORT).show();
                        }
                        listMenuRefresh.setRefreshing(false);
                    }
                    @Override
                    public void onFailure(Call<EditNote> call, Throwable t) {
                        if( verbose ) Toast.makeText(NoteEditActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        listMenuRefresh.setRefreshing(false);
                    }
                });
            } catch (JsonIOException e) {
                e.printStackTrace();
                listMenuRefresh.setRefreshing(false);
            }

        }
    }

    public void deleteAction(){
        listMenuRefresh.setRefreshing(true);
        System.out.println("deleted");

        if( this.noteId == null || this.noteId.isEmpty() ){
            Toast.makeText(NoteEditActivity.this, "This note haven't been saved before.", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = sessionManager.getToken();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        try {
            Call<DeleteNote> deleteNoteCall = apiInterface.deleteNote(token, this.noteId);
            deleteNoteCall.enqueue(new Callback<DeleteNote>() {
                @Override
                public void onResponse(Call<DeleteNote> call, Response<DeleteNote> response) {
                    if(response.body() != null && response.isSuccessful() ){
                        Toast.makeText(NoteEditActivity.this, "Delete Success!", Toast.LENGTH_SHORT).show();

                        // Redirect to Note List
                        Intent intent = new Intent(NoteEditActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(NoteEditActivity.this, "Delete Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<DeleteNote> call, Throwable t) {
                    Toast.makeText(NoteEditActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JsonIOException e) {
            e.printStackTrace();
            listMenuRefresh.setRefreshing(false);
        }

    }

    public void onDeleteConfirmation(){
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_round_warning_24)
                .setTitle("Delete Note")
                .setMessage("Are you sure you want to delete this note?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAction();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    // Prevent Default
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if( !this.isSaved){
            builder.setTitle("Discard Change?");
            builder.setMessage("Do you want to discard change in this note? ");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    NoteEditActivity.super.onBackPressed();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //
                }
            });
            builder.show();
        }else{
            super.onBackPressed();
        }
    }

    private String convertDate(String src){
        try{
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:sss");
            Date dt = input.parse(src);
            SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String formattedDate = output.format(dt);
            return formattedDate;
        }catch(Exception e){

        }
        return src;
    }
}