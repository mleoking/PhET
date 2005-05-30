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

public class Taylor2ndDerivative {
//    public TimePoint getLatestDerivative( TimeSeries smoothedDataSeries ) {
//
//        return null;
//    }

    public TimePoint getLatestDerivative( TimeSeries timeSeries ) {
        if( timeSeries.size() > 3 ) {
            TimePoint a = timeSeries.lastPointAt( 2 );
            TimePoint b = timeSeries.lastPointAt( 1 );
            TimePoint c = timeSeries.lastPointAt( 0 );
            double h = ( a.getTime() - c.getTime() ) / 2.0;

            double f2 = a.getValue();
            double f1 = b.getValue();
            double f0 = c.getValue();

            double num = f0 - 2 * f1 + f2;
            double denom = h * h;
//            double num = f1 - f0;
//            double denom = timeSeries.lastPointAt( 0 ).getTime() - timeSeries.lastPointAt( 2 ).getTime();
//            double diffValue = num / denom / 2; //centered differentiation
            double diffValue = num / denom; //centered differentiation
//            System.out.println( "num = " + num + ", denom=" + denom );

            double middleTime = ( timeSeries.lastPointAt( 0 ).getTime() + timeSeries.lastPointAt( 2 ).getTime() ) / 2.0;
            return new TimePoint( diffValue, middleTime );
        }
        else {
            return null;
        }
    }
}
