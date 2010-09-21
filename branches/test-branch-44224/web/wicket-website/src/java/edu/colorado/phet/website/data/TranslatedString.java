package edu.colorado.phet.website.data;

import java.io.Serializable;
import java.util.Date;

import edu.colorado.phet.website.data.util.IntId;

public class TranslatedString implements Serializable, IntId {
    private int id;
    private Translation translation;
    private String key;
    private String value;
    private Date createdAt;
    private Date updatedAt;

    // TODO: add translation string locking!!

    public void initializeNewString( Translation translation, String key, String value ) {
        createdAt = new Date();
        updatedAt = createdAt;
        this.key = key;
        this.value = value;

        // should set our translation value
        translation.addString( this );
    }

    @Override
    public int hashCode() {
        return ( id + 47 ) * ( id - 7 ) * ( id % 7 );
    }

    @Override
    public boolean equals( Object o ) {
        if( this == o ) {
            return true;
        }
        return ( o instanceof TranslatedString ) && id == ( (TranslatedString) o ).id;
    }

    public TranslatedString() {
    }

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public Translation getTranslation() {
        return translation;
    }

    public void setTranslation( Translation translation ) {
        this.translation = translation;
    }

    public String getKey() {
        return key;
    }

    public void setKey( String key ) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt( Date createdAt ) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt( Date updatedAt ) {
        this.updatedAt = updatedAt;
    }
}
