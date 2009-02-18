package edu.colorado.phet.common.phetcommon.updates;

import edu.colorado.phet.common.phetcommon.preferences.PhetPreferences;

/**
 * "Ask Me Later" strategy for installer updates.
 */
public class InstallerAskMeLaterStrategy implements IAskMeLaterStrategy {
    
    private static final long DEFAULT_DURATION = 1000 * 60 * 60 * 24 * 30; // ms, 30 days
    
    private long duration;

    public InstallerAskMeLaterStrategy() {
        duration = DEFAULT_DURATION;
    }
    
    public void setStartTime( long time ) {
        PhetPreferences.getInstance().setInstallerAskMeLater( time );
    }

    public long getStartTime() {
        return PhetPreferences.getInstance().getInstallerAskMeLater();
    }

    public void setDuration( long duration ) {
        this.duration = duration;
    }
    
    public long getDuration() {
        return duration;
    }
    
    public boolean isDurationExceeded() {
        long askMeLaterPressed = getStartTime();
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - askMeLaterPressed;
        return elapsedTime > getDuration() || askMeLaterPressed == 0;
    }
   
}
