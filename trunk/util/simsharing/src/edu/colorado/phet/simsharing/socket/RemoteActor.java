// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.socket;

import java.io.IOException;

import edu.colorado.phet.common.phetcommon.util.Pair;
import edu.colorado.phet.simsharing.messages.GetStudentData;
import edu.colorado.phet.simsharing.messages.Sample;
import edu.colorado.phet.simsharing.messages.SessionID;
import edu.colorado.phet.simsharing.socketutil.IActor;

/**
 * @author Sam Reid
 */
public class RemoteActor implements SampleSource {
    private final SessionID sessionID;
    private IActor server;

    public RemoteActor( IActor server, SessionID sessionID ) {
        this.server = server;
        this.sessionID = sessionID;
    }

    public Pair<Sample, Integer> getSample( int index ) throws IOException, ClassNotFoundException {
        return (Pair<Sample, Integer>) server.ask( new GetStudentData( sessionID, index ) );
    }
}
