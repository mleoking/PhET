// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.code.morphia.annotations.Indexed;

/**
 * @author Sam Reid
 */
public class SessionStarted implements Serializable {
    @Indexed private SessionID sessionID;
    private long time;

    public SessionStarted( SessionID sessionID, long time ) {
        this.sessionID = sessionID;
        this.time = time;
    }

    public SessionStarted() {
    }

    public long getTime() {
        return time;
    }

    @Override public String toString() {
        return sessionID + " at " + new SimpleDateFormat( "EEE, d MMM yyyy HH:mm:ss Z" ).format( new Date( time ) );
    }

    public SessionID getSessionID() {
        return sessionID;
    }
}
