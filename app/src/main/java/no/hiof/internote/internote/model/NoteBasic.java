package no.hiof.internote.internote.model;

import java.util.Date;

/*
    This is a basic template for Note, that only consists of text
 */
public class NoteBasic extends Note {
    public NoteBasic(){ super(); }
    public NoteBasic(Date creationDate){ super(creationDate); }
    public NoteBasic(String title, Date creationDate) {
        super(title, creationDate);
    }
}