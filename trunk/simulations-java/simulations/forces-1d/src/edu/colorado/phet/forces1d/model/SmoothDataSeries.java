/*PhET, 2004.*/
package edu.colorado.phet.forces1d.model;

import edu.colorado.phet.forces1d.common_force1d.math.Average;

/**
 * User: Sam Reid
 * Date: Jul 1, 2003
 * Time: 3:42:23 PM
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

    private void updateSmoothedSeries() {
        Average avg = new Average();

        int numPtsToAvg = windowSize;
        numPtsToAvg = Math.min( numPtsToAvg, data.size() );
        for ( int i = 0; i < numPtsToAvg; i++ ) {
            avg.update( data.lastPointAt( i ) );
        }
        double value = avg.value();
        if ( Double.isNaN( value ) ) {
            value = 0;
        }
        smoothed.addPoint( value );
    }

    public DataSeries getSmoothedDataSeries() {
        return smoothed;
    }

    public void updateDerivative( double dt ) {
        DataSeries dataToDerive = this.smoothed;
        if ( dataToDerive.size() > 2 ) {
            double x1 = dataToDerive.lastPointAt( 0 );
            double x0 = dataToDerive.lastPointAt( 2 );
            double dx = x1 - x0;//median algorithm is smoothest.
            double vel = dx / dt / 2;
            derivative.addPoint( vel );
        }
    }

    public void addPoint( double pt ) {
        data.addPoint( pt );
        updateSmoothedSeries();
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

    public void clearAfter( int index ) {
        for ( int i = data.size() - 1; i > index; i-- ) {
            data.remove( i );
        }
        for ( int i = smoothed.size() - 1; i > index; i-- ) {
            smoothed.remove( i );
        }
    }
}