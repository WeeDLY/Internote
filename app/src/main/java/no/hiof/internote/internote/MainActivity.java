package no.hiof.internote.internote;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private DatabaseReference usersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference();
        myRef.child("users").child(String.valueOf(1)).child("username").setValue("Gunnar");

        //usersReference = firebaseDatabase.getReference("username");
        //generateTestData();

    }
    private void generateTestData(){
        ArrayList<String> data = new ArrayList<String>();
        data.add("a");
        data.add("b");
        data.add("c");

        for(String s : data){
            usersReference.push().setValue(s);
        }

    }

}