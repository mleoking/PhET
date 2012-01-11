// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.messages;

/**
 * ID for named user interface components to be chained with ComponentChain
 *
 * @author Sam Reid
 */
public class UserComponentId implements UserComponent {

    public final String id;

    public UserComponentId( int id ) {
        this( String.valueOf( id ) );
    }

    public UserComponentId( String id ) {
        this.id = id;
    }

    @Override public String toString() {
        return id;
    }
}