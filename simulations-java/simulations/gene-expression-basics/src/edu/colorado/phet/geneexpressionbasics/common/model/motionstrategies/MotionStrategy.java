// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * Base class for motion strategies that can be used to exhibit different sorts
 * of motion.  This class and its subclasses have been written to be very
 * general in order to enable reuse.
 *
 * @author John Blanco
 */
public abstract class MotionStrategy {

    protected MotionBounds motionBounds = new MotionBounds();

    /**
     * Get the next location based on the current motion.  If the application of
     * the current motion causes any part of the provided shape to go outside of
     * the motion bounds, a "bounce" will occur.
     *
     * @param currentLocation
     * @param shape           - Shape of the controlled item, used in detecting whether
     *                        the item would go outside of the motion bounds.
     * @param dt
     * @return
     */
    public abstract Point2D getNextLocation( Point2D currentLocation, Shape shape, double dt );

    /**
     * Set a new value for the motion bounds.
     *
     * @param motionBounds
     */
    public void setMotionBounds( MotionBounds motionBounds ) {
        this.motionBounds = motionBounds;
    }

    /**
     * This utility method will return a motion vector that reflects a "bounce"
     * in the x, y, or both directions based on which type of bounce will yield
     * a next location that is within the motion bounds.  If no such vector can
     * be found, a vector to the center of the motion bounds is returned.
     */
    private ImmutableVector2D getMotionVectorForBounce( Point2D currentLocation, ImmutableVector2D originalMotionVector, double dt, double maxVelocity ) {
        // Check that this isn't being called inappropriately.
        assert ( !motionBounds.inBounds( new Point2D.Double( currentLocation.getX() + originalMotionVector.getX() * dt,
                                                             currentLocation.getY() + originalMotionVector.getY() * dt ) ) );

        ImmutableVector2D reversedXMotionVector = new Vector2D( -originalMotionVector.getX(), originalMotionVector.getY() );
        if ( motionBounds.inBounds( new Point2D.Double( currentLocation.getX() + reversedXMotionVector.getX() * dt,
                                                        currentLocation.getY() + reversedXMotionVector.getY() * dt ) ) ) {
            return reversedXMotionVector;
        }
        ImmutableVector2D reversedYMotionVector = new Vector2D( originalMotionVector.getX(), -originalMotionVector.getY() );
        if ( motionBounds.inBounds( new Point2D.Double( currentLocation.getX() + reversedYMotionVector.getX() * dt,
                                                        currentLocation.getY() + reversedYMotionVector.getY() * dt ) ) ) {
            return reversedYMotionVector;
        }
        ImmutableVector2D reversedXYMotionVector = new Vector2D( -originalMotionVector.getX(), -originalMotionVector.getY() );
        if ( motionBounds.inBounds( new Point2D.Double( currentLocation.getX() + reversedXYMotionVector.getX() * dt,
                                                        currentLocation.getY() + reversedXYMotionVector.getY() * dt ) ) ) {
            return reversedXYMotionVector;
        }
        // If we reach this point, there is a problem.  Reversing the vector
        // in all the possible ways doesn't get us back to a valid location.
        // This might be because the user dropped the molecule in an invalid
        // location.  Set the motion to be directly back to the center of the
        // motion bounds for now.
        System.out.println( "Debug Warning: Unable to find vector to bounce back into motion bounds." );
        Point2D centerOfMotionBounds = new Point2D.Double( motionBounds.getBounds().getBounds2D().getCenterX(),
                                                           motionBounds.getBounds().getBounds2D().getCenterY() );
        Vector2D vectorToMotionBoundsCenter = new Vector2D( centerOfMotionBounds.getX() - currentLocation.getX(),
                                                            centerOfMotionBounds.getY() - currentLocation.getY() );
        vectorToMotionBoundsCenter.scale( maxVelocity / vectorToMotionBoundsCenter.getMagnitude() );
        return new ImmutableVector2D( vectorToMotionBoundsCenter );
    }

    /**
     * This utility method will return a motion vector that reflects a "bounce"
     * in the x, y, or both directions based on which type of bounce will yield
     * a next location for the shape that is within the motion bounds.  If no
     * such vector can be found, a vector to the center of the motion bounds is
     * returned.
     */
    protected ImmutableVector2D getMotionVectorForBounce( Shape shape, ImmutableVector2D originalMotionVector, double dt, double maxVelocity ) {
        // Check that this isn't being called inappropriately.
        AffineTransform currentMotionTransform = getMotionTransform( originalMotionVector, dt );
        assert ( !motionBounds.inBounds( currentMotionTransform.createTransformedShape( shape ) ) );

        // Try reversing X direction.
        ImmutableVector2D reversedXMotionVector = new Vector2D( -originalMotionVector.getX(), originalMotionVector.getY() );
        AffineTransform reverseXMotionTransform = getMotionTransform( reversedXMotionVector, dt );
        if ( motionBounds.inBounds( reverseXMotionTransform.createTransformedShape( shape ) ) ) {
            return reversedXMotionVector;
        }
        // Try reversing Y direction.
        ImmutableVector2D reversedYMotionVector = new Vector2D( originalMotionVector.getX(), -originalMotionVector.getY() );
        AffineTransform reverseYMotionTransform = getMotionTransform( reversedYMotionVector, dt );
        if ( motionBounds.inBounds( reverseYMotionTransform.createTransformedShape( shape ) ) ) {
            return reversedYMotionVector;
        }
        // Try reversing both X and Y directions.
        ImmutableVector2D reversedXYMotionVector = new Vector2D( -originalMotionVector.getX(), -originalMotionVector.getY() );
        AffineTransform reverseXYMotionTransform = getMotionTransform( reversedXYMotionVector, dt );
        if ( motionBounds.inBounds( reverseXYMotionTransform.createTransformedShape( shape ) ) ) {
            return reversedXYMotionVector;
        }
        // If we reach this point, there is a problem.  Reversing the vector
        // in all the possible ways doesn't get us back to a valid location.
        // This might be because the user dropped the molecule in an invalid
        // location.  Set the motion to be directly back to the center of the
        // motion bounds for now.
        System.out.println( "Debug Warning: Biomolecule is unable to bounce back into motion bounds." );
        Point2D centerOfMotionBounds = new Point2D.Double( motionBounds.getBounds().getBounds2D().getCenterX(),
                                                           motionBounds.getBounds().getBounds2D().getCenterY() );
        Vector2D vectorToMotionBoundsCenter = new Vector2D( centerOfMotionBounds.getX() - shape.getBounds2D().getCenterX(),
                                                            centerOfMotionBounds.getY() - shape.getBounds2D().getCenterY() );
        vectorToMotionBoundsCenter.scale( maxVelocity / vectorToMotionBoundsCenter.getMagnitude() );
        return new ImmutableVector2D( vectorToMotionBoundsCenter );
    }

    private AffineTransform getMotionTransform( ImmutableVector2D motionVector, double dt ) {
        return AffineTransform.getTranslateInstance( motionVector.getX() * dt, motionVector.getY() * dt );
    }

    protected static ImmutableVector2D getVectorTowardsDestination( Point2D currentLocation, Point2D destination, double velocity ) {
        return new ImmutableVector2D( currentLocation, destination ).getInstanceOfMagnitude( velocity );
    }
}
