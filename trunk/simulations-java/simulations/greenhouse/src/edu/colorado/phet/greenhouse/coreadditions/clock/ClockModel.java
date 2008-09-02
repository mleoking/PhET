/**
 * Class: ClockModel
 * Package: edu.colorado.phet.coreadditions.clock
 * Author: Another Guy
 * Date: Aug 7, 2003
 */
package edu.colorado.phet.greenhouse.coreadditions.clock;

public abstract class ClockModel {
    private double dt;
    private int waitTime;

    public ClockModel( double dt, int waitTime ) {

        this.dt = dt;
        this.waitTime = waitTime;
    }

    public double getDt() {
        return dt;
    }

    public void setDt( double dt ) {
        this.dt = dt;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime( int waitTime ) {
        this.waitTime = waitTime;
    }

    //
    // Abstract methods
    //
    abstract long getMillisSinceLastTick();
}
