// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.model;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * An object in the model for the torque sim.  The shape contains its location.
 *
 * @author John Blanco
 * @author Sam Reid
 */
public abstract class ModelObject {
    private final Property<Shape> shapeProperty;

    public ModelObject( Shape shape ) {
        this.shapeProperty = new Property<Shape>( shape );
    }

    public Shape getShape() {
        return shapeProperty.get();
    }

    public Property<Shape> getShapeProperty() {
        return shapeProperty;
    }

    protected void setShapeProperty( Shape newShape ) {
        shapeProperty.set( newShape );
    }
}
