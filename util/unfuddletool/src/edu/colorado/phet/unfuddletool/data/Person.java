package edu.colorado.phet.unfuddletool.data;

import org.w3c.dom.Element;

import edu.colorado.phet.unfuddletool.Communication;
import edu.colorado.phet.unfuddletool.PersonHandler;

public class Person {

    // access-key
    // account-id
    public DateTime rawCreatedAt;
    public String rawEmail;
    public String rawFirstName;
    public int rawId;
    //public boolean rawIsAdministrator;
    public String rawLastName;
    public DateTime rawLastSignedIn;
    // notification-frequency
    // notification-ignore-self
    // notification-last-sent
    // notification-scope-***
    // time-zone
    public DateTime rawUpdatedAt;
    public String rawUsername;

    public Person( Element element ) {
        rawCreatedAt = Communication.getDateTimeField( element, "created-at" );
        rawEmail = Communication.getStringField( element, "email" );
        rawFirstName = Communication.getStringField( element, "first-name" );
        rawId = Communication.getIntField( element, "id" );
        rawLastName = Communication.getStringField( element, "last-name" );
        rawLastSignedIn = Communication.getDateTimeField( element, "last-signed-in" );
        rawUpdatedAt = Communication.getDateTimeField( element, "updated-at" );
        rawUsername = Communication.getStringField( element, "username" );
    }

    public int getId() {
        return rawId;
    }

    public String getFirstName() {
        return rawFirstName;
    }

    public String getLastName() {
        return rawLastName;
    }

    public String getUsername() {
        return rawUsername;
    }

    public String getName() {
        if ( getFirstName() == null && getLastName() == null ) {
            return getUsername();
        }
        return getFirstName() + " " + getLastName();
    }

    public String getFullName() {
        return getUsername() + "(" + getName() + ")";
    }

    public boolean equal( Person otherPerson ) {
        return getId() == otherPerson.getId();
    }

    public static String getNameFromId( int id ) {
        if ( id == -1 ) {
            return null;
        }

        Person person = PersonHandler.getPersonHandler().getPersonById( id );
        if ( person == null ) {
            return null;
        }

        return person.getName();
    }
}
