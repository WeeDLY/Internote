package no.hiof.internote.internote.model;

import java.util.Date;

public class NoteOverview {
    private String title;
    private Date lastEdited;
    private String imageUrl;

    public NoteOverview(String title, Date lastEdited, String imageUrl){
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