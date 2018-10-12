package no.hiof.internote.internote;

import java.util.Date;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    private EditText textContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_text);

        textContent = findViewById(R.id.textContent);

        user = getIntent().getParcelableExtra(Settings.FIREBASEUSER_INTENT);
        if(user != null){
            TextView textTitle = findViewById(R.id.textTitle);
            textTitle.setText(user.getUid());
            noteDetailed = new NoteDetailed("New note", "", System.currentTimeMillis());
            // TODO: This is just temp to try to read data | MÅ VÆRE b@gmail.com brukeren
            String TEMPORARY_UID = "-LOcLHrBkPA2rsREJ5B6";
            retrieveDocument(TEMPORARY_UID);
        }
        else{
            noteDetailed = new NoteDetailed("New note", "content", System.currentTimeMillis());
            FillFields();
        }
    }

    private void FillFields(){
        textContent.setText(noteDetailed.getContent());

        EditText textCreationDate = findViewById(R.id.textCreationDate);
        textCreationDate.setText(new Date(noteDetailed.getCreationDate() * 1000).toString());

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
                noteDetailed = dataSnapshot.getValue(NoteDetailed.class);
                FillFields();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("retrieveDocument.cancel", "something went funky wunky");
            }
        });
    }

    /*
        Button: Takes you back to MainActivity
     */
    public void btnMainMenu(View view) {
        Intent intentMain = new Intent(view.getContext(), MainActivity.class);
        intentMain.putExtra(Settings.FIREBASEUSER_INTENT, user);
        startActivity(intentMain);
    }

    /*
        Button: Saves your current noteDetailed to firebase, also takes you back to MainActivity afterwards
     */
    public void btnSave(View view) {
        // TODO: Have to save it locally
        if(user == null){
            Toast.makeText(view.getContext(), "TODO: Save locally", Toast.LENGTH_LONG).show();
            return;
        }

        noteDetailed.setContent(textContent.getText().toString());

        // Saves everything to firebase under the user
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child(user.getUid());

        // Saves detailed information about the note
        DatabaseReference noteDetailedReference = userReference.child(Settings.FIREBASE_NOTE_DETAILED).push();
        noteDetailedReference.setValue(noteDetailed);

        // Saves the note overview
        DatabaseReference noteOverviewReference = userReference.child(Settings.FIREBASE_NOTE_OVERVIEW).push();
        noteOverviewReference.setValue(new NoteOverview(noteDetailed, noteDetailedReference.getKey()));

        // Display that it was saved and auto-moves user to MainActivity
        Toast.makeText(view.getContext(), "Saved note: " + noteDetailedReference.getKey(), Toast.LENGTH_LONG).show();
        Intent mainIntent = new Intent(view.getContext(), MainActivity.class);
        startActivity(mainIntent);
    }
}