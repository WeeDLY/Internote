package no.hiof.internote.internote;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import no.hiof.internote.internote.model.Settings;

public class SettingsActivity extends AppCompatActivity {
    private List<String> colorThemeList = new ArrayList<>();
    private Spinner spinnerColorTheme;

    private CheckBox checkSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(Settings.getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setUpSpinner();
        setUpCheckSound();
    }

    private void setUpSpinner(){
        spinnerColorTheme = findViewById(R.id.SpinnerColorTheme);
        colorThemeList.add("LightTheme");
        colorThemeList.add("DarkTheme");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, colorThemeList);
        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerColorTheme.setAdapter(spinnerAdapter);
        spinnerColorTheme.setSelection(Settings.getAppTheme());
        spinnerColorTheme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(Settings.getAppTheme() != position){
                    Settings.setAppTheme(position);
                    SettingsActivity.this.recreate();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setUpCheckSound(){
        checkSound = findViewById(R.id.checkBoxSound);
        checkSound.setChecked(Settings.getSound());
        checkSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Settings.setSound(isChecked);
            }
        });
    }

    public void applyChanges(View view){
        saveData();

        Intent intentMain = new Intent(this, MainActivity.class);
        startActivity(intentMain);
    }

    /*
        Saves user settings
     */
    public void saveData(){
        int appTheme = spinnerColorTheme.getSelectedItemPosition();
        boolean sound = checkSound.isChecked();
        Settings.saveData(this, appTheme, sound);
    }
}