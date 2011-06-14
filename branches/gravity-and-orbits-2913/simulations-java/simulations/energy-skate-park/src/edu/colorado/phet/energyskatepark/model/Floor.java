// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.model;

import edu.colorado.phet.common.phetcommon.math.SerializablePoint2D;
import edu.colorado.phet.common.spline.ParametricFunction2D;

import java.io.Serializable;

/**
 * User: Sam Reid
 * Date: Sep 21, 2005
 * Time: 3:05:30 AM
 */

public class Floor implements Serializable {
    private double y;

    public Floor() {
        this( 0.0 );
    }

    public Floor( double y ) {
        this.y = y;
    }

    public double getY() {
        return y;
    }

    public void setY( double y ) {
        this.y = y;
    }

    public ParametricFunction2D getParametricFunction2D() {
        return new LinearFloorSpline2D( new SerializablePoint2D[]{new SerializablePoint2D( -100, y ), new SerializablePoint2D( 200, y )} );
    }
}
