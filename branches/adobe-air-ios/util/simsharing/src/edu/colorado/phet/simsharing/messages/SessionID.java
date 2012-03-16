// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.messages;

import java.io.Serializable;

/**
 * Unique ID for each session
 *
 * @author Sam Reid
 */
public class SessionID implements Serializable {
    private final int index;
    private final String name;
    public final String sim;

    public SessionID( int index, String name, String sim ) {
        this.index = index;
        this.name = name;
        this.sim = sim;
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
        if ( !name.equals( sessionID.name ) ) { return false; }
        if ( !sim.equals( sessionID.sim ) ) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = index;
        result = 31 * result + name.hashCode();
        result = 31 * result + sim.hashCode();
        return result;
    }
}
