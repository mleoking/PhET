// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.model;

import java.io.Serializable;

import edu.colorado.phet.common.phetcommon.math.SerializablePoint2D;
import edu.colorado.phet.common.spline.ParametricFunction2D;

public class Floor implements Serializable {
    private double y = 0.0;

    //Friction is disabled in basics tab 1, but in other tabs it uses this value
    public static final double DEFAULT_FRICTION = 0.02;

    //Additional friction to be added to the skater friction, see Particle1D.getTotalFriction
    private final double friction;

    public Floor( double friction ) {
        this.friction = friction;
    }

    public double getY() {
        return y;
    }

    public void setY( double y ) {
        this.y = y;
    }

    public ParametricFunction2D getParametricFunction2D() {
        return new LinearFloorSpline2D( new SerializablePoint2D[] { new SerializablePoint2D( -100, y ), new SerializablePoint2D( 200, y ) }, friction );
    }
}