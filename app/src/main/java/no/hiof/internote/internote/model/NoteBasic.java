package no.hiof.internote.internote.model;

import java.util.Date;

public class NoteBasic extends Note {
    public NoteBasic(){ super(); }
    public NoteBasic(Date creationDate){ super(creationDate); }
    public NoteBasic(String title, Date creationDate) {
        super(title, creationDate);
    }
}