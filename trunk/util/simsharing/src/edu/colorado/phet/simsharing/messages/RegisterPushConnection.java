// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.messages;

/**
 * @author Sam Reid
 */
public class RegisterPushConnection {
    private SessionID sessionID;

    public RegisterPushConnection( SessionID sessionID ) {
        this.sessionID = sessionID;
    }

    public SessionID getSessionID() {
        return sessionID;
    }
}
