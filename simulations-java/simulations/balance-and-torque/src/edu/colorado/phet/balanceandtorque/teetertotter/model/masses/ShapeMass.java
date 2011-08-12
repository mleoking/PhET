// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model.masses;

import java.awt.Shape;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * This class defines a mass in the model whose appearance is defined by its
 * shape as opposed to, say, an image.
 *
 * @author John Blanco
 */
public abstract class ShapeMass extends Mass {
    public final Property<Shape> shapeProperty;
    public final Shape shape;

    public ShapeMass( double mass, Shape shape ) {
        super( mass );
        shapeProperty = new Property<Shape>( shape );
        this.shape = shape;
    }

    @Override public Point2D getMiddlePoint() {
        // Start with the middle point of the raw shape.
        Vector2D middlePoint = new Vector2D( shapeProperty.get().getBounds2D().getCenterX(), shapeProperty.get().getBounds2D().getCenterY() );
        // Rotate.
        middlePoint.rotate( getRotationAngle() );
        // Translate to the correct position.
        middlePoint.add( new ImmutableVector2D( getPosition() ) );
        return middlePoint.toPoint2D();
    }

    /**
     * Get the basic, untranslated shape of this mass.
     *
     * @return
     */
    public Shape getShape() {
        return shape;
    }
}
