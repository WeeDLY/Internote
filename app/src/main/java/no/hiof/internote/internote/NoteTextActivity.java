package no.hiof.internote.internote;

import java.util.Date;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import no.hiof.internote.internote.model.*;
import okhttp3.internal.cache.DiskLruCache;

public class NoteTextActivity extends AppCompatActivity {
    private FirebaseUser firebaseUser;
    private NoteDetailed noteDetailed;
    private EditText textContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_text);

        firebaseUser = getIntent().getParcelableExtra(Settings.FIREBASEUSER_INTENT);
        if(firebaseUser != null){
            TextView textTitle = findViewById(R.id.textTitle);
            textTitle.setText(firebaseUser.getUid());
        }

        // TODO: This is just temp to try to read data
        String TEMPORARY_UID = "-LOSLijhtzuSjudQTAFU";
        retrieveDocument(TEMPORARY_UID);

        noteDetailed = new NoteDetailed("BasicNote", new Date());

        EditText textCreationDate = findViewById(R.id.textCreationDate);
        textCreationDate.setText(noteDetailed.getCreationDate().toString());
        textContent = findViewById(R.id.textContent);
    }

    private void retrieveDocument(final String documentId){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference documentReference = databaseReference.child(firebaseUser.getUid()).child(Settings.FIREBASE_NOTE_DETAILED);
        documentReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    Log.d("GotNoteDetailed", "testooni: " + snap.getKey());
                    Log.d("GotNoteDetailed", "testooni: " + snap.getValue());
                    //NoteDetailed noteDetailed = snap.getValue(NoteDetailed.class);
                    if(noteDetailed == null){
                        Log.d("GotNoteDetailed","WRONG" );
                        return;
                    }
                    Log.d("GotNoteDetailed","CORRECT" );
                    textContent.setText(noteDetailed.getContent());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /*
        Button: Takes you back to MainActivity
     */
    public void btnMainMenu(View view) {
        Intent intent = new Intent(view.getContext(), MainActivity.class);
        startActivity(intent);
    }

    /*
        Button: Saves your current noteDetailed to firebase, also takes you back to MainActivity afterwards
     */
    public void BtnSave(View view) {
        // TODO: Have to save it locally
        if(firebaseUser == null){
            Toast.makeText(view.getContext(), "TODO: Save locally", Toast.LENGTH_LONG).show();
            return;
        }

        noteDetailed.setContent(textContent.getText().toString());

        // Saves everything to firebase under the user
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child(firebaseUser.getUid());

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