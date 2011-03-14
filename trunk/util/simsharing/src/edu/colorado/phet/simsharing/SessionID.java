// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.io.Serializable;

/**
 * Unique ID for each session
 *
 * @author Sam Reid
 */
public class SessionID implements Serializable {
    private int index;
    private String name;

    public SessionID() {
    }

    public SessionID( int index, String name ) {
        this.index = index;
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " #" + index;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( o == null || getClass() != o.getClass() ) { return false; }

        SessionID sessionID = (SessionID) o;

        if ( index != sessionID.index ) { return false; }
        if ( name != null ? !name.equals( sessionID.name ) : sessionID.name != null ) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = index;
        result = 31 * result + ( name != null ? name.hashCode() : 0 );
        return result;
    }
}
