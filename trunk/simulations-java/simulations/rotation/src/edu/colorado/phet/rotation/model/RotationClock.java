package edu.colorado.phet.rotation.model;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.rotation.MyRepaintManager;
import edu.umd.cs.piccolox.pswing.PSwingRepaintManager;

import java.util.ArrayList;

/**
 * Author: Sam Reid
 * Aug 20, 2007, 1:32:16 AM
 */
public class RotationClock extends ConstantDtClock {
    private static final double FPS = 20;
    public static final int DELAY = (int) ( 1000.0 / FPS );
    public static final double DEFAULT_CLOCK_DT = DELAY / 1000.0 / 2;

    private ArrayList tickTimes = new ArrayList();

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
        MyRepaintManager.getInstance().setDoMyCoalesce( getRunningClocks() > 0 );
    }

    protected void doTick() {
        tickTimes.add( new Long( System.currentTimeMillis() ) );
        if ( tickTimes.size() > 100 ) {
            tickTimes.remove( 0 );
        }
        super.doTick();

        //if the repaint is scheduled for later, sometimes regions are dropped in the render process
        //therefore, we paint immediately here
        MyRepaintManager.getInstance().doUpdateNow();
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
