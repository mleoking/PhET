package edu.colorado.phet.energyskatepark.model;

import edu.colorado.phet.energyskatepark.test.phys1d.LinearSpline2D;

import java.awt.geom.Point2D;

/**
 * Author: Sam Reid
 * Mar 28, 2007, 6:57:35 PM
 */
public class LinearFloorSpline2D extends LinearSpline2D implements TrackWithFriction {
    private double friction = 0.02;

    public LinearFloorSpline2D( Point2D[] point2Ds ) {
        super( point2Ds );
    }

    public double getFriction() {
        return friction;
    }
}
