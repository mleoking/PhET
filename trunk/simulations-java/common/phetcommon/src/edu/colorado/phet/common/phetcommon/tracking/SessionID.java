package edu.colorado.phet.common.phetcommon.tracking;

import edu.colorado.phet.common.phetcommon.application.ISimInfo;
import edu.colorado.phet.common.phetcommon.preferences.PhetPreferences;

/**
 * SessionID identifies a "session", which is an invocation of a simulation.
 * All messages sent during a session will contain this identifier.
 * This identifier is likely (but not assuredly) unique.
 *
 * @author Sam Reid
 */
public class SessionID {
    
    private long preferencesFileCreationTime;
    private long simStartedAtTime;
    private String sim;

    public SessionID( ISimInfo simInfo ) {
        this( PhetPreferences.getInstance().getPreferencesFileCreatedAtMillis(),
              simInfo.getSimStartTimeMillis(), simInfo.getProjectName() );
    }

    public SessionID( long preferencesFileCreationTime, long simStartedAtTime, String sim ) {
        this.preferencesFileCreationTime = preferencesFileCreationTime;
        this.simStartedAtTime = simStartedAtTime;
        this.sim = sim;
    }

    public String toString() {
        return sim + "_" + preferencesFileCreationTime + "_" + simStartedAtTime;
    }
}
