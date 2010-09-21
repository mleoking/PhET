package edu.colorado.phet.website.translation;

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

    /*---------------------------------------------------------------------------*
    * override equals to be key equality
    *----------------------------------------------------------------------------*/

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals( Object o ) {
        if ( o instanceof TranslationEntityString ) {
            return ( (TranslationEntityString) o ).getKey().equals( key );
        }
        else {
            return false;
        }
    }
}
