package no.hiof.internote.internote.model;

// TODO: What is Resources.Theme?
enum Theme{
    Light,
    Dark
}

public class Settings {
    public final static String FIREBASEUSER_INTENT = "firebaseUser";
    public final static String FIREBASE_NOTE_OVERVIEW = "NoteOverview";
    public final static String FIREBASE_NOTE_DETAILED = "NoteDetailed";

    private int fontSize;
    private String fontFamily;
    private Theme theme;

    public Settings(){
        fontSize = 12;
        fontFamily = "Arial";
        theme = Theme.Light;
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

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }
}
