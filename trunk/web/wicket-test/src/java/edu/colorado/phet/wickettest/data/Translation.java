package edu.colorado.phet.wickettest.data;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.io.Serializable;

public class Translation implements Serializable {
    private int id;
    private Locale locale;
    private Set translatedStrings = new HashSet();

    public Translation() {
    }

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale( Locale locale ) {
        this.locale = locale;
    }

    public Set getTranslatedStrings() {
        return translatedStrings;
    }

    public void setTranslatedStrings( Set translatedStrings ) {
        this.translatedStrings = translatedStrings;
    }
}
