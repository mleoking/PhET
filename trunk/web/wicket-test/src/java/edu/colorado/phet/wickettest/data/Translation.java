package edu.colorado.phet.wickettest.data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class Translation implements Serializable {
    private int id;
    private Locale locale;
    private Set translatedStrings = new HashSet();
    private Set authorizedUsers = new HashSet();
    private boolean visible;

    public boolean isAuthorizedUser( PhetUser user ) {
        if ( user.isTeamMember() ) {
            return true;
        }
        for ( Object authorizedUser : authorizedUsers ) {
            if ( ( (PhetUser) authorizedUser ).getId() == user.getId() ) {
                return true;
            }
        }
        return false;
    }

    public void addString( TranslatedString str ) {
        translatedStrings.add( str );
        str.setTranslation( this );
    }

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

    public Set getAuthorizedUsers() {
        return authorizedUsers;
    }

    public void setAuthorizedUsers( Set authorizedUsers ) {
        this.authorizedUsers = authorizedUsers;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
    }
}
