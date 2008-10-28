package edu.colorado.phet.common.phetcommon.tracking;

public class SessionID {
    private long preferencesFileCreationTime;
    private long simStartedAtTime;
    private String sim;

    public SessionID( long preferencesFileCreationTime, long simStartedAtTime, String sim ) {
        this.preferencesFileCreationTime = preferencesFileCreationTime;
        this.simStartedAtTime = simStartedAtTime;
        this.sim = sim;
    }

    public String toString() {
        return sim + "_" + preferencesFileCreationTime + "_" + simStartedAtTime;
    }
}
