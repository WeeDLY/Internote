package no.hiof.internote.internote;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import no.hiof.internote.internote.model.Settings;

public class AboutUsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(Settings.getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
    }
}