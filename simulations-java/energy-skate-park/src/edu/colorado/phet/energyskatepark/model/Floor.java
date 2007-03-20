/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.model;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.energyskatepark.model.spline.AbstractSpline;
import edu.colorado.phet.energyskatepark.test.phys1d.LinearSpline2D;
import edu.colorado.phet.energyskatepark.test.phys1d.ParametricFunction2D;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Sep 21, 2005
 * Time: 3:05:30 AM
 * Copyright (c) Sep 21, 2005 by Sam Reid
 */

public class Floor {
    private double y;
    private Vector2D normal = new Vector2D.Double( 0, 1 );
    private EnergySkateParkModel model;

    public Floor( EnergySkateParkModel model ) {
        this( model, -AbstractSpline.SPLINE_THICKNESS );
    }

    public Floor( EnergySkateParkModel model, double y ) {
        this.model = model;
        this.y = y;
    }

    public Floor copyState() {
        Floor copy = new Floor( model, y );
        copy.normal.setComponents( normal.getX(), normal.getY() );
        return copy;
    }

    public double getY() {
        return y;
    }

    public ParametricFunction2D getParametricFunction2D() {
        return new LinearSpline2D( new Point2D[]{new Point2D.Double( -100, y ), new Point2D.Double( 200, y )} );
    }
}
