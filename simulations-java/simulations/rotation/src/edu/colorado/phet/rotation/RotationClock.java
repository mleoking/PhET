package edu.colorado.phet.rotation;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

import java.util.ArrayList;

/**
 * Author: Sam Reid
 * Aug 20, 2007, 1:32:16 AM
 */
public class RotationClock extends ConstantDtClock {
    private ArrayList tickTimes = new ArrayList();

    public RotationClock( int delay, double clockDt ) {
        super( delay, clockDt );
    }

    protected void doTick() {
        tickTimes.add( new Long( System.currentTimeMillis() ) );
        if( tickTimes.size() > 100 ) {
            tickTimes.remove( 0 );
        }
        if( tickTimes.size() >= 2 ) {
            long a = getTickTime( tickTimes.size() - 1 );
            long b = getTickTime( tickTimes.size() - 2 );
            double deltaT = a - b;
            double frameRate = 1000.0 / deltaT;
            System.out.println( "dt=" + deltaT + "ms, Frame rate=" + frameRate + "Hz" );
        }
        super.doTick();
    }

    public double getLastFrameRate() {
        if( tickTimes.size() < 2 ) {
            return 0.0;
        }
        else {
            long a = getTickTime( tickTimes.size() - 1 );
            long b = getTickTime( tickTimes.size() - 2 );
            double deltaT = a - b;
            return 1000.0 / deltaT;
        }
    }

    private long getTickTime( int i ) {
        return ( (Long)tickTimes.get( i ) ).longValue();
    }

}
