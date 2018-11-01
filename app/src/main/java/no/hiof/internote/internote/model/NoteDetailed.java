package no.hiof.internote.internote.model;

import java.util.Date;

/*
    This class contains:
        Title
        lastEdited
        creationDate
        Bildet/ikke Bildet
        (Tags)
        innhold
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

    public NoteDetailed(String title, long creationDate){
        super(title, creationDate); // Passing creationDate as lastEdited
        this.creationDate = creationDate;
    }

    public NoteDetailed(String title, long lastEdited, String content, long creationDate){
        super(title, lastEdited);
        this.content = content;
        this.creationDate = creationDate;
    }

    public NoteDetailed(String title, long lastEdited, String imageUrl, String content, long creationDate){
        super(title, lastEdited, imageUrl);
        this.content = content;
        this.creationDate = creationDate;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
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

    @Override
    public boolean equals(Object obj) {
        // Compared to itself
        if(obj == this) return true;

        // Compare instance of class
        if (!(obj instanceof NoteDetailed)) {
            return false;
        }

        NoteDetailed o = (NoteDetailed)obj;

        if(this.getTitle() == o.getTitle() &&
                this.getContent() == o.getContent() &&
                this.getImageUrl() == o.getImageUrl()){
            return true;
        }
        else{
            return false;
        }
    }
}