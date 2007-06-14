package edu.colorado.phet.mm;

/**
 * User: Sam Reid
 * Date: Aug 17, 2006
 * Time: 9:58:07 AM
 * Copyright (c) Aug 17, 2006 by Sam Reid
 */
public class ImageEntryBean {
    private String path;
    private boolean nonPhet;
    private String source = "";
    private String notes = "";
    private boolean done = false;

    public ImageEntryBean() {
    }

    public ImageEntryBean( ImageEntry imageEntry ) {
        this.path = imageEntry.getPath();
        this.nonPhet = imageEntry.isNonPhet();
        this.source = imageEntry.getSource();
        this.notes = imageEntry.getNotes();
        this.done = imageEntry.isDone();
    }

    public String getPath() {
        return path;
    }

    public void setPath( String path ) {
        this.path = path;
    }

    public boolean isNonPhet() {
        return nonPhet;
    }

    public void setNonPhet( boolean nonPhet ) {
        this.nonPhet = nonPhet;
    }

    public String getSource() {
        return source;
    }

    public String getNotes() {
        return notes;
    }

    public void setSource( String source ) {
        this.source = source;
    }

    public void setNotes( String notes ) {
        this.notes = notes;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone( boolean done ) {
        this.done = done;
    }
}
