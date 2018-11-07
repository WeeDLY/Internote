package no.hiof.internote.internote.model;


import java.util.Date;

/*
    abstract class Note
 */
public abstract class Note {
    private String title;
    private long lastEdited;
    private String imageUrl;

    public Note(){}

    public Note(NoteDetailed noteDetailed){
        this.title = noteDetailed.getTitle();
        this.lastEdited = noteDetailed.getLastEdited();
        this.imageUrl = noteDetailed.getImageUrl();
    }

    public Note(String title, long lastEdited){
        this.title = title;
        this.lastEdited = lastEdited;
    }

    public Note(String title, long lastEdited, String imageUrl){
        this.title = title;
        this.lastEdited = lastEdited;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getLastEdited() {
        return lastEdited;
    }

    public void setLastEdited(long lastEdited) {
        this.lastEdited = lastEdited;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "Title: " + getTitle() + " lastEdited" + new Date(getLastEdited() * 1000) + " imageUrl: " + getImageUrl();
    }
}