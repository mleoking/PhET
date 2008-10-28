package edu.colorado.phet.common.phetcommon.tracking;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.preferences.PhetPreferences;

public class SessionID {
    private long preferencesFileCreationTime;
    private long simStartedAtTime;
    private String sim;

    public SessionID( PhetApplicationConfig config ) {
        this( PhetPreferences.getInstance().getPreferencesFileCreatedAtMillis(),
              config.getSimStartTimeMillis(), config.getProjectName() );
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
