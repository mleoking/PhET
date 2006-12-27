/*PhET, 2004.*/
package edu.colorado.phet.forces1d.model;

import edu.colorado.phet.common.math.Average;

/**
 * User: Sam Reid
 * Date: Jul 1, 2003
 * Time: 3:42:23 PM
 * Copyright (c) Jul 1, 2003 by Sam Reid
 */
public class SmoothDataSeries {
    private DataSeries data = new DataSeries();
    private DataSeries smoothed = new DataSeries();
    private SmoothDataSeries derivative;
    private int windowSize;

    public SmoothDataSeries( int windowSize ) {
        this.windowSize = windowSize;
    }

    public void setDerivative( SmoothDataSeries derivative ) {
        this.derivative = derivative;
    }

    public void updateSmoothedSeries() {
        Average avg = new Average();

        int numPtsToAvg = windowSize;
        numPtsToAvg = Math.min( numPtsToAvg, data.size() );
        for( int i = 0; i < numPtsToAvg; i++ ) {
            avg.update( data.lastPointAt( i ) );
        }
        double value = avg.value();
        if( Double.isNaN( value ) ) {
            value = 0;
        }
        smoothed.addPoint( value );
    }

    public DataSeries getSmoothedDataSeries() {
        return smoothed;
    }

    public void updateDerivative( double dt ) {
        DataSeries dataToDerive = this.smoothed;
        if( dataToDerive.size() > 2 ) {
            double x1 = dataToDerive.lastPointAt( 0 );
            double x0 = dataToDerive.lastPointAt( 2 );
            double dx = x1 - x0;
            double vel = dx / dt / 2;
            derivative.addPoint( vel );
        }
    }

    public void addPoint( double pt ) {
        data.addPoint( pt );
    }

    public int numSmoothedPoints() {
        return smoothed.size();
    }

    public void reset() {
        data.reset();
        smoothed.reset();
    }

    public double smoothedPointAt( int index ) {
        return smoothed.pointAt( index );
    }

    public void setWindowSize( int windowSize ) {
        this.windowSize = windowSize;
    }

}