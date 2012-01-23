// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.messages;

import edu.colorado.phet.common.phetcommon.util.PhetUtilities;

/**
 * Class for creating custom IUserComponents without using enum.
 *
 * @author Sam Reid
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class UserComponent implements IUserComponent {

    private final String id;

    public UserComponent( String id ) {
        this.id = id;
    }

    public UserComponent( int id ) {
        this( String.valueOf( id ) );
    }

    // Converts a Class name to an IUserComponent by getting the Class' basename and converting first char to lowercase.
    public UserComponent( Class theClass ) {
        this( toId( theClass ) );
    }

    // Converts an Object to a UserComponent by using is class name, ala UserComponentId(Class)
    public UserComponent( Object object ) {
        this( object.getClass() );
    }

    @Override public String toString() {
        return id;
    }

    private static String toId( Class theClass ) {
        String basename = PhetUtilities.getBasename( theClass );
        return Character.toLowerCase( basename.indexOf( 0 ) ) + basename.substring( 1 );
    }
}