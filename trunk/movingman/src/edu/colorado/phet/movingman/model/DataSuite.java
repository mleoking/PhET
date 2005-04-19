/*PhET, 2004.*/
package edu.colorado.phet.movingman.model;

import edu.colorado.phet.movingman.plots.TimeSeries;

/**
 * User: Sam Reid
 * Date: Jul 1, 2003
 * Time: 3:42:23 PM
 * Copyright (c) Jul 1, 2003 by Sam Reid
 */
public class DataSuite {
    private TimeSeries timeSeries = new TimeSeries();
    private TimeSeries smoothedSeries = new TimeSeries();
    private DataSuite derivative;
    private int numSmoothingPoints;

    public DataSuite( int numSmoothingPoints ) {
        this.numSmoothingPoints = numSmoothingPoints;
    }

    public void setDerivative( DataSuite derivative ) {
        this.derivative = derivative;
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
            average = 0;
            throw new RuntimeException( "NaN result for average." );
        }

        smoothedSeries.addPoint( average, timeAverage );
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
            double vel = dx / dt / 2; //centered differentiation
//            derivative.addPoint( vel, timeToDerive.getLastPoint().getTime() );
            double middleTime = ( timeToDerive.lastPointAt( 0 ).getTime() + timeToDerive.lastPointAt( 2 ).getTime() ) / 2.0;
            derivative.addPoint( vel, middleTime );
//            derivative.addPoint( vel, timeToDerive.lastPointAt( 1).getValue() );
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