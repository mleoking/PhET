/*PhET, 2004.*/
package edu.colorado.phet.theramp.timeseries;


/**
 * User: Sam Reid
 * Date: Jul 1, 2003
 * Time: 3:42:23 PM
 */
public class DataSuite {
    private TimeSeries timeSeries = new TimeSeries();
    private TimeSeries smoothedSeries = new TimeSeries();
    private int numSmoothingPoints;

    public DataSuite( int numSmoothingPoints ) {
        this.numSmoothingPoints = numSmoothingPoints;
    }

    public void updateSmoothedSeries() {
        int numPtsToAvg = Math.min( numSmoothingPoints, timeSeries.size() );
        if( numPtsToAvg == 0 ) {
            System.out.println( "No points to average." );
            return;
        }
        double valSum = 0;
        double timeSum = 0;
        for( int i = 0; i < numPtsToAvg; i++ ) {
            valSum += timeSeries.lastPointAt( i ).getValue();
            timeSum += timeSeries.lastPointAt( i ).getTime();
        }
        double average = valSum / numPtsToAvg;
        double timeAverage = timeSum / numPtsToAvg;
        if( Double.isNaN( average ) ) {
            throw new RuntimeException( "NaN result for average." );
        }

        smoothedSeries.addPoint( average, timeAverage );
    }

    public TimeSeries getSmoothedDataSeries() {
        return smoothedSeries;
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

    public void addPoint( TimePoint dx ) {
        timeSeries.addPoint( dx.getValue(), dx.getTime() );
    }

    public TimeSeries getRawData() {
        return timeSeries;
    }
}