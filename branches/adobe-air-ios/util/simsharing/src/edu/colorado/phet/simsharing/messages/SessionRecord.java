// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.messages;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author Sam Reid
 */
public class SessionRecord implements Serializable {
    private SessionID sessionID;
    private long time;

    public SessionRecord( SessionID sessionID, long time ) {
        this.sessionID = sessionID;
        this.time = time;
    }

    @Override public String toString() {
        return sessionID + " at " + new SimpleDateFormat( "EEE, d MMM yyyy HH:mm:ss Z" ).format( new Date( time ) );
    }

    public SessionID getSessionID() {
        return sessionID;
    }

    public long getTime() {
        return time;
    }
}