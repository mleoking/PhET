// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.server;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import edu.colorado.phet.simsharing.messages.SessionID;
import edu.colorado.phet.simsharing.socket.Session;

/**
 * @author Sam Reid
 */
public class MemoryStorage implements SessionStorage {

    //Careful, used in many threads, so must threadlock
    private Map<SessionID, Session<?>> sessions = Collections.synchronizedMap( new HashMap<SessionID, Session<?>>() );

    public Session<?> get( SessionID sessionID ) {
        return sessions.get( sessionID );
    }

    public int size() {
        return sessions.size();
    }

    public void put( SessionID sessionID, Session session ) {
        sessions.put( sessionID, session );
    }

    public Collection<? extends SessionID> keySet() {
        return sessions.keySet();
    }

    public Collection<Session<?>> values() {
        return sessions.values();
    }

    public void clear() {
        sessions.clear();
    }
}