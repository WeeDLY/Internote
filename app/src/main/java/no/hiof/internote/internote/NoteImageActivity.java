package no.hiof.internote.internote;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import pub.devrel.easypermissions.EasyPermissions;

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
    private boolean accessingCamera = false;

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PERMISSION_CAMERA_STORAGE = 2;

    private Bitmap mImageBitmap;
    private ImageView imageView_noteImage;
    private String mCurrentImagePath;

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
        Creating the options overflow toolbar menu
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_note, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*
        Android lifecycle: onStop event
    */
    @Override
    protected void onPause() {
        super.onPause();
        if(madeChanges && !deleteNote){
            if(!accessingCamera)
                saveDocument(this);
        }
    }

    /*
        Android lifecycle: saveState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
        Button click event: btnBackToMain
    */
    public void btnBackToMainOnClick(View view) {
        goToMain();
    }

    /*
        ImageView click event
     */
    public void checkCameraPermissions(View view){
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            getAnotherImage();
        }
        else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "We need to access your camera and storage",
                    PERMISSION_CAMERA_STORAGE, perms);
        }
    }

    /*
        Requesting permissions for taking and saving image
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            getAnotherImage();
        }
    }

    /*
        Capture a image for the note
    */
    public void getAnotherImage () {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the image should go
            File imageFile = null;
            try {
                imageFile = createImageFile();
            }
            catch (IOException ex) {
                // Error occurred while creating the File
                Log.d("IOEXception:", ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (imageFile != null) {
                madeChanges = true;
                accessingCamera = true;
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
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
        mCurrentImagePath = "file:" + image.getAbsolutePath();
        return image;
    }

    /*
        Replaces the current image in the image section
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentImagePath));
                imageView_noteImage.setImageBitmap(mImageBitmap);

                // Delete last image from firebase, if it existed
                if(noteDetailed.getImageUrl() != null){
                    StorageReference storageImages = FirebaseStorage.getInstance().getReference().child(noteDetailed.getImageUrl());
                    storageImages.delete();
                    noteDetailed.setImageUrl("");
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            accessingCamera = false;
        }
    }

    /*
        Handling the tap on the toolbar menu item
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            // Deletes the note
            case R.id.menuDeleteNote:
                // Note does not exist in Firebase, do nothing.
                if(currentNoteDetailedKey == null){
                    break;
                }

                deleteNote = true;
                if(user != null && currentNoteDetailedKey != null && currentNoteOverviewKey != null){
                    DatabaseReference userReference = firebaseDatabaseReference.child(user.getUid());

                    DatabaseReference noteDetailedRef = userReference.child(Settings.FIREBASE_NOTE_DETAILED).child(currentNoteDetailedKey);
                    noteDetailedRef.removeValue();

                    DatabaseReference noteOverviewRef = userReference.child(Settings.FIREBASE_NOTE_OVERVIEW).child(currentNoteOverviewKey);
                    noteOverviewRef.removeValue();

                    StorageReference storageImages = FirebaseStorage.getInstance().getReference().child(noteDetailed.getImageUrl());
                    storageImages.delete();

                    goToMain();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
        Takes user to MainActivity
     */
    private void goToMain(){
        accessingCamera = false;
        Intent intentMain = new Intent(this, MainActivity.class);
        startActivity(intentMain);
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
                Toast.makeText(getApplicationContext(), "Image failed to upload", Toast.LENGTH_LONG).show();
            }
        });
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(), "Image uploaded successfully", Toast.LENGTH_LONG).show();
            }
        });

        return imagePath;
    }

    /*
        Downloads image from Firebase storage
     */
    private void downloadImage(String url){
        if(url == null)
            return;

        StorageReference storageImage = FirebaseStorage.getInstance().getReference(url);
        storageImage.getBytes(Settings.MAX_IMAGE_DOWNLOAD_SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
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
                Log.d("retrieveDocument.cancel", databaseError.getMessage());
            }
        });
    }

    /*
        Checks if user has internet connection
    */
    private boolean hasNetworkConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /*
        Saves the current document to firebase
     */
    private void saveDocument(Context context){
        if(!hasNetworkConnection()){
            Toast.makeText(context, "Note was not saved, due to no internet connection :(", Toast.LENGTH_LONG).show();
            return;
        }

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


        // NoteOverview does not exist, writes a new NoteOverview to database
        if(currentNoteOverviewKey == null){
            DatabaseReference noteOverviewReference = userReference.child(Settings.FIREBASE_NOTE_OVERVIEW).push();
            noteOverviewReference.setValue(new NoteOverview(noteDetailed, noteDetailedReference.getKey()));

        }
        // Update NoteOverview to firebase
        else{
            DatabaseReference noteOverviewReference = userReference.child(Settings.FIREBASE_NOTE_OVERVIEW).child(currentNoteOverviewKey);
            noteOverviewReference.child("lastEdited").setValue(noteDetailed.getLastEdited());
            noteOverviewReference.child("title").setValue(noteDetailed.getTitle());
            noteOverviewReference.child("imageUrl").setValue(noteDetailed.getImageUrl());
        }
    }
}