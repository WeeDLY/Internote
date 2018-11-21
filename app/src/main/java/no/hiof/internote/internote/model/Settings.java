package no.hiof.internote.internote.model;

import android.content.Context;
import android.content.SharedPreferences;

import no.hiof.internote.internote.R;

import static android.content.Context.MODE_PRIVATE;

public class Settings {
    /* Constant string for intents, settings name, etc */
    public static final String FIREBASE_NOTE_OVERVIEW = "NoteOverview";
    public static final String FIREBASE_NOTE_DETAILED = "NoteDetailed";

    public static final String INTENT_NOTEDETAILED_KEY = "INTENT_NOTE_DETAILED"; // NoteDetailedKey. Used to update NoteDetailed
    public static final String INTENT_NOTEOVERVIEW_KEY = "INTENT_NOTE_OVERVIEW"; // NoteOverviewKey. Used to update "lastEdited"
    public static final String INTENT_REFRESH_RECYCLER = "INTENT_REFRESH_RECYCLER";

    private static final String USER_PREFERENCE = "USER_PREFERENCE";
    private static final String SETTINGS_THEME = "SETTINGS_THEME";
    private static final String SETTINGS_CHECK_SOUND = "SETTINGS_CHECK_SOUND";
    public static final String SETTINGS_CHECK_DESCENDING = "SETTINGS_CHECK_DESCENDING";

    /* Settings variables */
    public static final long MAX_IMAGE_DOWNLOAD_SIZE = 1024 * 1024 * 25; // 25MB is max size for image download from Firebase Storage
    public static final int MAX_TITLE_LENGTH = 12; // Max length on the title field in the note overview

    private static boolean sound = true;
    private static int appTheme = 0;
    private static boolean descending = true;

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

    public static void setAppTheme(int appTheme){
        Settings.appTheme = appTheme;
    }

    public static int getAppTheme(){
        return Settings.appTheme;
    }

    public static boolean getSound() {
        return sound;
    }

    public static void setSound(boolean sound) {
        Settings.sound = sound;
    }

    public static boolean getDescending() {
        return descending;
    }

    public static void setDescending(boolean descending) {
        Settings.descending = descending;
    }

    /*
        Loads user settings
     */
    public static void loadData(Context ctx){
        SharedPreferences prefs = ctx.getSharedPreferences(Settings.USER_PREFERENCE, MODE_PRIVATE);
        Settings.setAppTheme(prefs.getInt(Settings.SETTINGS_THEME, 0));
        Settings.setSound(prefs.getBoolean(Settings.SETTINGS_CHECK_SOUND, true));
        Settings.setDescending(prefs.getBoolean(Settings.SETTINGS_CHECK_DESCENDING, true));
    }

    /*
        Saves user settings
     */
    public static void saveData(Context ctx, int appTheme, boolean checkSound, boolean descending){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(Settings.USER_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Settings.SETTINGS_THEME, appTheme);
        editor.putBoolean(Settings.SETTINGS_CHECK_SOUND, checkSound);
        editor.putBoolean(Settings.SETTINGS_CHECK_DESCENDING, descending);
        editor.apply();
    }
}