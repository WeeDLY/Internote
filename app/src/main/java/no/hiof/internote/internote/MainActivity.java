package no.hiof.internote.internote;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import no.hiof.internote.internote.adapter.NoteRecyclerAdapter;
import no.hiof.internote.internote.model.NoteOverview;
import no.hiof.internote.internote.model.Settings;

public class MainActivity extends AppCompatActivity {
    private FirebaseUser user;
    private ArrayList<NoteOverview> notes = new ArrayList<>(); // List of users notes
    private RecyclerView recyclerView;
    private NoteRecyclerAdapter noteRecyclerAdapter;

    /*
        onCreate lifecycle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = getIntent().getParcelableExtra(Settings.INTENT_FIREBASEUSER);
        // user is logged in
        if(user != null){
            Toast.makeText(this, "user: " + user.getUid(), Toast.LENGTH_LONG).show();
            retrieveUserDocuments(user);
        }
        else{
            Toast.makeText(this, "No user logged in", Toast.LENGTH_LONG).show();
        }

        setUpFloatingActionButton();
        setUpRecyclerView();
    }

    /*
        onStart lifecycle
     */
    @Override
    protected void onStart() {
        // Clear recycleview list
        noteRecyclerAdapter.notifyItemRangeRemoved(0, notes.size());
        notes.clear();
        super.onStart();
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
    public void setUpFloatingActionButton() {
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(view.getContext(), "TODO: Overflow menu", Toast.LENGTH_LONG).show();
                return true;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startNoteBasicActivity = new Intent(MainActivity.this, NoteTextActivity.class);
                startNoteBasicActivity.putExtra(Settings.INTENT_FIREBASEUSER, user);
                startActivity(startNoteBasicActivity);
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
        documentsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    NoteOverview noteOverview = snap.getValue(NoteOverview.class);
                    noteOverview.setKey(snap.getKey());
                    if(!notes.contains(noteOverview)){
                        notes.add(noteOverview);
                        noteRecyclerAdapter.notifyItemInserted(notes.size() - 1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /*
        Creating the options overflow toolbar menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*
        Handling the tap on the toolbar menu item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menuItem_overflow1:
                FirebaseAuth.getInstance().signOut();
                Intent startLoginActivity = new Intent(this, LoginActivity.class);
                startActivity(startLoginActivity);
                break;
            case R.id.menuItem_overflow2:
                Intent startAboutUsActivity = new Intent(this, AboutUsActivity.class);
                startActivity(startAboutUsActivity);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}