// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * Class that defines the bounds within which some shape or point is allowed to
 * move.  The shape can be anything, and does not need to be rectangular.
 * <p/>
 * If the bounds are not set, they are assumed to be infinite.
 *
 * @author John Blanco
 */
public class MotionBounds {

    // Use a shape, rather than a rectangle, for the bounds.  This allows
    // more complex bounds to be used.
    private Shape boundsShape = null;

    public MotionBounds() {
        // Default constructor does nothing, leaves bounds infinite.
    }

    public MotionBounds( Shape boundsShape ) {
        this.boundsShape = boundsShape;
    }

    public boolean inBounds( Point2D p ) {
        return boundsShape == null || boundsShape.contains( p );
    }

    public boolean inBounds( Shape s ) {
        return boundsShape == null || boundsShape.contains( s.getBounds2D() );
    }

    public Shape getBounds() {
        return new Area( boundsShape );
    }

    /**
     * Test whether the given shape will be in or out of the motion bounds if
     * the given motion vector is applied for the given time.
     *
     * @param shape        - Shape of entity being tested.
     * @param motionVector - Motion vector of the object in distance/sec
     * @param dt           - delta time, i.e. amount of time, in seconds.
     * @return
     */
    public boolean testIfInMotionBounds( Shape shape, ImmutableVector2D motionVector, double dt ) {
        AffineTransform motionTransform = AffineTransform.getTranslateInstance( motionVector.getX() * dt, motionVector.getY() * dt );
        return inBounds( motionTransform.createTransformedShape( shape ) );
    }

    /**
     * Test whether the given shape will be within the motion bounds if it is
     * translated such that its center is at the given point.
     *
     * @param shape            - Test shape.
     * @param proposedLocation - Proposed location of the shape's center.
     * @return - True is in bounds, false if not.
     */
    public boolean testIfInMotionBounds( Shape shape, Point2D proposedLocation ) {
        ImmutableVector2D shapeCenter = new ImmutableVector2D( shape.getBounds2D().getCenterX(), shape.getBounds2D().getCenterY() );
        ImmutableVector2D translationVector = new ImmutableVector2D( proposedLocation ).getSubtractedInstance( shapeCenter );
        Shape translatedBounds = AffineTransform.getTranslateInstance( translationVector.getX(), translationVector.getY() ).createTransformedShape( shape );
        return inBounds( translatedBounds );
    }
}
