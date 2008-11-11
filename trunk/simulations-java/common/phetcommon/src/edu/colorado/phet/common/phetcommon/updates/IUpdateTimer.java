package edu.colorado.phet.common.phetcommon.updates;

/**
 * IUpdateTimer is the interface for requesting that updates be ignored until some later time.
 */
public interface IUpdateTimer {
    
    /**
     * Sets the time at which the user requested to defer update checks.
     * @param project
     * @param name
     * @param time
     */
    void setTimerStartTime( String project, String name, long time );
}
