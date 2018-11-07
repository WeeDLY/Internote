package no.hiof.internote.internote;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import no.hiof.internote.internote.model.*;

public class NoteImageActivity extends AppCompatActivity {
    private FirebaseUser user;
    private NoteDetailed noteDetailed;
    private EditText textTitle;
    private EditText textLastEdited;
    private EditText textContent;

    private DatabaseReference firebaseDatabaseReference;

    private DatabaseReference noteDetailedReference;

    private String currentNoteDetailedKey;
    private String currentNoteOverviewKey;

    private boolean deleteNote = false;
    private boolean madeChanges = false;

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    private final long ONE_MEGABYTE = 1024 * 1024;

    private Bitmap mImageBitmap;
    private ImageView imageView_noteImage;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(Settings.getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_image);

        textTitle = findViewById(R.id.textTitle);
        textLastEdited = findViewById(R.id.textLastEdited);
        textContent = findViewById(R.id.textContent);
        imageView_noteImage = findViewById(R.id.imageView_image);

        firebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        currentNoteDetailedKey = getIntent().getStringExtra(Settings.INTENT_NOTEDETAILED_KEY);
        currentNoteOverviewKey = getIntent().getStringExtra(Settings.INTENT_NOTEOVERVIEW_KEY);

        if (currentNoteDetailedKey != null){
            retrieveDocument(currentNoteDetailedKey);
        }
        else{
            noteDetailed = new NoteDetailed("New note", "", System.currentTimeMillis());
            fillFields();
        }

    }

    /*
        Capture a picture for the note
    */
    public void getAnotherPicture (View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.i("IOEXception:", ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                madeChanges = true;
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
    /*
        Create image from the camera
    */
    private File createImageFile() throws IOException {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    // Replaces the current picture in the image section
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
                imageView_noteImage.setImageBitmap(mImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Saving the picture
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /*
        Button click event: btnBackToMain
     */
    public void btnBackToMainOnClick(View view) {
        goToMain();
    }

    /*
        TextChanged event. Used for listening for changes in the document
     */
    private class TextChangedListener implements TextWatcher{
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            madeChanges = true;
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    /*
        Creating the options overflow toolbar menu
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_note, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*
        Handling the tap on the toolbar menu item
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            // Deletes the note
            case R.id.menuDeleteNote:
                deleteNote = true;
                if(user != null && currentNoteDetailedKey != null && currentNoteOverviewKey != null){
                    DatabaseReference userReference = firebaseDatabaseReference.child(user.getUid());

                    DatabaseReference noteDetailedRef = userReference.child(Settings.FIREBASE_NOTE_DETAILED).child(currentNoteDetailedKey);
                    noteDetailedRef.removeValue();

                    DatabaseReference noteOverviewRef = userReference.child(Settings.FIREBASE_NOTE_OVERVIEW).child(currentNoteOverviewKey);
                    noteOverviewRef.removeValue();
                    goToMain();
                }
                break;
            // Goes back to MainActivity
            case R.id.menuBackToMain:
                goToMain();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
        Takes user to MainActivity
     */
    private void goToMain(){
        Intent intentMain = new Intent(this, MainActivity.class);
        startActivity(intentMain);
    }

    /*
        Android lifecycle: onPause event
     */
    @Override
    protected void onPause() {
        super.onPause();
        if(madeChanges && !deleteNote){
            Log.d("SaveDocument", "SAVED");
            saveDocument(this);
        }
    }

    /*
        Fills all the text fields in the layout
     */
    private void fillFields(){
        textContent.setText(noteDetailed.getContent());

        textLastEdited.setText(new SimpleDateFormat("HH:mm dd-MM-yyyy").format(new Date(noteDetailed.getLastEdited())));

        TextView textTitle = findViewById(R.id.textTitle);
        textTitle.setText(noteDetailed.getTitle());


        // Set events, so we can check if the user made changes to the document
        this.textTitle.addTextChangedListener(new TextChangedListener());
        textContent.addTextChangedListener(new TextChangedListener());

        // Download Image
        downloadImage(noteDetailed.getImageUrl());
    }

    /*
        Downloads image from Firebase
     */
    private void downloadImage(String url){
        if(url == null)
            return;

        StorageReference storageImage = FirebaseStorage.getInstance().getReference(url);
        storageImage.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView_noteImage.setImageBitmap(bitmap);
            }
        });
    }

    /*
        Retrieves document information from firebase
     */
    private void retrieveDocument(String documentId){
        firebaseDatabaseReference.child(user.getUid()).child(Settings.FIREBASE_NOTE_DETAILED).child(documentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noteDetailed = dataSnapshot.getValue(NoteDetailed.class);
                if(noteDetailed == null)
                    noteDetailed = new NoteDetailed("New note", "", System.currentTimeMillis());
                fillFields();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("retrieveDocument.cancel", "something went funky wunky");
            }
        });
    }

    /*
        Saves the current document and moves user to MainActivity
     */
    private void saveDocument(Context context){
        // Upload image(if image was taken)
        String imagePath = uploadImage();
        if(imagePath != null){
            noteDetailed.setImageUrl(imagePath);
        }

        // Update current noteDetailed
        noteDetailed.setTitle(textTitle.getText().toString());
        noteDetailed.setContent(textContent.getText().toString());
        noteDetailed.setLastEdited(System.currentTimeMillis());

        DatabaseReference userReference = firebaseDatabaseReference.child(user.getUid());
        // Update NoteDetailed to firebase

        // Note does not exist, writes a new NoteDetailed to database
        if(currentNoteDetailedKey == null){
            noteDetailedReference = userReference.child(Settings.FIREBASE_NOTE_DETAILED).push();
        }
        // Note was loaded from a previous note. Overwrite with new data
        else{
            noteDetailedReference = userReference.child(Settings.FIREBASE_NOTE_DETAILED).child(currentNoteDetailedKey);
        }
        noteDetailedReference.setValue(noteDetailed);


        // NoteOver does not exist, writes a new NoteOverview to database
        if(currentNoteOverviewKey == null){
            DatabaseReference noteOverviewReference = userReference.child(Settings.FIREBASE_NOTE_OVERVIEW).push();
            noteOverviewReference.setValue(new NoteOverview(noteDetailed, noteDetailedReference.getKey()));

        }
        // Update NoteOverview to firebase
        else{
            DatabaseReference noteOverviewReference = userReference.child(Settings.FIREBASE_NOTE_OVERVIEW).child(currentNoteOverviewKey);
            noteOverviewReference.child("lastEdited").setValue(noteDetailed.getLastEdited());
            noteOverviewReference.child("title").setValue(noteDetailed.getTitle());
        }

        // Display that it was saved and auto-moves user to MainActivity
        Toast.makeText(context, noteDetailed.getTitle() + " saved", Toast.LENGTH_LONG).show();
    }

    /*
        Upload image to Firebase Storage
     */
    private String uploadImage(){
        if(mImageBitmap == null){
            return null;
        }

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = "JPEG_" + timeStamp + ".jpg";
        String imagePath = user.getUid() + "/" + imageName;

        StorageReference storageImages = FirebaseStorage.getInstance().getReference().child(imagePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        UploadTask uploadTask = storageImages.putBytes(imageData);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed image upload" + noteDetailedReference.getKey(), Toast.LENGTH_LONG).show();
            }
        });
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(), "Success image upload" + noteDetailedReference.getKey(), Toast.LENGTH_LONG).show();
            }
        });
        return imagePath;
    }

    /*
        GoToMain Menu
     */
    public void btnBackOnClick(View view) {
        goToMain();
    }
}