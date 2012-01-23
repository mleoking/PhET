// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.messages;

import edu.colorado.phet.common.phetcommon.util.PhetUtilities;

/**
 * ID for named user interface components to be chained with ComponentChain
 *
 * @author Sam Reid
 */
public class UserComponentId implements IUserComponent {

    private final String id;

    public UserComponentId( String id ) {
        this.id = id;
    }

    public UserComponentId( int id ) {
        this( String.valueOf( id ) );
    }

    // Converts a Class name to an IUserComponent by getting the Class' basename and converting first char to lowercase.
    public UserComponentId( Class theClass ) {
        this( toId( theClass ) );
    }

    // Converts an Object to a UserComponent by using is class name, ala UserComponentId(Class)
    public UserComponentId( Object object ) {
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