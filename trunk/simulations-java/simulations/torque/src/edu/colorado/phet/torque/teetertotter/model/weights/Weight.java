// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.model.weights;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.torque.teetertotter.model.UserMovableModelElement;

/**
 * Base class for all objects that can be placed on the balance.
 *
 * @author John Blanco
 */
public abstract class Weight implements UserMovableModelElement {
    public final BooleanProperty userControlled = new BooleanProperty( false );
    protected double rotationAngle = 0;
    private final double mass;

    public Weight( double mass ) {
        this.mass = mass;
    }

    public double getMass() {
        return mass;
    }

    public abstract void translate( double x, double y );

    public abstract void translate( ImmutableVector2D delta );

    public abstract Point2D getPosition();

    /**
     * Set the angle of rotation.  The point of rotation is the position
     * handle.  For a weight, that means that this method can be used to make
     * it appear to sit will on plank.
     *
     * @param angle rotational angle in radians.
     */
    public void setRotationAngle( double angle ) {
        rotationAngle = angle;
        // Override to implement the updates to the shape.
    }

    /**
     * The user has released this weight.
     */
    public void release() {
        userControlled.set( false );
    }
}
