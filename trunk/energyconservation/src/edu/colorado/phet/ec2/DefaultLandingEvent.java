/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2;

import edu.colorado.phet.ec2.elements.spline.SegmentPath;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jun 21, 2003
 * Time: 2:41:08 PM
 * Copyright (c) Jun 21, 2003 by Sam Reid
 */
public class DefaultLandingEvent {

    public static double getSplineLocation( SegmentPath path, Point2D.Double carLoc, int numTestLocations ) {
        double closest = Double.POSITIVE_INFINITY;
        double pathLength = path.getLength();
        double testDist = 0;
        double dx = pathLength / numTestLocations;
        double bestLocation = pathLength / 2;
        for( int i = 0; i < numTestLocations; i++ ) {
            Point2D.Double projected = path.getPosition( testDist );
            double distance = projected.distance( carLoc );
            if( distance < closest ) {
                closest = distance;
                bestLocation = testDist;
            }
            testDist += dx;
        }
        return bestLocation;
    }
}
