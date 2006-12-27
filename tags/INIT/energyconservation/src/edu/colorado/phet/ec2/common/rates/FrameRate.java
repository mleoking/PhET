/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.common.rates;

import edu.colorado.phet.common.model.ModelElement;

/**
 * User: Sam Reid
 * Date: Jul 24, 2003
 * Time: 3:22:45 PM
 * Copyright (c) Jul 24, 2003 by Sam Reid
 */
public class FrameRate extends ModelElement {
    private int windowSize;
    int callCount = 0;
    long lastTime = Long.MIN_VALUE;
    boolean init = false;
    double frameRate = Double.NaN;

    public FrameRate( int windowSize ) {
        this.windowSize = windowSize;
    }

    public void stepInTime( double dt ) {
        if( !init ) {
            lastTime = System.currentTimeMillis();
            init = true;
        }
        else {
            if( callCount >= windowSize ) {
                long now = System.currentTimeMillis();
                long timeDiff = now - lastTime;
                frameRate = callCount / ( timeDiff / 1000f );
                updateObservers();
                lastTime = now;
                callCount = 0;
            }
            callCount++;
        }
    }

    public double getFrameRate() {
        return frameRate;
    }

}
