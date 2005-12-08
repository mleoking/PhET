/**
 * Class: DynamicClockModel
 * Package: edu.colorado.phet.coreadditions.clock
 * Author: Another Guy
 * Date: Aug 7, 2003
 */
package edu.colorado.phet.coreadditions.clock;

public class DynamicClockModel extends ClockModel {

    private long lastTickTime = System.currentTimeMillis();

    public DynamicClockModel( double dt, int waitTime ) {
        super( dt, waitTime );
    }

    public long getMillisSinceLastTick() {
        long nextTickTime = System.currentTimeMillis();
        long dt = nextTickTime - lastTickTime;
        lastTickTime = nextTickTime;
        return dt;
    }
}
