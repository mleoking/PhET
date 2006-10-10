/* Copyright 2004, Sam Reid */
package edu.colorado.phet.energyskatepark.model;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;

/**
 * User: Sam Reid
 * Date: Sep 29, 2005
 * Time: 9:40:01 AM
 * Copyright (c) Sep 29, 2005 by Sam Reid
 */

public class RVector2D {
    private AbstractVector2D vector;
    private AbstractVector2D unitParallel;
    private double parallel;
    private double perpendicular;

    public RVector2D( AbstractVector2D vector, AbstractVector2D parallelDirVector ) {
        this.vector = vector;
        this.unitParallel = parallelDirVector.getNormalizedInstance();
        this.parallel = unitParallel.dot( vector );
        this.perpendicular = unitParallel.getNormalVector().dot( vector );
    }

    public double getParallel() {
        return unitParallel.dot( vector );
    }

    public double getPerpendicular() {
        return unitParallel.getNormalVector().dot( vector );
    }

    private Vector2D.Double perpToCartesian( double parallel, double vPerp ) {
        Vector2D.Double newVelocity = new Vector2D.Double( parallel, vPerp );
        newVelocity.rotate( -unitParallel.getAngle() );
        newVelocity.setY( -newVelocity.getY() );
        return newVelocity;
    }

    public void setPerpendicular( double perpendicular ) {
        this.perpendicular = perpendicular;
    }

    public Vector2D.Double toCartesianVector() {
        return perpToCartesian( parallel, perpendicular );
    }

    public void setParallel( double parallel ) {
        this.parallel = parallel;
    }
}
