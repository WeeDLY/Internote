package no.hiof.internote.internote;

import java.util.Date;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import no.hiof.internote.internote.model.NoteBasic;

public class NoteBasicActivity extends AppCompatActivity {

    private NoteBasic noteBasic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_basic);

        noteBasic = new NoteBasic();
        noteBasic.setCreationDate(new Date());

        //findViewById()
    }

    public void btnMainMenu(View view) {
        Intent intent = new Intent(view.getContext(), MainActivity.class);
        startActivity(intent);
    }

    public void BtnSave(View view) {

    }
}