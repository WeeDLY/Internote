package no.hiof.internote.internote.model;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import no.hiof.internote.internote.R;

import static android.content.Context.MODE_PRIVATE;


public class Settings {
    public final static String FIREBASE_NOTE_OVERVIEW = "NoteOverview";
    public final static String FIREBASE_NOTE_DETAILED = "NoteDetailed";

    public final static String INTENT_NOTEDETAILED_KEY = "INTENT_NOTE_DETAILED"; // NoteDetailedKey. Used to update NoteDetailed
    public final static String INTENT_NOTEOVERVIEW_KEY = "INTENT_NOTE_OVERVIEW"; // NoteOverviewKey. Used to update "lastEdited"

    public static final String USER_PREFERENCE = "USER_PREFERENCE";
    public static final String SETTINGS_THEME = "SETTINGS_THEME";

    private int fontSize;
    private String fontFamily;
    private static int appTheme = 0;

    public Settings(){
        fontSize = 12;
        fontFamily = "Arial";
    }

    public static int getTheme (boolean actionBar){
        if(actionBar){
            switch(appTheme){
                default:
                case 0:
                    return R.style.LightThemeNoActionBar;
                case 1:
                    return R.style.DarkThemeNoActionBar;
            }
        }
        else{
            return getTheme();
        }
    }

    public static int getTheme (){
        switch(appTheme){
            default:
            case 0:
                return R.style.LightTheme;
            case 1:
                return R.style.DarkTheme;
        }
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public static void setAppTheme(int appTheme){
        Settings.appTheme = appTheme;
    }

    public static int getAppTheme(){
        return Settings.appTheme;
    }

    /*
        Loads user settings
     */
    public static void loadData(Context ctx){
        SharedPreferences prefs = ctx.getSharedPreferences(Settings.USER_PREFERENCE, MODE_PRIVATE);
        Settings.setAppTheme(prefs.getInt(Settings.SETTINGS_THEME, 0));
    }

    /*
        Saves user settings
     */
    public static void saveData(Context ctx, int appTheme){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(Settings.USER_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Settings.SETTINGS_THEME, appTheme);
        editor.apply();
    }
}
