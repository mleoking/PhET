/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.model;

import edu.colorado.phet.movingman.plots.TimePoint;
import edu.colorado.phet.movingman.plots.TimeSeries;

/**
 * User: Sam Reid
 * Date: Apr 27, 2005
 * Time: 4:47:43 PM
 * Copyright (c) Apr 27, 2005 by Sam Reid
 */

public class Taylor2ndDerivativeO4 {
//    public TimePoint getLatestDerivative( TimeSeries smoothedDataSeries ) {
//
//        return null;
//    }

    public TimePoint getLatestDerivative( TimeSeries timeSeries ) {
        if( timeSeries.size() >= 5 ) {
            TimePoint pt0 = timeSeries.lastPointAt( 0 );
            TimePoint pt1 = timeSeries.lastPointAt( 1 );
            TimePoint pt2 = timeSeries.lastPointAt( 2 );
            TimePoint pt3 = timeSeries.lastPointAt( 3 );
            TimePoint pt4 = timeSeries.lastPointAt( 4 );
            double h = ( pt0.getTime() - pt1.getTime() );

            double f0 = pt0.getValue();
            double f1 = pt1.getValue();
            double f2 = pt2.getValue();
            double f3 = pt3.getValue();
            double f4 = pt4.getValue();
            double num = -f0 - f4 + 16 * ( f1 + f3 ) - 30 * f2;
            double denom = h * h;
//            double num = f1 - f0;
//            double denom = timeSeries.lastPointAt( 0 ).getTime() - timeSeries.lastPointAt( 2 ).getTime();
//            double diffValue = num / denom / 2; //centered differentiation
            double diffValue = num / denom; //centered differentiation
//            System.out.println( "num = " + num + ", denom=" + denom );

            double middleTime = ( timeSeries.lastPointAt( 0 ).getTime() + timeSeries.lastPointAt( 4 ).getTime() ) / 2.0;
            return new TimePoint( diffValue, middleTime );
        }
        else {
            return null;
        }
    }
}
