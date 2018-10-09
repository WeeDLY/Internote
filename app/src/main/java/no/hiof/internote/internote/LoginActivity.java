package no.hiof.internote.internote;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void BtnMain(View view) {
        //Toast.makeText(view.getContext(), "asdasd", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(view.getContext(), MainActivity.class);
        startActivity(intent);
    }
}