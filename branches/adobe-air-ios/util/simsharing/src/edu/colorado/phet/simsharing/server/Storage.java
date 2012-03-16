// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.server;

import edu.colorado.phet.simsharing.messages.AddSamples;
import edu.colorado.phet.simsharing.messages.SampleBatch;
import edu.colorado.phet.simsharing.messages.SessionID;
import edu.colorado.phet.simsharing.teacher.SessionList;
import edu.colorado.phet.simsharing.teacher.StudentList;

/**
 * @author Sam Reid
 */
public interface Storage {
    int getNumberSamples( SessionID sessionID );

    int getNumberSessions();

    StudentList getActiveStudentList();

    SessionList listAllSessions();

    void startSession( SessionID sessionID );

    void endSession( SessionID sessionID );

    SampleBatch getSamplesAfter( SessionID id, int index );

    public void storeAll( SessionID sessionID, AddSamples data );

    void clear();
}
