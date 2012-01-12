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
        if ( boundsShape == null ) {
            // No bounds means everything is in bounds.
            return true;
        }
        else {
            return boundsShape.contains( p );
        }
    }

    public boolean inBounds( Shape s ) {
        if ( boundsShape == null ) {
            // No bounds means everything is in bounds.
            return true;
        }
        else {
            return boundsShape.contains( s.getBounds2D() );
        }
    }

    public Shape getBounds() {
        return new Area( boundsShape );
    }

    public void setBounds( Shape newBounds ) {
        boundsShape = new Area( newBounds );
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
    public boolean testMotionAgainstBounds( Shape shape, ImmutableVector2D motionVector, double dt ) {
        AffineTransform motionTransform = AffineTransform.getTranslateInstance( motionVector.getX() * dt, motionVector.getY() * dt );
        return inBounds( motionTransform.createTransformedShape( shape ) );
    }

    /**
     * Test whether the given point will be in or out of the motion bounds if
     * the given motion vector is applied for the given time.
     *
     * @param currentLocation - Current location of entity being tested.
     * @param motionVector    - Motion vector of the object in distance/sec
     * @param dt              - delta time, i.e. amount of time, in seconds.
     * @return
     */
    public boolean testMotionAgainstBounds( Point2D currentLocation, ImmutableVector2D motionVector, double dt ) {
        Point2D newLocation = new Point2D.Double( currentLocation.getX() + motionVector.getX() * dt, currentLocation.getY() + motionVector.getY() * dt );
        return inBounds( newLocation );
    }
}
