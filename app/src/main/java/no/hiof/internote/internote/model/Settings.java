package no.hiof.internote.internote.model;

public class Settings {
    public final static String FIREBASE_NOTE_OVERVIEW = "NoteOverview";
    public final static String FIREBASE_NOTE_DETAILED = "NoteDetailed";

    public final static String INTENT_FIREBASEUSER = "INTENT_FIREBASEUSER";
    public final static String INTENT_NOTEDETAILED_KEY = "INTENT_NOTE_DETAILED"; // NoteDetailedKey. Used to update NoteDetailed
    public final static String INTENT_NOTEOVERVIEW_KEY = "INTENT_NOTE_OVERVIEW"; // NoteOverviewKey. Used to update "lastEdited"

    private int fontSize;
    private String fontFamily;

    public Settings(){
        fontSize = 12;
        fontFamily = "Arial";
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
