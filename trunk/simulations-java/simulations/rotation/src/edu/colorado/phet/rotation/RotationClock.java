package edu.colorado.phet.rotation;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.umd.cs.piccolox.pswing.MyRepaintManager;

/**
 * Author: Sam Reid
 * Aug 20, 2007, 1:32:16 AM
 */
public class RotationClock extends ConstantDtClock {
    public static final int DELAY = (int) ( 1000.0 / 30.0 );
    public static final double DEFAULT_CLOCK_DT = DELAY / 1000.0 / 2;

    private ArrayList tickTimes = new ArrayList();
    private long lastTickFinishTime = System.currentTimeMillis();
    private long lastTickStartTime;

    public RotationClock() {
        super( 2, DEFAULT_CLOCK_DT );
        setRunning( false );
    }

    protected void doTick() {
        long tickStartTime = System.currentTimeMillis();
//        System.out.println( "off-time=" + ( lastTickFinishTime - tickStartTime ) );
        long tickDelay = tickStartTime - lastTickStartTime;
        lastTickStartTime = System.currentTimeMillis();

        long dt = ( tickStartTime - lastTickFinishTime );
//        System.out.println( "time since last tick finished=" + dt );

        tickTimes.add( new Long( System.currentTimeMillis() ) );
        if ( tickTimes.size() > 100 ) {
            tickTimes.remove( 0 );
        }
        super.doTick();

        //if the repaint is scheduled for later, sometimes regions are dropped in the render process
        //therefore, we paint immediately here
        MyRepaintManager.getInstance().doUpdateNow();
//        }
        //see how long has passed:
        long elapsed = System.currentTimeMillis() - tickStartTime;
        if ( elapsed < DELAY ) {
            try {
//                System.out.println( "didn't sleep long enough: sleeping: " + ( DELAY - elapsed) );
                Thread.sleep( DELAY - elapsed );
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }
        }
//        System.out.println( "tickDelay = " + tickDelay );

        long tickFinishTime = System.currentTimeMillis();
        lastTickFinishTime = tickFinishTime;

//        System.out.println( "tickDelay=" + tickDelay + ", dt(offclock)=" + dt + ", DELAY=" + DELAY +" elapsed="+(System.currentTimeMillis()-tickStartTime));
//        System.out.println( "tickDelay=\t" + tickDelay );
    }

}
