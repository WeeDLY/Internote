package no.hiof.internote.internote.model;


import java.util.Date;

/*
    abstract class Note
 */
public abstract class Note {
    private String title;
    private Date lastEdited;
    private String imageUrl;

    public Note(NoteDetailed noteDetailed){
        this.title = noteDetailed.getTitle();
        this.lastEdited = noteDetailed.getLastEdited();
        this.imageUrl = noteDetailed.getImageUrl();
    }

    public Note(String title, Date lastEdited){
        this.title = title;
        this.lastEdited = lastEdited;
    }

    public Note(String title, Date lastEdited, String imageUrl){
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

    public Date getLastEdited() {
        return lastEdited;
    }

    public void setLastEdited(Date lastEdited) {
        this.lastEdited = lastEdited;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    //TODO: Tags?


}