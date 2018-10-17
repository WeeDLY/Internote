package no.hiof.internote.internote;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import no.hiof.internote.internote.model.Settings;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1;

    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        boolean internetAvailable = hasNetworkConnection();

        if(internetAvailable){
            firebaseAuth = FirebaseAuth.getInstance();
            createAuthenticationListener();
        }
    }

    // Check if user has internet connection
    private boolean hasNetworkConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    // Sends user directly to MainActivity
    public void goToMain(){
        Intent intentMain = new Intent(this, MainActivity.class);
        intentMain.putExtra(Settings.FIREBASEUSER_INTENT, firebaseUser);
        startActivity(intentMain);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (firebaseAuthStateListener != null)
            firebaseAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (firebaseAuthStateListener != null)
            firebaseAuth.removeAuthStateListener(firebaseAuthStateListener);
    }

    // Creates a authentication listener that prompts the user to log in
    private void createAuthenticationListener() {
        Log.i("tag", "CreateAuthentication listener");
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                // User not signed in, show sign in options
                if (firebaseUser == null) {
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(
                                            Arrays.asList(
                                                    new AuthUI.IdpConfig.EmailBuilder().build(),
                                                    new AuthUI.IdpConfig.GoogleBuilder().build()
                                            )
                                    )
                                    .build(),
                            RC_SIGN_IN);
                }
                // User already signed in
                else{
                    Toast.makeText(LoginActivity.this, "createAuthListener", Toast.LENGTH_SHORT).show();
                    goToMain();
                }
            }
        };
    }

    // Callback from createAuthenticationListener
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                firebaseUser = firebaseAuth.getCurrentUser();
                Toast.makeText(this, "Signed in as " + firebaseUser.getDisplayName(), Toast.LENGTH_SHORT).show();
                goToMain();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}