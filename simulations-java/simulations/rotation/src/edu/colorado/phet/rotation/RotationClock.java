package edu.colorado.phet.rotation;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.QuickProfiler;
import edu.umd.cs.piccolox.pswing.MyRepaintManager;

/**
 * Author: Sam Reid
 * Aug 20, 2007, 1:32:16 AM
 */
public class RotationClock extends ConstantDtClock {
    public static final int DEFAULT_DELAY = 30;
    public static final double DEFAULT_CLOCK_DT = DEFAULT_DELAY / 1000.0 / 2;

    private ArrayList tickTimes = new ArrayList();
    private long lastEvalTime;
    private long lastTickFinishTime = System.currentTimeMillis();
    private long lastActualDelay;

    private static final boolean DEBUG_PAINT_OVERHEAD = false;

    public RotationClock() {
        super( DEFAULT_DELAY, DEFAULT_CLOCK_DT );
        setRunning( false );
    }


    public void start() {
        super.start();    //To change body of overridden methods use File | Settings | File Templates.
    }

    protected void doTick() {
        lastActualDelay = System.currentTimeMillis() - lastTickFinishTime;
        tickTimes.add( new Long( System.currentTimeMillis() ) );
        if ( tickTimes.size() > 100 ) {
            tickTimes.remove( 0 );
        }
        QuickProfiler qp = new QuickProfiler();
        super.doTick();
        lastEvalTime = qp.getTime();
        lastTickFinishTime = System.currentTimeMillis();

        if ( DEBUG_PAINT_OVERHEAD && AbstractRotationModule.INSTANCE != null ) {
            JComponent component = AbstractRotationModule.INSTANCE.getRotationSimulationPanel();

            for ( int nx = 1; nx <= 10; nx++ ) {
                paintScreen( nx, nx, component );

            }
        }
        else {
            //if the repaint is scheduled for later, sometimes regions are dropped in the render process
            //therefore, we paint immediately here
//            SynchronizedPSwingRepaintManager.getInstance().update();
            MyRepaintManager.getInstance().doUpdateNow();
        }
    }

    private void paintScreen( int nx, int ny, JComponent component ) {
        QuickProfiler qp2 = new QuickProfiler( "nx=" + nx + ", ny=" + ny );
        int w = component.getWidth() / nx;
        int h = component.getHeight() / ny;
        for ( int i = 0; i < nx; i++ ) {
            for ( int j = 0; j < ny; j++ ) {

                Rectangle repaintRegion = new Rectangle( i * w, j * h, w, h );
                component.paintImmediately( repaintRegion );
            }
        }
        qp2.println();
    }

    public long getLastActualDelay() {
        return lastActualDelay;
    }

    public long getLastEvalTime() {
        return lastEvalTime;
    }

    public double getLastFrameRate() {
        if ( tickTimes.size() < 2 ) {
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
        return ( (Long) tickTimes.get( i ) ).longValue();
    }

}
