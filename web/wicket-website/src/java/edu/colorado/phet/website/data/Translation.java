package edu.colorado.phet.website.data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.data.util.IntId;

public class Translation implements Serializable, IntId {
    private int id;
    private Locale locale;
    private Set translatedStrings = new HashSet();
    private Set authorizedUsers = new HashSet();
    private boolean visible;
    private boolean locked;

    public boolean isAuthorizedUser( PhetUser user ) {
        // must be specifically authorized to change main English translation strings
        if ( user.isTeamMember() && ( !visible || !PhetWicketApplication.getDefaultLocale().equals( locale ) ) ) {
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

    public void addUser( PhetUser user ) {
        authorizedUsers.add( user );
        user.getTranslations().add( this );
    }

    public void removeUser( PhetUser user ) {
        authorizedUsers.remove( user );
        user.getTranslations().remove( this );
    }

    @Override
    public boolean equals( Object o ) {
        return o != null && o instanceof Translation && ( (Translation) o ).getId() == getId();
    }

    @Override
    public int hashCode() {
        return ( id * 475165 ) % 2567;
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

    public boolean isLocked() {
        return locked;
    }

    public void setLocked( boolean locked ) {
        this.locked = locked;
    }
}
