// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.io.Serializable;

/**
 * @author Sam Reid
 */
public class GetStudentData implements Serializable {

    private final SessionID sessionID;
    private int index;

    public GetStudentData( SessionID sessionID, int index ) {
        this.sessionID = sessionID;
        this.index = index;
    }

    public SessionID getSessionID() {
        return sessionID;
    }

    public int getIndex() {
        return index;
    }
}
