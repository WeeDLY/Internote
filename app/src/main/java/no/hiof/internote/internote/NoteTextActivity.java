package no.hiof.internote.internote;

import java.util.Date;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import no.hiof.internote.internote.model.*;

public class NoteTextActivity extends AppCompatActivity {
    private FirebaseUser firebaseUser;
    private NoteDetailed noteDetailed;
    private EditText editTextContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_text);

        firebaseUser = getIntent().getParcelableExtra(Settings.FIREBASEUSER_INTENT);
        if(firebaseUser != null){
            TextView textTitle = findViewById(R.id.textTitle);
            textTitle.setText(firebaseUser.getUid());
        }

        noteDetailed = new NoteDetailed("BasicNote", new Date());

        EditText textCreationDate = findViewById(R.id.textCreationDate);
        textCreationDate.setText(noteDetailed.getCreationDate().toString());
        editTextContent = findViewById(R.id.textContent);
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
        noteDetailed.setContent(editTextContent.getText().toString());

        // Saves everything to firebase under the user
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child(firebaseUser.getUid());

        // Saves detailed information about the note
        DatabaseReference noteDetailedReference = userReference.child(Settings.FIREBASE_NOTE_DETAILED).push();
        noteDetailedReference.setValue(noteDetailed);

        // Saves the note overview
        DatabaseReference noteOverviewReference = userReference.child(Settings.FIREBASE_NOTE_OVERVIEW).push();
        noteOverviewReference.setValue(new NoteOverview(noteDetailed, noteDetailedReference.getKey()));

        // Display that it was saved and auto-moves user to MainActivity
        Toast.makeText(view.getContext(), "Saved note: " + noteDetailed.getTitle(), Toast.LENGTH_LONG).show();
        Intent mainIntent = new Intent(view.getContext(), MainActivity.class);
        startActivity(mainIntent);
    }
}