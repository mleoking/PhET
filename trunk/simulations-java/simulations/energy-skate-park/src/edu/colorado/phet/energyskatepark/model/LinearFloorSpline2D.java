// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.model;

import edu.colorado.phet.common.phetcommon.math.SerializablePoint2D;
import edu.colorado.phet.energyskatepark.model.physics.LinearSpline2D;
import edu.colorado.phet.energyskatepark.model.physics.TrackWithStickiness;

/**
 * Author: Sam Reid
 * Mar 28, 2007, 6:57:35 PM
 */
public class LinearFloorSpline2D extends LinearSpline2D implements TrackWithFriction, TrackWithStickiness {
    private final double friction;
    private final double stickiness = 1.0;

    public LinearFloorSpline2D( SerializablePoint2D[] point2Ds, double friction ) {
        super( point2Ds );
        this.friction = friction;
    }

    public double getFriction() {
        return friction;
    }

    public double getStickiness() {
        return stickiness;
    }
}
