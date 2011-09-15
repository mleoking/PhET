// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.server;

import edu.colorado.phet.common.phetcommon.simsharing.SimState;
import edu.colorado.phet.simsharing.messages.AddSamples;
import edu.colorado.phet.simsharing.messages.SessionID;

/**
 * @author Sam Reid
 */
public interface Storage {
    void init();

    SimState getSample( SessionID sessionID, int index );

    int getNumberSamples( SessionID sessionID );

    int getNumberSessions();

    void startSession( SessionID sessionID );

    void endSession( SessionID sessionID );

    Object getActiveStudentList();

    public void storeAll( SessionID sessionID, AddSamples data );

    Object listAllSessions();

    void clear();

    Object getSamplesAfter( SessionID id, long time );
}
