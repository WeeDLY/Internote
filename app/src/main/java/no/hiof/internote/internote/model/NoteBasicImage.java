package no.hiof.internote.internote.model;

import android.media.Image;

import java.util.Date;

/*
    This is a basic template for Notes with a single image.
 */
public class NoteBasicImage extends Note {
    // Image used in the note
    private Image imageFile;
    // Location where the image/note was taken
    private String location;

    public NoteBasicImage(){ super(); }

    public NoteBasicImage(Date creationDate){
        super(creationDate);
    }

    public NoteBasicImage(String title, Date creationDate) {
        super(title, creationDate);
    }

    public NoteBasicImage(String title, Date creationDate, Image imageFile) {
        super(title, creationDate);
        this.imageFile = imageFile;
    }

    public Image getImageFile() {
        return imageFile;
    }

    public void setImageFile(Image imageFile) {
        this.imageFile = imageFile;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}