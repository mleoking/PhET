package edu.colorado.phet.website.data;

import java.io.Serializable;

import edu.colorado.phet.website.data.util.IntId;

// TODO: make sure keywords can never exist with the same key. Thought it was implemented, but maybe it is broken?

public class Keyword implements Serializable, IntId {

    private int id;

    /**
     * Localization key
     */
    private String key;

    public String getSubKey() {
        if ( key.startsWith( "keyword." ) ) {
            return key.substring( "keyword.".length() );
        }
        else {
            return key;
        }
    }

    public String getLocalizationKey() {
        if ( key.startsWith( "keyword." ) ) {
            return key;
        }
        else {
            return "keyword." + key;
        }
    }

    public Keyword() {
    }

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey( String key ) {
        this.key = key;
    }
}
