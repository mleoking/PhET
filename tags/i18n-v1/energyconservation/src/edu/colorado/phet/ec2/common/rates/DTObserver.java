/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.common.rates;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.coreadditions.math.CircularBuffer;

/**
 * User: Sam Reid
 * Date: Jul 24, 2003
 * Time: 3:42:43 PM
 * Copyright (c) Jul 24, 2003 by Sam Reid
 */
public class DTObserver extends ModelElement {
    double dt;
    CircularBuffer cb;
    private double max;
    private double min;
    private double avg;

    public DTObserver( int windowSize ) {
        cb = new CircularBuffer( windowSize );
    }

    public void stepInTime( double dt ) {
        this.dt = dt;
        cb.addPoint( dt );
        max = cb.maxValue();
        min = cb.minValue();
        avg = cb.average();
        updateObservers();
//        O.d("DT="+dt);
    }

    public double getDt() {
        return dt;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getAverage() {
        return avg;
    }
}
