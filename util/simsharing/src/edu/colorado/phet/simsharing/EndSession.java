// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.io.Serializable;

/**
 * @author Sam Reid
 */
public class EndSession implements Serializable {
    private SessionID sessionID;

    public EndSession( SessionID sessionID ) {
        this.sessionID = sessionID;
    }

    public SessionID getSessionID() {
        return sessionID;
    }
}
