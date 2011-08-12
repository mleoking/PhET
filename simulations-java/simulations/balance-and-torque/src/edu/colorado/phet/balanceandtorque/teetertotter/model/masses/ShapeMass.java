// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model.masses;

import java.awt.Shape;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * This class defines a mass in the model whose appearance is defined by its
 * shape as opposed to, say, an image.
 *
 * @author John Blanco
 */
public abstract class ShapeMass extends Mass {
    public final Property<Shape> shapeProperty;

    public ShapeMass( double mass, Shape shape ) {
        super( mass );
        shapeProperty = new Property<Shape>( shape );
    }

    @Override public Point2D getMiddlePoint() {
        return new Point2D.Double( shapeProperty.get().getBounds2D().getCenterX(), shapeProperty.get().getBounds2D().getCenterY() );
    }
}
