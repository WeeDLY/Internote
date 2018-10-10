package no.hiof.internote.internote.model;

import java.util.Date;

/*
    Class NoteOverview
    Used to display users file in MainActivity
 */
public class NoteOverview extends Note{
    private String uid;

    public NoteOverview(NoteDetailed noteDetailed, String uid){
        super(noteDetailed);
        this.uid = uid;
    }

    public NoteOverview(String title, Date lastEdited, String imageUrl){
        super(title, lastEdited, imageUrl);
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}