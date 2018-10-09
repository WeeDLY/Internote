package no.hiof.internote.internote;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private DatabaseReference usersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //DatabaseReference myRef1 = FirebaseDatabase.getInstance().getReference(); //Getting root reference
        //DatabaseReference myRef = myRef1.child("message"); //Write your child reference if any
        //myRef.setValue("Hello, World!");
    }

    // Creating the options overflow toolbar_menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Handling the click on the toolbar_menu item
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menuItem_overflow1:
                Intent startLoginActivity = new Intent(this, LoginActivity.class);
                startActivity(startLoginActivity);
                break;
            case R.id.menuItem_overflow2:
                Intent startNoteBasicActivity = new Intent(this, NoteBasicActivity.class);
                startActivity(startNoteBasicActivity);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}