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
    private Date creationDate;

    public NoteDetailed(String title, Date creationDate){
        super(title, creationDate); // Passing creationDate as lastEdited
        this.creationDate = creationDate;
    }

    public NoteDetailed(String title, Date lastEdited, String imageUrl, String content, Date creationDate){
        super(title, lastEdited, imageUrl);
        this.content = content;
        this.creationDate = creationDate;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }


    @Override
    public String toString() {
        return "Title: " + getTitle() + " creationDate: " + getCreationDate().toString() + " content: " + getContent();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}