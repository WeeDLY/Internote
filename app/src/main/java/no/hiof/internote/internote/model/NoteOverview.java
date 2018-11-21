package no.hiof.internote.internote.model;

import com.google.firebase.database.Exclude;

/*
    Class NoteOverview
    Used to display users file in MainActivity
 */
public class NoteOverview extends Note implements Comparable<NoteOverview>{
    private String uid;

    @Exclude
    private String key;


    // Empty constructor for Firebase -> object
    public NoteOverview(){ super(); }

    public NoteOverview(NoteDetailed noteDetailed, String uid){
        super(noteDetailed);
        this.uid = uid;
    }

    public NoteOverview(String title, long lastEdited, String imageUrl){
        super(title, lastEdited, imageUrl);
    }

    public String getUid() {
        return uid;
    }

    /*
        Shortens the title.
     */
    public void setTitleShort(String title){
        int difference = title.length() - Settings.MAX_TITLE_LENGTH;
        if(difference > 0){
            String newTitle = getTitle().substring(0, Settings.MAX_TITLE_LENGTH - 3);
            newTitle += "...";
            setTitle(newTitle);
        }
    }

    @Override
    public String toString() {
        return "uid: " + getUid() + super.toString();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public int compareTo(NoteOverview o) {
        if (o.getLastEdited() == this.getLastEdited())
            return 0;
        return o.getLastEdited() > this.getLastEdited() ? 1 : -1;
    }
}