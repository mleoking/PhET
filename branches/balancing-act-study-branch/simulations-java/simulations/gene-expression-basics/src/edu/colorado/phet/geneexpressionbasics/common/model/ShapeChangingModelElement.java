// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import java.awt.Shape;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Base class for elements in the model that change shape and/or move around.
 *
 * @author John Blanco
 */
public abstract class ShapeChangingModelElement {

    // Shape property, which is not public because it should only be changed
    // by descendants of the class.
    protected final Property<Shape> shapeProperty;

    public ShapeChangingModelElement( Shape initialShape ) {
        this.shapeProperty = new Property<Shape>( initialShape );
    }

    public Shape getShape() {
        return shapeProperty.get();
    }

    public void addShapeChangeObserver( VoidFunction1<Shape> shapeChangeObserver ) {
        shapeProperty.addObserver( shapeChangeObserver );
    }

    public void removeShapeChangeObserver( VoidFunction1<Shape> shapeChangeObserver ) {
        shapeProperty.removeObserver( shapeChangeObserver );
    }

    public void translate( Vector2D translationVector ) {
        AffineTransform translationTransform = AffineTransform.getTranslateInstance( translationVector.getX(), translationVector.getY() );
        shapeProperty.set( translationTransform.createTransformedShape( shapeProperty.get() ) );
    }

    public void setPosition( Vector2D newPos ) {
        setPosition( newPos.getX(), newPos.getY() );
    }

    public void setPosition( double x, double y ) {
        if ( x != getPosition().getX() || y != getPosition().getY() ) {
            // This default implementation assumes that the position indicator
            // is defined by the center of the shape's bounds.  Override if
            // some other behavior is required.
            translate( new Vector2D( x - shapeProperty.get().getBounds2D().getCenterX(),
                                     y - shapeProperty.get().getBounds2D().getCenterY() ) );
        }
    }

    public Vector2D getPosition() {
        // Assumes that the center of the shape is the position.  Override if
        // other behavior is needed.
        return new Vector2D( shapeProperty.get().getBounds2D().getCenterX(), shapeProperty.get().getBounds2D().getCenterY() );
    }
}
