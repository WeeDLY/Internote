package no.hiof.internote.internote.model;

import java.util.ArrayList;
import java.util.Date;

public abstract class Note{
    private String title;
    private Date creationDate;
    private ArrayList<String> Tags;

    public Note(String title, Date creationDate){
        this.title = title;
        this.creationDate = creationDate;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}