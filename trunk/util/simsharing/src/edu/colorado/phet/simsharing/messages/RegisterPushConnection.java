// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.messages;

import java.io.Serializable;

/**
 * Message to indicate a teacher sim view wants to receive push notifications for the specified session
 *
 * @author Sam Reid
 */
public class RegisterPushConnection implements Serializable {
    private SessionID sessionID;

    public RegisterPushConnection( SessionID sessionID ) {
        this.sessionID = sessionID;
    }

    public SessionID getSessionID() {
        return sessionID;
    }
}
