// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.socket;

import java.io.IOException;

import edu.colorado.phet.simsharing.messages.GetStudentData;
import edu.colorado.phet.simsharing.messages.SessionID;
import edu.colorado.phet.simsharing.socketutil.IActor;

/**
 * @author Sam Reid
 */
public class RemoteActor<T> {
    private final SessionID sessionID;
    private IActor server;

    public RemoteActor( IActor server, SessionID sessionID ) {
        this.server = server;
        this.sessionID = sessionID;
    }

    public T getSample( int index ) throws IOException, ClassNotFoundException {
        return (T) server.ask( new GetStudentData( sessionID, index ) );
    }
}
