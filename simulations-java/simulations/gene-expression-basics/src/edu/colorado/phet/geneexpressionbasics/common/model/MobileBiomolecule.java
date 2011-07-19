// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Base class for all biomolecules (i.e. rna polymerase, transciption factors,
 * etc.) that move around within the simulation.
 *
 * @author John Blanco
 */
public class MobileBiomolecule {
    private final Color baseColor;
    protected final Property<Shape> shapeProperty;

    public MobileBiomolecule( Shape initialShape, Color baseColor ) {
        this.baseColor = baseColor;
        this.shapeProperty = new Property<Shape>( initialShape );
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

    public void setPosition( Point2D newPos ) {
        // This default implementation assumes that the position indicator is
        // defined by the center of the shape's bounds.  Override if some
        // other behavior is required.
        translate( new Vector2D( newPos.getX() - shapeProperty.get().getBounds2D().getCenterX(),
                                 newPos.getY() - shapeProperty.get().getBounds2D().getCenterY() ) );
    }

    public Color getBaseColor() {
        return baseColor;
    }
}
