package no.hiof.internote.internote;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;


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
/*
        try{
            DatabaseReference myRef1 = FirebaseDatabase.getInstance().getReference(); //Getting root reference
            DatabaseReference myRef = myRef1.child("message"); //Write your child reference if any
            myRef.setValue("Hello, World!");
            /*
            firebaseDatabase = FirebaseDatabase.getInstance();
            //DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            //mDatabase.push().setValue(1);
            //DatabaseReference myRef = firebaseDatabase.getReference("users");
            usersReference = firebaseDatabase.getReference("users");
            generateTestData();
        }
        catch(Exception e){
            Log.d("EXCEPTIONDECEPTION", "onCreate: " + e.getMessage());
        }*/
        //generateTestData();

    }

}