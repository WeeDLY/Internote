package no.hiof.internote.internote;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import no.hiof.internote.internote.adapter.NoteRecyclerAdapter;
import no.hiof.internote.internote.fragment.NavigationDrawerFragment;
import no.hiof.internote.internote.model.NoteOverview;
import no.hiof.internote.internote.model.Settings;

public class MainActivity extends AppCompatActivity {
    private FirebaseUser user;
    private ArrayList<NoteOverview> notes = new ArrayList<>(); // List of users notes
    private ArrayList<String> notesKey = new ArrayList<>(); // List of note key

    private RecyclerView recyclerView;
    private NoteRecyclerAdapter noteRecyclerAdapter;

    // For navigation Drawer
    private NavigationDrawerFragment navigationDrawerFragment;
    private Toolbar toolbar;

    /*
        onCreate lifecycle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);


        user = getIntent().getParcelableExtra(Settings.INTENT_FIREBASEUSER);
        // user is logged in
        if(user != null){
            Toast.makeText(this, "user: " + user.getUid(), Toast.LENGTH_LONG).show();
            retrieveUserDocuments(user);
        }
        else{
            Toast.makeText(this, "No user logged in", Toast.LENGTH_LONG).show();
        }

        //setUpFloatingActionButton();
        setUpRecyclerView();

        setUpNavigationDrawer();
    }

    /*
        onStart lifecycle
     */
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void setUpNavigationDrawer() {
        navigationDrawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.fragmentNavigationDrawer);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        navigationDrawerFragment.setUpDrawer(drawerLayout, toolbar);
    }

    /*
            Sets up RecyclerView
         */
    private void setUpRecyclerView(){
        recyclerView = findViewById(R.id.recyclerView);
        noteRecyclerAdapter = new NoteRecyclerAdapter(this, notes);

        noteRecyclerAdapter.setOnItemClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int position = recyclerView.getChildAdapterPosition(view);

                NoteOverview note = notes.get(position);

                Intent intent = new Intent(MainActivity.this, NoteTextActivity.class);
                intent.putExtra(Settings.INTENT_FIREBASEUSER, user);
                intent.putExtra(Settings.INTENT_NOTEDETAILED_KEY, note.getUid());
                intent.putExtra(Settings.INTENT_NOTEOVERVIEW_KEY, note.getKey());
                Log.d("recyclerView", note.getUid());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(noteRecyclerAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
    }

    /*
        Sets up the floating action button
     */
    /*public void setUpFloatingActionButton() {
        FloatingActionButton fab_textnote = findViewById(R.id.fab_newTextnote);
        FloatingActionButton fab_imagenote = findViewById(R.id.fab_newImagenote);

        fab_imagenote.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "TODO: Overflow menu", Toast.LENGTH_LONG).show();
            }
        });

        fab_textnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startNoteBasicActivity = new Intent(MainActivity.this, NoteTextActivity.class);
                startNoteBasicActivity.putExtra(Settings.INTENT_FIREBASEUSER, user);
                startActivity(startNoteBasicActivity);
            }
        });
    }*/

    /*
        Retrieves documents that the user has
     */
    private void retrieveUserDocuments(FirebaseUser user){
        FirebaseDatabase databaseReference = FirebaseDatabase.getInstance();
        DatabaseReference documentsReference = databaseReference.getReference();
        documentsReference = documentsReference.child(user.getUid()).child(Settings.FIREBASE_NOTE_OVERVIEW);
        documentsReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                NoteOverview noteOverview = dataSnapshot.getValue(NoteOverview.class);
                noteOverview.setKey(dataSnapshot.getKey());
                if (!notes.contains(noteOverview)) {
                    notes.add(noteOverview);
                    notesKey.add(noteOverview.getKey());
                    noteRecyclerAdapter.notifyItemInserted(notes.size() - 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                NoteOverview noteOverview = dataSnapshot.getValue(NoteOverview.class);
                noteOverview.setKey(dataSnapshot.getKey());

                int position = notesKey.indexOf(noteOverview.getKey());
                notes.set(position, noteOverview);
                noteRecyclerAdapter.notifyItemChanged(position);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // TODO: Untested
                NoteOverview noteOverview = dataSnapshot.getValue(NoteOverview.class);
                noteOverview.setKey(dataSnapshot.getKey());

                int position = notesKey.indexOf(noteOverview.getKey());
                notes.remove(position);
                notesKey.remove(position);
                noteRecyclerAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("child.databaseError", databaseError.getMessage());
            }
        });
    }
}