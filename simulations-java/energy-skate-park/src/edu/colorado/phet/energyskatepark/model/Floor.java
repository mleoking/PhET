/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.model;

import edu.colorado.phet.energyskatepark.model.physics.ParametricFunction2D;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Sep 21, 2005
 * Time: 3:05:30 AM
 * Copyright (c) Sep 21, 2005 by Sam Reid
 */

public class Floor {
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
        return new LinearFloorSpline2D( new Point2D[]{new Point2D.Double( -100, y ), new Point2D.Double( 200, y )} );
    }
}
