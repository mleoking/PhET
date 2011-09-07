// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.teacher;

import java.io.Serializable;
import java.util.ArrayList;

import edu.colorado.phet.simsharing.messages.SessionRecord;

/**
 * @author Sam Reid
 */
public class SessionList implements Serializable {
    private final ArrayList<SessionRecord> list;

    public SessionList( ArrayList<SessionRecord> list ) {
        this.list = new ArrayList<SessionRecord>( list );
    }

    public int size() {
        return list.size();
    }

    public Object[] toArray() {
        return list.toArray( new SessionRecord[list.size()] );
    }
}