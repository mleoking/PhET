package edu.colorado.phet.common.phetcommon.updates;

/**
 * IUpdateTimer is the interface for requesting that updates be ignored until some later time.
 */
public interface IUpdateTimer {
    
    /**
     * Sets the time at which the user requested to defer update checks.
     * @param time
     */
    public void setStartTime( long time );
    
    public long getStartTime();
    
    public void setDuration( long duration );
    
    public long getDuration();
    
    public boolean isDurationExceeded();
}
