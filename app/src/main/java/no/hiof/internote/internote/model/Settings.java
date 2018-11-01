package no.hiof.internote.internote.model;

import android.content.res.Resources;

import no.hiof.internote.internote.R;

public class Settings {
    public final static String FIREBASE_NOTE_OVERVIEW = "NoteOverview";
    public final static String FIREBASE_NOTE_DETAILED = "NoteDetailed";

    public final static String INTENT_NOTEDETAILED_KEY = "INTENT_NOTE_DETAILED"; // NoteDetailedKey. Used to update NoteDetailed
    public final static String INTENT_NOTEOVERVIEW_KEY = "INTENT_NOTE_OVERVIEW"; // NoteOverviewKey. Used to update "lastEdited"

    private int fontSize;
    private String fontFamily;
    private static int appTheme;

    public Settings(){
        fontSize = 12;
        fontFamily = "Arial";
        appTheme = 0;
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
}
