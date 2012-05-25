// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Motion strategy where the controlled entity moves to the back of the Z
 * dimension and then moves instantly to a new location.  This was created to
 * use when a polymerase molecule needs to return to the beginning of the
 * transcribed area of a gene when it complete transcription, though it may
 * end up having other applications.
 *
 * @author John Blanco
 */
public class DriftThenTeleportMotionStrategy extends MotionStrategy {

    private static final double PRE_TELEPORT_TIME = 2; // In seconds.
    private static final double PRE_TELEPORT_VELOCITY = 100; // In picometers per second.
    private static final Random RAND = new Random();

    private final ImmutableVector2D wanderDirection;

    // List of valid places where the item can teleport.
    private final List<Rectangle2D> destinationZones;

    private double countdown = PRE_TELEPORT_TIME;
    private final ImmutableVector2D velocityXY;
    private double velocityZ = 0;

    public DriftThenTeleportMotionStrategy( ImmutableVector2D wanderDirection, List<Rectangle2D> destinationZones, Property<MotionBounds> motionBoundsProperty ) {
        this.destinationZones = destinationZones;
        this.wanderDirection = wanderDirection;
        motionBoundsProperty.addObserver( new VoidFunction1<MotionBounds>() {
            public void apply( MotionBounds motionBounds ) {
                DriftThenTeleportMotionStrategy.this.motionBounds = motionBounds;
            }
        } );
        velocityXY = wanderDirection.getScaledInstance( PRE_TELEPORT_VELOCITY );
        velocityZ = -1 / PRE_TELEPORT_TIME;
    }

    @Override public Point2D getNextLocation( Point2D currentLocation, Shape shape, double dt ) {
        Point3D location3D = getNextLocation3D( new Point3D.Double( currentLocation.getX(), currentLocation.getY(), 0 ), shape, dt );
        return new Point2D.Double( location3D.getX(), location3D.getY() );
    }

    @Override public Point3D getNextLocation3D( Point3D currentLocation, Shape shape, double dt ) {
        countdown -= dt;
        if ( countdown <= 0 && !pointContainedInBoundsList( new Point2D.Double( currentLocation.getX(), currentLocation.getY() ), destinationZones ) ) {
            // Time to teleport.  Should be at back of Z space when this occurs.
            Point2D destination2D = generateRandomLocationInBounds( destinationZones, shape );
            return new Point3D.Double( destination2D.getX(), destination2D.getY(), -1 );
        }
        else {
            // Move in the wander direction, if it doesn't take us out of bounds.
            if ( motionBounds.testIfInMotionBounds( shape, velocityXY, dt ) ) {
                return new Point3D.Double( currentLocation.getX() + velocityXY.getX() * dt,
                                           currentLocation.getY() + velocityXY.getY() * dt,
                                           MathUtil.clamp( -1, currentLocation.getZ() + velocityZ * dt, 0 ) );
            }
            else {
                return currentLocation;
            }
        }
    }

    private boolean pointContainedInBoundsList( Point2D p, List<Rectangle2D> boundsList ) {
        for ( Rectangle2D bounds : boundsList ) {
            if ( bounds.contains( p ) ) {
                return true;
            }
        }
        return false;
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
