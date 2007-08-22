package edu.colorado.phet.rotation;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.QuickProfiler;
import edu.umd.cs.piccolox.pswing.PSwingRepaintManager;
import edu.umd.cs.piccolox.pswing.SynchronizedPSwingRepaintManager;

import java.util.ArrayList;

/**
 * Author: Sam Reid
 * Aug 20, 2007, 1:32:16 AM
 */
public class RotationClock extends ConstantDtClock {
    private ArrayList tickTimes = new ArrayList();
    private long lastEvalTime;
    private long lastTickFinishTime=System.currentTimeMillis();
    private long lastActualDelay;

    public RotationClock( int delay, double clockDt ) {
        super( delay, clockDt );
    }

    protected void doTick() {
        lastActualDelay=System.currentTimeMillis()-lastTickFinishTime;
        tickTimes.add( new Long( System.currentTimeMillis() ) );
        if( tickTimes.size() > 100 ) {
            tickTimes.remove( 0 );
        }
        QuickProfiler qp = new QuickProfiler();
        super.doTick();
        lastEvalTime = qp.getTime();
        lastTickFinishTime=System.currentTimeMillis();
        SynchronizedPSwingRepaintManager.getInstance().updateLater();
//        SynchronizedPSwingRepaintManager.getInstance().update();
    }

    public long getLastActualDelay() {
        return lastActualDelay;
    }

    public long getLastEvalTime() {
        return lastEvalTime;
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
