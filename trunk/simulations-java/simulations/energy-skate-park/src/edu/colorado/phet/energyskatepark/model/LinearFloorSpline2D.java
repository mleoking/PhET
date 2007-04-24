package edu.colorado.phet.energyskatepark.model;

import edu.colorado.phet.energyskatepark.model.physics.LinearSpline2D;
import edu.colorado.phet.energyskatepark.model.physics.TrackWithStickiness;

import edu.colorado.phet.energyskatepark.model.SPoint2D;

/**
 * Author: Sam Reid
 * Mar 28, 2007, 6:57:35 PM
 */
public class LinearFloorSpline2D extends LinearSpline2D implements TrackWithFriction, TrackWithStickiness {
    private double friction = 0.02;
    private double stickiness = 1.0;

    public LinearFloorSpline2D( SPoint2D[] point2Ds ) {
        super( point2Ds );
    }

    public double getFriction() {
        return friction;
    }

    public double getStickiness() {
        return stickiness;
    }
}
