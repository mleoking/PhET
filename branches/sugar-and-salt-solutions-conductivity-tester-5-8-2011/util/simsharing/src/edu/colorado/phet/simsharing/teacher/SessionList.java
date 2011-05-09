// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.teacher;

import java.io.Serializable;
import java.util.ArrayList;

import edu.colorado.phet.simsharing.SessionStarted;

/**
 * @author Sam Reid
 */
public class SessionList implements Serializable {
    private ArrayList<SessionStarted> list = new ArrayList<SessionStarted>();

    public void add( SessionStarted sessionStarted ) {
        list.add( sessionStarted );
    }

    public int size() {
        return list.size();
    }

    public SessionStarted get( int i ) {
        return list.get( i );
    }

    public Object[] toArray() {
        return list.toArray( new SessionStarted[0] );
    }
}
