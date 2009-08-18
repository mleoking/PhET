package edu.colorado.phet.wickettest.data;

import java.io.Serializable;

// TODO: make sure keywords can never exist with the same key. Thought it was implemented, but maybe it is broken?
public class Keyword implements Serializable {

    private int id;

    /**
     * Localization key
     */
    private String key;

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
