package edu.colorado.phet.common.phetcommon.updates;

import edu.colorado.phet.common.phetcommon.preferences.PhetPreferences;

public class DefaultUpdateTimer implements IUpdateTimer {
    
    private static final long DEFAULT_DURATION = 1000 * 60 * 60 * 24; // ms, 1 day
    
    private final String project, sim;
    private long duration;

    public DefaultUpdateTimer( String project, String sim ) {
        this.project = project;
        this.sim = sim;
        duration = DEFAULT_DURATION;
    }
    
    public void setStartTime( long time ) {
        PhetPreferences.getInstance().setSimAskMeLater( project, sim, time );
    }

    public long getStartTime() {
        return PhetPreferences.getInstance().getSimAskMeLater( project, sim );
    }

    public void setDuration( long duration ) {
        this.duration = duration;
    }
    
    public long getDuration() {
        return duration;
    }
    
    public boolean isDurationExceeded() {
        long askMeLaterPressed = PhetPreferences.getInstance().getSimAskMeLater( project, sim );
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - askMeLaterPressed;
//        System.out.println( "elapsedTime/1000.0 = " + elapsedTime / 1000.0+" sec" );
        return elapsedTime > duration || askMeLaterPressed == 0;
    }
   
}
