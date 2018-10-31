package no.hiof.internote.internote;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.text.SimpleDateFormat;
import java.util.Date;

import no.hiof.internote.internote.model.NoteDetailed;
import no.hiof.internote.internote.model.NoteOverview;
import no.hiof.internote.internote.model.Settings;

public class NoteImageActivity extends AppCompatActivity {
    private FirebaseUser user;
    private NoteDetailed noteDetailed;
    private EditText textTitle;
    private EditText textLastEdited;
    private EditText textContent;

    private DatabaseReference noteDetailedReference;

    private String currentNoteDetailedKey;
    private String currentNoteOverviewKey;

    private boolean deletingNote = false; // TODO: Has to be a better way, than this

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        Creating the options overflow toolbar menu
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_note, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*
        Handling the tap on the toolbar menu item
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            // Deletes the note
            case R.id.menuItem_overflow1:
                // TODO: Add SnackBar for confirmation
                // TODO: Clean up, so it's not ugly af code
                if(user != null && currentNoteDetailedKey != null && currentNoteOverviewKey != null){
                    // Stops onDestroy from trying to save the document
                    deletingNote = true;

                    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child(user.getUid());

                    DatabaseReference noteDetailedRef = userReference.child(Settings.FIREBASE_NOTE_DETAILED).child(currentNoteDetailedKey);
                    noteDetailedRef.removeValue();

                    DatabaseReference noteOverviewRef = userReference.child(Settings.FIREBASE_NOTE_OVERVIEW).child(currentNoteOverviewKey);
                    noteOverviewRef.removeValue();

                    // Note is deleted, go to MainActivity
                    goToMain();
                }
                break;
            // Goes back to MainActivity
            case R.id.menuItem_overflow2:
                goToMain();
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
        onDestroy method
        Saves the document, unless deletingNote = true
     */
    @Override
    protected void onDestroy() {
        if(!deletingNote)
            saveDocument(this);

        super.onDestroy();
    }

    /*
        Fills all the text fields in the layout
     */
    private void fillFields(){
        textContent.setText(noteDetailed.getContent());

        textLastEdited.setText(new SimpleDateFormat("HH:mm dd-MM-yyyy").format(new Date(noteDetailed.getLastEdited())));

        TextView textTitle = findViewById(R.id.textTitle);
        textTitle.setText(noteDetailed.getTitle());
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
                // TODO: has to be a better way, than using boolean deletingNote
                if(!deletingNote){
                    noteDetailed = dataSnapshot.getValue(NoteDetailed.class);
                    fillFields();
                }
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
        // TODO: Have to save it locally. Not logged in as a user
        if(user == null){
            Toast.makeText(context, "TODO: Save locally", Toast.LENGTH_LONG).show();
            return;
        }

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
        Toast.makeText(context, "Saved note: " + noteDetailedReference.getKey(), Toast.LENGTH_LONG).show();
    }
}