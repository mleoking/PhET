package edu.colorado.phet.common.phetcommon.updates;

import edu.colorado.phet.common.phetcommon.preferences.PhetPreferences;

/**
 * DefaultVersionSkipper skips a version by recording the 
 * skipped version number in the user's preferences.
 */
public class DefaultVersionSkipper implements IVersionSkipper {
    
    private final String project, sim;
    
    public DefaultVersionSkipper( String project, String sim ) {
        this.project = project;
        this.sim = sim;
    }
    
    public void setSkippedVersion( int skipVersion ) {
        PhetPreferences.getInstance().setSimSkipUpdate( project, sim, skipVersion );
    }
    
    public int getSkippedVersion() {
        return PhetPreferences.getInstance().getSimSkipUpdate( project, sim );
    }
    
    public boolean isSkipped( int version ) {
        return version <= getSkippedVersion();
    }
}
