// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;

/**
 * Base class for all biomolecules (i.e. rna polymerase, transciption factors,
 * etc.) that move around within the simulation.
 *
 * @author John Blanco
 */
public abstract class MobileBiomolecule extends ShapeChangingModelElement {

    public final BooleanProperty userControlled = new BooleanProperty( false );

    // Color to use when displaying this biomolecule to the user.  This is
    // a bit out of place here, and has nothing to do with the fact that the
    // molecule moves.  This was just a convenient place to put it (so far).
    private final Color baseColor;

    /**
     * Constructor.
     *
     * @param initialShape
     * @param baseColor
     */
    public MobileBiomolecule( Shape initialShape, Color baseColor ) {
        super( initialShape );
        this.baseColor = baseColor;
    }

    public void translate( ImmutableVector2D translationVector ) {
        AffineTransform translationTransform = AffineTransform.getTranslateInstance( translationVector.getX(), translationVector.getY() );
        shapeProperty.set( translationTransform.createTransformedShape( shapeProperty.get() ) );
        System.out.println( this + " Translated, now at : " + getShape().getBounds2D().getCenterX() + ", " + getShape().getBounds2D().getCenterY() );
    }

    public void setPosition( Point2D newPos ) {
        // This default implementation assumes that the position indicator is
        // defined by the center of the shape's bounds.  Override if some
        // other behavior is required.
        translate( new Vector2D( newPos.getX() - shapeProperty.get().getBounds2D().getCenterX(),
                                 newPos.getY() - shapeProperty.get().getBounds2D().getCenterY() ) );
//        System.out.println(this + "Position set to: " + newPos );
    }

    public Color getBaseColor() {
        return baseColor;
    }

    public void release() {
        userControlled.set( false );
    }
}
