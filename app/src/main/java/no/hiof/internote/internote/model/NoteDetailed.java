package no.hiof.internote.internote.model;

import java.util.Date;

/*
    class: NoteDetailed
    Contains everything a Note needs
 */
public class NoteDetailed extends Note{
    private String content;
    private long creationDate;

    // Empty constructor for Firebase -> object
    public NoteDetailed(){ super(); }

    public NoteDetailed(String title, String content, long creationDate){
        super(title, creationDate);
        this.content = content;
        this.creationDate = creationDate;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Title: " + getTitle() + " creationDate: " + new Date(getCreationDate() * 1000).toString() + " content: " + getContent();
    }
}