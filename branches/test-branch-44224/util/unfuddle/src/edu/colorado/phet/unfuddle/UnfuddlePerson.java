package edu.colorado.phet.unfuddle;

import org.w3c.dom.Node;

public class UnfuddlePerson extends XMLObject {

    public UnfuddlePerson( Node node ) {
        super( node );
    }

    public int getID() {
        return Integer.parseInt( getTextContent( "id" ) );
    }

    public String getName() {
        return getTextContent( "first-name" ) + " " + getTextContent( "last-name" );
    }

    public String getUsername() {
        return getTextContent( "username" );
    }

    public String getEmail() {
        return getTextContent( "email" );
    }
}
