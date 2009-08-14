package edu.colorado.phet.wickettest.translation;

import java.io.Serializable;

public class TranslationEntityString implements Serializable {
    private String key;
    private String notes = null;

    public TranslationEntityString( String key ) {
        this.key = key;
    }

    public TranslationEntityString( String key, String notes ) {
        this.key = key;
        this.notes = notes;
    }

    public String getKey() {
        return key;
    }

    public void setKey( String key ) {
        this.key = key;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes( String notes ) {
        this.notes = notes;
    }
}
