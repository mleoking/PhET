/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.model;

import edu.colorado.phet.movingman.plots.TimePoint;
import edu.colorado.phet.movingman.plots.TimeSeries;

/**
 * User: Sam Reid
 * Date: Apr 27, 2005
 * Time: 4:46:37 PM
 * Copyright (c) Apr 27, 2005 by Sam Reid
 */

public class CenteredDifferenceDerivative {

    public TimePoint getLatestDerivative( TimeSeries timeSeries ) {
        if( timeSeries.size() > 2 ) {
            double x1 = timeSeries.lastPointAt( 0 ).getValue();
            double x0 = timeSeries.lastPointAt( 2 ).getValue();
            double dx = x1 - x0;
            double dt = timeSeries.lastPointAt( 0 ).getTime() - timeSeries.lastPointAt( 2 ).getTime();
//            double diffValue = dx / dt / 2; //centered differentiation
            double diffValue = dx / dt; //centered differentiation
//            System.out.println( "dx = " + dx + ", dt=" + dt );

            double middleTime = ( timeSeries.lastPointAt( 0 ).getTime() + timeSeries.lastPointAt( 2 ).getTime() ) / 2.0;
            return new TimePoint( diffValue, middleTime );
        }
        else {
            return null;
        }
    }
}
