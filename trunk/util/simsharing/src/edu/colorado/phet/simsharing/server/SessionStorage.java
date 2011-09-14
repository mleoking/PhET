// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.server;

import java.util.Collection;

import edu.colorado.phet.simsharing.messages.SessionID;
import edu.colorado.phet.simsharing.socket.Session;

/**
 * @author Sam Reid
 */
public interface SessionStorage {
    Session<?> get( SessionID sessionID );

    int size();

    void put( SessionID sessionID, Session session );

    Collection<? extends SessionID> keySet();

    Collection<Session<?>> values();

    void clear();
}