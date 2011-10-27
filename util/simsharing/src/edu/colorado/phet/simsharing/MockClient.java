// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.io.IOException;

import edu.colorado.phet.common.simsharingcore.IActor;
import edu.colorado.phet.simsharing.messages.SessionID;
import edu.colorado.phet.simsharing.messages.StartSession;

/**
 * @author Sam Reid
 */
public class MockClient implements IActor {
    int sessionCount = 0;

    public MockClient( String host, int port ) {
//        super( host, port );
    }

    public Object ask( Object message ) throws IOException, ClassNotFoundException {
        if ( message instanceof StartSession ) {
            StartSession request = (StartSession) message;
            final SessionID sessionID = new SessionID( sessionCount, request.studentID + "*" + sessionCount, request.simName );
            sessionCount++;
            System.out.println( "session started: " + sessionID );
            return sessionID;
        }
        else {
            return "hello";
        }
    }

    public void tell( Object statement ) throws IOException {
    }
}
