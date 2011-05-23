// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro;

import org.jbox2d.common.Vec2;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * Interface used by coulomb force calculation.
 *
 * @author Sam Reid
 */
interface Particle {
    Vec2 getBox2DPosition();

    double getCharge();

    ImmutableVector2D getModelPosition();

    //Sets the model position, and updates the box2d position
    void setModelPosition( ImmutableVector2D immutableVector2D );
}
