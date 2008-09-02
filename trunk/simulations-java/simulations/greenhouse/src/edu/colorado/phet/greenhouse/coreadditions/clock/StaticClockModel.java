/**
 * Class: StaticClockModel
 * Package: edu.colorado.phet.coreadditions.clock
 * Author: Another Guy
 * Date: Aug 7, 2003
 */
package edu.colorado.phet.greenhouse.coreadditions.clock;

public class StaticClockModel extends ClockModel {

    public StaticClockModel( double dt, int waitTime ) {
        super( dt, waitTime );
    }

    public long getMillisSinceLastTick() {
        return (long)getDt();
    }
}
