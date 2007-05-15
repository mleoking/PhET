/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.model;

import edu.colorado.phet.energyskatepark.model.physics.ParametricFunction2D;

import edu.colorado.phet.common.phetcommon.math.SPoint2D;

import java.io.Serializable;

/**
 * User: Sam Reid
 * Date: Sep 21, 2005
 * Time: 3:05:30 AM
 *
 */

public class Floor implements Serializable {
    private double y;
    private EnergySkateParkModel model;

    public Floor( EnergySkateParkModel model ) {
        this( model, 0 );
    }

    public Floor( EnergySkateParkModel model, double y ) {
        this.model = model;
        this.y = y;
    }

    public Floor copyState() {
        return new Floor( model, y );
    }

    public double getY() {
        return y;
    }

    public ParametricFunction2D getParametricFunction2D() {
        return new LinearFloorSpline2D( new SPoint2D[]{new SPoint2D( -100, y ), new SPoint2D( 200, y )} );
    }
}
