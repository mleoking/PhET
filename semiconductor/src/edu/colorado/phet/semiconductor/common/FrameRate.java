/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.semiconductor.common;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.simpleobservable.SimpleObservable;

import java.util.ArrayList;

public class FrameRate extends SimpleObservable implements ClockTickListener {
    ArrayList times = new ArrayList();
    int size = 50;
    private double fps;

    public double getFrameTimeMillis() {
        return frameTimeMillis;
    }

    private double frameTimeMillis;

    public void clockTicked( AbstractClock abstractClock, double v ) {
        long time = System.currentTimeMillis();
        if( times.size() < size ) {
            times.add( new Long( time ) );
        }
        else {
            times.remove( 0 );
            times.add( new Long( time ) );
            Long start = (Long)times.get( 0 );
            double dt = time - start.doubleValue();
            //fps=size/dt*1000;
//            double timeSec = dt / 1000;
//            double fps = size / timeSec;
            this.frameTimeMillis = dt / size;
//                System.out.println("fps = " + fps);
            this.fps = 1000 / frameTimeMillis;
            updateObservers();
        }
    }

    public double getFps() {
        return fps;
    }

}