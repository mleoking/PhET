// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.model.weights;

import java.awt.*;

import edu.colorado.phet.torque.teetertotter.model.ModelObject;
import edu.colorado.phet.torque.teetertotter.model.UserMovableModelElement;

/**
 * Base class for all objects that can be placed on the balance.
 *
 * @author John Blanco
 */
public abstract class Weight extends ModelObject implements UserMovableModelElement {
    private final double mass;
    protected double rotationAngle;

    public Weight( Shape shape, double mass ) {
        super( shape );
        this.mass = mass;
    }

    public double getMass() {
        return mass;
    }


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
