/*PhET, 2004.*/
package edu.colorado.phet.movingman.model;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.movingman.plots.TimeSeries;

/**
 * User: Sam Reid
 * Date: Jul 1, 2003
 * Time: 3:42:23 PM
 * Copyright (c) Jul 1, 2003 by Sam Reid
 */
public class SmoothDataSeries {
    private TimeSeries timeSeries = new TimeSeries();
    private TimeSeries smoothedSeries = new TimeSeries();
    private SmoothDataSeries derivative;
    private int numSmoothingPoints;

    public SmoothDataSeries( int numSmoothingPoints ) {
        this.numSmoothingPoints = numSmoothingPoints;
    }

    public void setDerivative( SmoothDataSeries derivative ) {
        this.derivative = derivative;
    }

    public void updateSmoothedSeries() {
        MathUtil.Average avg = new MathUtil.Average();

        int numPtsToAvg = numSmoothingPoints;
        numPtsToAvg = Math.min( numPtsToAvg, timeSeries.size() );
        for( int i = 0; i < numPtsToAvg; i++ ) {
            avg.update( timeSeries.lastPointAt( i ).getValue() );
        }
        double value = avg.value();
        if( Double.isNaN( value ) ) {
            value = 0;
        }
        smoothedSeries.addPoint( value, timeSeries.getLastTime() );
    }

    public TimeSeries getSmoothedDataSeries() {
        return smoothedSeries;
    }

    public void updateDerivative( double dt ) {
        TimeSeries timeToDerive = this.smoothedSeries;
        if( timeToDerive.size() > 2 ) {
            double x1 = timeToDerive.lastPointAt( 0 ).getValue();
            double x0 = timeToDerive.lastPointAt( 2 ).getValue();
            double dx = x1 - x0;
            double vel = dx / dt / 2;
            derivative.addPoint( vel, timeToDerive.getLastPoint().getTime() );
        }
    }

    public void addPoint( double pt, double time ) {
        timeSeries.addPoint( pt, time );
    }

    public int numSmoothedPoints() {
        return smoothedSeries.size();
    }

    public void reset() {
        timeSeries.reset();
        smoothedSeries.reset();
    }

    public double smoothedPointAt( int index ) {
        return smoothedSeries.pointAt( index ).getValue();
    }

    public void setNumSmoothingPoints( int numSmoothingPoints ) {
        this.numSmoothingPoints = numSmoothingPoints;
    }

}