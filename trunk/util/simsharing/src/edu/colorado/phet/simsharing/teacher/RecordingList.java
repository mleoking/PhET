// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.teacher;

import java.io.Serializable;
import java.util.ArrayList;

import edu.colorado.phet.simsharing.SessionID;

/**
 * @author Sam Reid
 */
public class RecordingList implements Serializable {
    private ArrayList<SessionID> list = new ArrayList<SessionID>();

    public void add( SessionID studentID ) {
        list.add( studentID );
    }

    public int size() {
        return list.size();
    }

    public SessionID get( int i ) {
        return list.get( i );
    }

    public Object[] toArray() {
        return list.toArray( new SessionID[0] );
    }
}
