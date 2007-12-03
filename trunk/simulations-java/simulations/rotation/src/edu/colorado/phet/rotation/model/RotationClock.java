package edu.colorado.phet.rotation.model;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.umd.cs.piccolox.pswing.MyRepaintManager;
import edu.umd.cs.piccolox.pswing.PSwingRepaintManager;

import java.util.ArrayList;

/**
 * Author: Sam Reid
 * Aug 20, 2007, 1:32:16 AM
 */
public class RotationClock extends ConstantDtClock {
    private static final double FPS = 20;
    public static final int DELAY = (int) ( 1000.0 / FPS );
    //    public static final int DELAY = (int) ( 1000.0 / 20.0 );
    public static final double DEFAULT_CLOCK_DT = DELAY / 1000.0 / 2;

    private ArrayList tickTimes = new ArrayList();
    private long lastTickFinishTime = System.currentTimeMillis();
    private long lastTickStartTime;

    private static ArrayList clocks = new ArrayList();

    public RotationClock() {
        super( DELAY, DEFAULT_CLOCK_DT );
        clocks.add( this );
        addClockListener( new ClockAdapter() {
            public void clockStarted( ClockEvent clockEvent ) {
                updateRepaintManager();
            }

            public void clockPaused( ClockEvent clockEvent ) {
                updateRepaintManager();
            }
        } );

        //this.setCoalesce(false);
    }

    private void updateRepaintManager() {
//        System.out.println( "RotationClock.updateRepaintManager, clocksrunning=" + getRunningClocks() );
        PSwingRepaintManager.getInstance().setDoMyCoalesce( getRunningClocks() > 0 );
    }

    protected void doTick() {
        long tickStartTime = System.currentTimeMillis();
//        System.out.println( "off-time=" + ( lastTickFinishTime - tickStartTime ) );
        long tickDelay = tickStartTime - lastTickStartTime;
        System.out.println( "Elapsed="+(System.currentTimeMillis()-lastTickStartTime) );
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
//        try {
//            Thread.sleep(60);
//        }
//        catch( InterruptedException e ) {
//            e.printStackTrace();
//        }
//        }
        //see how long has passed:
//        long elapsed = System.currentTimeMillis() - tickStartTime;
//        if ( elapsed < DELAY ) {
//            try {
////                System.out.println( "didn't sleep long enough: sleeping: " + ( DELAY - elapsed) );
//                Thread.sleep( DELAY - elapsed );
//            }
//            catch( InterruptedException e ) {
//                e.printStackTrace();
//            }
//        }
//        System.out.println( "tickDelay = " + tickDelay );

        long tickFinishTime = System.currentTimeMillis();
        lastTickFinishTime = tickFinishTime;

//        System.out.println( "tickDelay=" + tickDelay + ", dt(offclock)=" + dt + ", DELAY=" + DELAY +" elapsed="+(System.currentTimeMillis()-tickStartTime));
//        System.out.println( "tickDelay=\t" + tickDelay );
    }

    public int getRunningClocks() {
        int count = 0;
        for ( int i = 0; i < clocks.size(); i++ ) {
            RotationClock rotationClock = (RotationClock) clocks.get( i );
            if ( rotationClock.isRunning() ) {
                count++;
            }
        }
        return count;
    }
}
