package no.hiof.internote.internote;

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

import java.util.Date;

import no.hiof.internote.internote.model.NoteDetailed;
import no.hiof.internote.internote.model.NoteOverview;
import no.hiof.internote.internote.model.Settings;

public class NoteImageActivity extends AppCompatActivity {
    private FirebaseUser user;
    private NoteDetailed noteDetailed;
    private EditText textContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_image);

        user = getIntent().getParcelableExtra(Settings.INTENT_FIREBASEUSER);
        if(user != null){
            TextView textTitle = findViewById(R.id.textTitle);
            textTitle.setText(user.getUid());
        }

        noteDetailed = new NoteDetailed("BasicNote", System.currentTimeMillis());

        EditText textCreationDate = findViewById(R.id.textCreationDate);
        textCreationDate.setText(new Date(noteDetailed.getCreationDate() * 1000).toString());
        textContent = findViewById(R.id.textContent);
    }

    /*
    Button: Takes you back to MainActivity
    */
    public void btnMainMenu(View view) {
        Intent intentMain = new Intent(view.getContext(), MainActivity.class);
        intentMain.putExtra(Settings.INTENT_FIREBASEUSER, user);
        startActivity(intentMain);
    }

    /*
    Button: Saves your current noteDetailed to firebase, also takes you back to MainActivity afterwards
    */
    public void BtnSave(View view) {
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

    public void imageViewClick(View view) {

    }
}