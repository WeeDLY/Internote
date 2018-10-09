package no.hiof.internote.internote;

import java.util.Date;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import no.hiof.internote.internote.model.NoteBasic;

public class NoteBasicActivity extends AppCompatActivity {
    private NoteBasic noteBasic;
    private EditText editTextContent;

    // TODO: "Temporary user"
    private final String TEST_USER = "Test User";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_basic);

        noteBasic = new NoteBasic("BasicNote", new Date());

        EditText textCreationDate = findViewById(R.id.textCreationDate);
        textCreationDate.setText(noteBasic.getCreationDate().toString());
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
        Button: Saves your current note to firebase, also takes you back to MainActivity afterwards
     */
    public void BtnSave(View view) {
        noteBasic.setContent(editTextContent.getText().toString());

        // Saves everything to firebase under the user
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child(TEST_USER).push();
        userReference.setValue(noteBasic);

        Toast.makeText(view.getContext(), "Saved note: " + noteBasic.getTitle(), Toast.LENGTH_LONG).show();
        Intent mainIntent = new Intent(view.getContext(), MainActivity.class);
        startActivity(mainIntent);
    }
}