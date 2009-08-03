package edu.colorado.phet.wickettest.data;

import java.io.Serializable;

public class TranslatedString implements Serializable {
    private int id;
    private Translation translation;
    private String key;
    private String value;

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
}
