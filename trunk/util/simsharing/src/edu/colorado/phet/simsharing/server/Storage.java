// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.server;

import edu.colorado.phet.common.phetcommon.simsharing.SimState;
import edu.colorado.phet.simsharing.messages.AddSamples;
import edu.colorado.phet.simsharing.messages.SampleBatch;
import edu.colorado.phet.simsharing.messages.SessionID;
import edu.colorado.phet.simsharing.teacher.SessionList;
import edu.colorado.phet.simsharing.teacher.StudentList;

/**
 * @author Sam Reid
 */
public interface Storage {
    SimState getSample( SessionID sessionID, int index );

    int getNumberSamples( SessionID sessionID );

    int getNumberSessions();

    void startSession( SessionID sessionID );

    void endSession( SessionID sessionID );

    StudentList getActiveStudentList();

    public void storeAll( SessionID sessionID, AddSamples data );

    SessionList listAllSessions();

    void clear();

    SampleBatch getSamplesAfter( SessionID id, long time );
}
