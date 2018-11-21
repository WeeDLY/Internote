package no.hiof.internote.internote;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import no.hiof.internote.internote.model.*;

public class NoteTextActivity extends AppCompatActivity {
    private FirebaseUser user;
    private NoteDetailed noteDetailed;
    private EditText textTitle;
    private EditText textLastEdited;
    private EditText textContent;

    private DatabaseReference noteDetailedReference;

    private String currentNoteDetailedKey;
    private String currentNoteOverviewKey;

    private boolean deleteNote = false;
    private boolean madeChanges = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(Settings.getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_text);

        textTitle = findViewById(R.id.textTitle);
        textLastEdited = findViewById(R.id.textLastEdited);
        textContent = findViewById(R.id.textContent);

        user = FirebaseAuth.getInstance().getCurrentUser();
        currentNoteDetailedKey = getIntent().getStringExtra(Settings.INTENT_NOTEDETAILED_KEY);
        currentNoteOverviewKey = getIntent().getStringExtra(Settings.INTENT_NOTEOVERVIEW_KEY);

        if (currentNoteDetailedKey != null){
            retrieveDocument(currentNoteDetailedKey);
        }
        else{
            noteDetailed = new NoteDetailed("New note", "", System.currentTimeMillis());
            fillFields();
        }
    }

    /*
        TextChanged event. Used for listening for changes in the document
     */
    private class TextChangedListener implements TextWatcher{
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            madeChanges = true;
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    /*
        Creating the options overflow toolbar menu
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_note_text, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*
        Handling the tap on the toolbar menu item
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            // Deletes the note
            case R.id.menuDeleteNote:
                // Note does not exist in Firebase, do nothing.
                if(currentNoteDetailedKey == null){
                    break;
                }

                deleteNote = true;
                if(user != null && currentNoteDetailedKey != null && currentNoteOverviewKey != null){
                    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child(user.getUid());

                    DatabaseReference noteDetailedRef = userReference.child(Settings.FIREBASE_NOTE_DETAILED).child(currentNoteDetailedKey);
                    noteDetailedRef.removeValue();

                    DatabaseReference noteOverviewRef = userReference.child(Settings.FIREBASE_NOTE_OVERVIEW).child(currentNoteOverviewKey);
                    noteOverviewRef.removeValue();
                    goToMain();
                }
                break;
            // Converts NoteText to NoteImage
            case R.id.menuConvertImageNote:
                Intent imageIntent = new Intent(NoteTextActivity.this, NoteImageActivity.class);
                imageIntent.putExtra(Settings.INTENT_NOTEDETAILED_KEY, currentNoteDetailedKey);
                imageIntent.putExtra(Settings.INTENT_NOTEOVERVIEW_KEY, currentNoteOverviewKey);
                startActivity(imageIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
        Takes user to MainActivity
     */
    private void goToMain(){
        Intent intentMain = new Intent(this, MainActivity.class);
        startActivity(intentMain);
    }

    /*
        Android lifecycle: onPause event
     */
    @Override
    protected void onPause() {
        super.onPause();
        if(madeChanges && !deleteNote){
            Log.d("SaveDocument", "SAVED");
            saveDocument(this);
        }
    }

    /*
        Fills all the text fields in the layout
     */
    private void fillFields(){
        textContent.setText(noteDetailed.getContent());

        textLastEdited.setText(new SimpleDateFormat("HH:mm dd-MM-yyyy").format(new Date(noteDetailed.getLastEdited())));

        TextView textTitle = findViewById(R.id.textTitle);
        textTitle.setText(noteDetailed.getTitle());


        // Set events, so we can check if the user made changes to the document
        this.textTitle.addTextChangedListener(new TextChangedListener());
        textContent.addTextChangedListener(new TextChangedListener());
    }

    /*
        Retrieves document information from firebase
     */
    private void retrieveDocument(String documentId){
        FirebaseDatabase databaseReference = FirebaseDatabase.getInstance();
        DatabaseReference documentReference = databaseReference.getReference();
        documentReference.child(user.getUid()).child(Settings.FIREBASE_NOTE_DETAILED).child(documentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noteDetailed = dataSnapshot.getValue(NoteDetailed.class);
                if(noteDetailed == null)
                    noteDetailed = new NoteDetailed("New note", "", System.currentTimeMillis());
                fillFields();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("retrieveDocument.cancel", "something went funky wunky");
            }
        });
    }

    /*
        Saves the current document and moves user to MainActivity
     */
    private void saveDocument(Context context){
        // Update current noteDetailed
        noteDetailed.setTitle(textTitle.getText().toString());
        noteDetailed.setContent(textContent.getText().toString());
        noteDetailed.setLastEdited(System.currentTimeMillis());

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child(user.getUid());
        // Update NoteDetailed to firebase

        // Note does not exist, writes a new NoteDetailed to database
        if(currentNoteDetailedKey == null){
            noteDetailedReference = userReference.child(Settings.FIREBASE_NOTE_DETAILED).push();
        }
        // Note was loaded from a previous note. Overwrite with new data
        else{
            noteDetailedReference = userReference.child(Settings.FIREBASE_NOTE_DETAILED).child(currentNoteDetailedKey);
        }
        noteDetailedReference.setValue(noteDetailed);


        // NoteOver does not exist, writes a new NoteOverview to database
        if(currentNoteOverviewKey == null){
            DatabaseReference noteOverviewReference = userReference.child(Settings.FIREBASE_NOTE_OVERVIEW).push();
            noteOverviewReference.setValue(new NoteOverview(noteDetailed, noteDetailedReference.getKey()));

        }
        // Update NoteOverview to firebase
        else{
            DatabaseReference noteOverviewReference = userReference.child(Settings.FIREBASE_NOTE_OVERVIEW).child(currentNoteOverviewKey);
            noteOverviewReference.child("lastEdited").setValue(noteDetailed.getLastEdited());
            noteOverviewReference.child("title").setValue(noteDetailed.getTitle());
        }

        // Display that it was saved and auto-moves user to MainActivity
        Toast.makeText(context, "Saved note: " + noteDetailed.getTitle(), Toast.LENGTH_LONG).show();
    }

    /*
        GoToMain Menu
     */
    public void btnBackOnClick(View view) {
        goToMain();
    }
}