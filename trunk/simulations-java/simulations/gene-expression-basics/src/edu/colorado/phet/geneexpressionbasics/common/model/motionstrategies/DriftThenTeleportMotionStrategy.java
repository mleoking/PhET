// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Motion strategy where the controlled entity drifts at the front of a Z
 * dimension, then moves to the back of Z space, then moves instantly to a
 * new randomly generated location within a set of possible "destination
 * zones" (hench the "teleport" portion of the name). This was created to use
 * when a polymerase molecule needs to return to the beginning of the
 * transcribed area of a gene when it complete transcription. It may, at some
 * point, have other applications as well.
 *
 * @author John Blanco
 */
public class DriftThenTeleportMotionStrategy extends MotionStrategy {

    private static final double PRE_FADE_DRIFT_TIME = 1.5; // In seconds.
    private static final double FADE_AND_DRIFT_TIME = 1; // In seconds.
    private static final double PRE_TELEPORT_VELOCITY = 250; // In picometers per second.
    private static final Random RAND = new Random();

    // List of valid places where the item can teleport.
    private final List<Rectangle2D> destinationZones;

    private double preFadeCountdown = PRE_FADE_DRIFT_TIME;
    private final Vector2D velocityXY;
    private double velocityZ = 0;

    public DriftThenTeleportMotionStrategy( Vector2D wanderDirection, List<Rectangle2D> destinationZones, Property<MotionBounds> motionBoundsProperty ) {
        this.destinationZones = destinationZones;
        motionBoundsProperty.addObserver( new VoidFunction1<MotionBounds>() {
            public void apply( MotionBounds motionBounds ) {
                DriftThenTeleportMotionStrategy.this.motionBounds = motionBounds;
            }
        } );
        velocityXY = wanderDirection.times( PRE_TELEPORT_VELOCITY );
        velocityZ = -1 / FADE_AND_DRIFT_TIME;
    }

    @Override public Point2D getNextLocation( Point2D currentLocation, Shape shape, double dt ) {
        Point3D location3D = getNextLocation3D( new Point3D.Double( currentLocation.getX(), currentLocation.getY(), 0 ), shape, dt );
        return new Point2D.Double( location3D.getX(), location3D.getY() );
    }

    @Override public Point3D getNextLocation3D( Point3D currentLocation, Shape shape, double dt ) {

        // Check if it is time to teleport.  This occurs when back of Z-space is reached.
        if ( currentLocation.getZ() <= -1 ) {
            // Time to teleport.
            Point2D destination2D = generateRandomLocationInBounds( destinationZones, shape );
            return new Point3D.Double( destination2D.getX(), destination2D.getY(), -1 );
        }

        // Determine movement for drift.
        final Vector2D xyMovement;
        if ( motionBounds.testIfInMotionBounds( shape, velocityXY, dt ) ) {
            xyMovement = velocityXY.times( dt );
        }
        else {
            xyMovement = new Vector2D( 0, 0 );
        }
        double zMovement = 0;
        if ( preFadeCountdown > 0 ) {
            // In pre-fade state, so no movement in Z direction.
            preFadeCountdown -= dt;
        }
        else {
            // In fade-out state.
            zMovement = velocityZ * dt;
        }

        return new Point3D.Double( currentLocation.getX() + xyMovement.getX(),
                                   currentLocation.getY() + xyMovement.getY(),
                                   MathUtil.clamp( -1, currentLocation.getZ() + zMovement, 0 ) );
    }

    private Point2D generateRandomLocationInBounds( List<Rectangle2D> destinationZones, Shape shape ) {

        // Randomly choose one of the destination zones.
        Rectangle2D destinationBounds = destinationZones.get( RAND.nextInt( destinationZones.size() ) );

        // Generate a random valid location within the chosen zone.
        double reducedBoundsWidth = destinationBounds.getWidth() - shape.getBounds2D().getWidth();
        double reducedBoundsHeight = destinationBounds.getHeight() - shape.getBounds2D().getHeight();
        if ( reducedBoundsWidth <= 0 || reducedBoundsHeight <= 0 ) {
            System.out.println( getClass().getName() + " - Warning: Bounds cannot contain shape." );
            return new Point2D.Double( destinationBounds.getCenterX(), destinationBounds.getCenterY() );
        }
        else {
            return new Point2D.Double( destinationBounds.getX() + shape.getBounds2D().getWidth() / 2 + RAND.nextDouble() * reducedBoundsWidth,
                                       destinationBounds.getY() + shape.getBounds2D().getHeight() / 2 + RAND.nextDouble() * reducedBoundsHeight );
        }
    }
}
