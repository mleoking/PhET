/** Sam Reid*/
package edu.colorado.phet.forces1d.model;

import edu.colorado.phet.common.model.ModelElement;

/**
 * User: Sam Reid
 * Date: Nov 12, 2004
 * Time: 9:54:55 PM
 * Copyright (c) Nov 12, 2004 by Sam Reid
 */
public class Forces1DModel implements ModelElement {

    private static final double EARTH_GRAVITY = 9.8;
    private double gravity = EARTH_GRAVITY;
    private double appliedForce;
    private Block block;

    public Forces1DModel() {
        block = new Block();
    }

    public double getGravity() {
        return gravity;
    }

    public void setGravity( double gravity ) {
        this.gravity = gravity;
    }

    public double getAppliedForce() {
        return appliedForce;
    }

    public void setAppliedForce( double appliedForce ) {
        this.appliedForce = appliedForce;
    }

    public void stepInTime( double dt ) {
        block.stepInTime( dt );
    }
}
