package no.hiof.internote.internote;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

import no.hiof.internote.internote.adapter.NoteRecyclerAdapter;
import no.hiof.internote.internote.fragment.NavigationDrawerFragment;
import no.hiof.internote.internote.model.Audio;
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

    // For floating action button
    private FloatingActionMenu floatingActionMenu;
    private FloatingActionButton newImagenote, newTextnote;

    private boolean descending = true;

    /*
        onCreate lifecycle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(Settings.getTheme(true));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        TextView toolbarTextUser = findViewById(R.id.toolbarTextUser);

        user = FirebaseAuth.getInstance().getCurrentUser();
        toolbarTextUser.setText((user != null) ? user.getEmail() : "(Offline)");

        setUpFloatingActionButton();
        setUpRecyclerView();
        setUpNavigationDrawer();
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean refreshRecyclerView = getIntent().getBooleanExtra(Settings.INTENT_REFRESH_RECYCLER, false);
        if(refreshRecyclerView){
            int size = notes.size();
            Collections.sort(notes);
            notesKey.clear();
            for(NoteOverview note : notes){
                notesKey.add(note.getKey());
            }
            noteRecyclerAdapter.notifyItemChanged(0, size);
        }
    }

    /*
            Sets up Navigation Drawer
         */
    private void setUpNavigationDrawer() {
        navigationDrawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.fragmentNavigationDrawer);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        navigationDrawerFragment.setUpDrawer(drawerLayout, toolbar);

        retrieveUserDocuments(user);
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
                Intent intent;
                if(note.getImageUrl() != null){
                    intent = new Intent(MainActivity.this, NoteImageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                }
                else{
                    intent = new Intent(MainActivity.this, NoteTextActivity.class);
                }
                intent.putExtra(Settings.INTENT_NOTEDETAILED_KEY, note.getUid());
                intent.putExtra(Settings.INTENT_NOTEOVERVIEW_KEY, note.getKey());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(noteRecyclerAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
    }

    /*
        Sets up the floating action button
     */
    public void setUpFloatingActionButton() {
        floatingActionMenu = findViewById(R.id.fab);
        newImagenote = findViewById(R.id.fab_newImagenote);
        newTextnote = findViewById(R.id.fab_newTextnote);

        newImagenote.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intentNoteImage = new Intent(MainActivity.this, NoteImageActivity.class);
                intentNoteImage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentNoteImage);
            }
        });

        newTextnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentNoteText = new Intent(MainActivity.this, NoteTextActivity.class);
                startActivity(intentNoteText);
            }
        });
    }

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

                notes.add(0, noteOverview);

                Collections.sort(notes);
                int position = notes.indexOf(noteOverview);
                notesKey.add(position, noteOverview.getKey());

                noteRecyclerAdapter.notifyItemInserted(position);
                Audio.playSound(getApplicationContext(), "Note.mp3");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                NoteOverview noteOverview = dataSnapshot.getValue(NoteOverview.class);
                noteOverview.setKey(dataSnapshot.getKey());

                int position = notesKey.indexOf(noteOverview.getKey());
                notes.remove(position);
                notesKey.remove(position);

                int newPosition = Settings.getDescending() ? 0 : notes.size();
                notes.add(newPosition, noteOverview);
                notesKey.add(newPosition, noteOverview.getKey());

                if (position == newPosition){
                    noteRecyclerAdapter.notifyItemChanged(position);
                }
                else{
                    noteRecyclerAdapter.notifyItemRemoved(position);
                    noteRecyclerAdapter.notifyItemInserted(newPosition);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
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