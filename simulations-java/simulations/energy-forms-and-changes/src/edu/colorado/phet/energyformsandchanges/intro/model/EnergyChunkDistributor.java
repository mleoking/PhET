// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;

/**
 * A class that contains static methods for redistributing a set of energy
 * chunks.
 *
 * @author John Blanco
 */
public class EnergyChunkDistributor {

    private static final double OUTSIDE_CONTAINER_FORCE = 1.5; // In Newtons, empirically determined.
    private static final double MAX_TIME_STEP = 10E-3; // In seconds, for algorithm that moves the points.
    private static final Random RAND = new Random();

    public static void updatePositions( List<EnergyChunk> energyChunkList, Rectangle2D enclosingRect, double dt ) {

        // Create a map that relates each energy chunk to a point mass.
        Map<EnergyChunk, PointMass> map = new HashMap<EnergyChunk, PointMass>();
        for ( EnergyChunk energyChunk : energyChunkList ) {
            map.put( energyChunk, new PointMass( energyChunk.position.get(), enclosingRect ) );
        }

        // Determine the force constant to use for the repulsive algorithm that
        // positions the energy chunks.  Formula was made up and has some
        // tweak factors, so it may require some adjustments.
        double forceConstant = ( enclosingRect.getWidth() * enclosingRect.getHeight() / energyChunkList.size() ) * 0.5;

        int numSteps = (int) ( dt / MAX_TIME_STEP );
        double extraTime = dt - numSteps * MAX_TIME_STEP;

        for ( int i = 0; i <= numSteps; i++ ) {
            double timeStep = i < numSteps ? MAX_TIME_STEP : extraTime;
            // Update the forces acting on each point mass.
            for ( PointMass p : map.values() ) {
                if ( enclosingRect.contains( p.position.toPoint2D() ) ) {

                    // Force from left side of rectangle.
                    p.applyForce( new ImmutableVector2D( forceConstant / Math.pow( p.position.getX() - enclosingRect.getX(), 2 ), 0 ) );

                    // Force from right side of rectangle.
                    p.applyForce( new ImmutableVector2D( -forceConstant / Math.pow( enclosingRect.getMaxX() - p.position.getX(), 2 ), 0 ) );

                    // Force from bottom of rectangle.
                    p.applyForce( new ImmutableVector2D( 0, forceConstant / Math.pow( p.position.getY() - enclosingRect.getY(), 2 ) ) );

                    // Force from top of rectangle.
                    p.applyForce( new ImmutableVector2D( 0, -forceConstant / Math.pow( enclosingRect.getMaxY() - p.position.getY(), 2 ) ) );

                    // Apply the force from each of the other particles.
                    double minDistance = Math.min( enclosingRect.getWidth(), enclosingRect.getHeight() ) / 100; // Divisor empirically determined.
                    for ( PointMass otherP : map.values() ) {
                        if ( p != otherP ) {
                            // Calculate force vector, but handle cases where too close.
                            ImmutableVector2D vectorToOther = p.position.getSubtractedInstance( otherP.position );
                            if ( vectorToOther.getMagnitude() < minDistance ) {
                                if ( vectorToOther.getMagnitude() == 0 ) {
                                    // Create a random vector of min distance.
                                    System.out.println( "Creating random vector" );
                                    double angle = RAND.nextDouble() * Math.PI * 2;
                                    vectorToOther = new ImmutableVector2D( minDistance * Math.cos( angle ), minDistance * Math.sin( angle ) );
                                }
                                else {
                                    vectorToOther = vectorToOther.getInstanceOfMagnitude( minDistance );
                                }
                            }
                            p.applyForce( vectorToOther.getInstanceOfMagnitude( forceConstant / ( vectorToOther.getMagnitudeSq() ) ) );
                        }
                    }
                }
                else {
                    // Point is outside container, move it towards center of rectangle.
                    ImmutableVector2D vectorToCenter = new ImmutableVector2D( enclosingRect.getCenterX(), enclosingRect.getCenterY() ).getSubtractedInstance( p.position );
                    p.applyForce( vectorToCenter.getInstanceOfMagnitude( OUTSIDE_CONTAINER_FORCE ) );
                }
            }

            // Update the positions of the point masses and the corresponding
            // energy chunks.
            for ( PointMass p : map.values() ) {
                // Update the position of the point.
                p.updatePosition( timeStep );
                p.clearAcceleration();
            }
        }

        // Update the positions of the energy chunks.
        for ( EnergyChunk energyChunk : energyChunkList ) {
            energyChunk.position.set( map.get( energyChunk ).position );
        }
    }

    public static void updatePositions( List<EnergyChunk> energyChunkList, Shape enclosingShape, double dt ) {

        Rectangle2D boundingRect = enclosingShape.getBounds2D();

        // Create a map that relates each energy chunk to a point mass.
        Map<EnergyChunk, PointMass> map = new HashMap<EnergyChunk, PointMass>();
        for ( EnergyChunk energyChunk : energyChunkList ) {
            map.put( energyChunk, new PointMass( energyChunk.position.get(), enclosingShape ) );
        }

        // Determine the force constant to use for the repulsive algorithm that
        // positions the energy chunks.  Formula was made up and has some
        // tweak factors, so it may require some adjustments.
        double forceConstant = ( boundingRect.getWidth() * boundingRect.getHeight() / energyChunkList.size() ) * 0.5;

        // Determine the minimum distance that is allowed to be used in the
        // force calculations.  This prevents hitting infinities that can
        // cause run time issues or unreasonably large forces.
        double minDistance = Math.min( boundingRect.getWidth(), boundingRect.getHeight() ) / 100; // Divisor empirically determined.

        // Loop, calculating and applying the forces for each point mass.
        int numForceCalcSteps = (int) ( dt / MAX_TIME_STEP );
        double extraTime = dt - numForceCalcSteps * MAX_TIME_STEP;
        for ( int forceCalcStep = 0; forceCalcStep <= numForceCalcSteps; forceCalcStep++ ) {

            double timeStep = forceCalcStep < numForceCalcSteps ? MAX_TIME_STEP : extraTime;

            // Update the forces acting on each point mass.
            for ( PointMass p : map.values() ) {
                if ( enclosingShape.contains( p.position.toPoint2D() ) ) {

                    // Calculate the forces from the edges of the container.
                    for ( double angle = 0; angle < 2 * Math.PI; angle += Math.PI / 2 ) {
                        int edgeDetectSteps = 8;
                        DoubleRange lengthBounds = new DoubleRange( 0, Math.sqrt( boundingRect.getWidth() * boundingRect.getWidth() + boundingRect.getHeight() * boundingRect.getHeight() ) );
                        for ( int edgeDetectStep = 0; edgeDetectStep < edgeDetectSteps; edgeDetectStep++ ) {
                            ImmutableVector2D vectorToEdge = new ImmutableVector2D( lengthBounds.getCenter(), 0 ).getRotatedInstance( angle );
                            if ( enclosingShape.contains( p.position.getAddedInstance( vectorToEdge ).toPoint2D() ) ) {
                                lengthBounds = new DoubleRange( lengthBounds.getCenter(), lengthBounds.getMax() );
                            }
                            else {
                                lengthBounds = new DoubleRange( lengthBounds.getMin(), lengthBounds.getCenter() );
                            }
                        }

                        // Handle case where point is too close to the container's edge.
                        if ( lengthBounds.getCenter() < minDistance ) {
                            System.out.println( "Warning: point is on container edge." );
                            lengthBounds = new DoubleRange( minDistance, minDistance );
                        }

                        // Apply the force due to this edge.
                        ImmutableVector2D forceVector = new ImmutableVector2D( forceConstant / Math.pow( lengthBounds.getCenter(), 2 ), 0 ).getRotatedInstance( angle + Math.PI );
                        p.applyForce( forceVector );
                    }

                    // Apply the force from each of the other particles, but
                    // set some limits on the max force that can be applied.
                    for ( PointMass otherP : map.values() ) {
                        if ( p != otherP ) {
                            // Calculate force vector, but handle cases where too close.
                            ImmutableVector2D vectorToOther = p.position.getSubtractedInstance( otherP.position );
                            if ( vectorToOther.getMagnitude() < minDistance ) {
                                if ( vectorToOther.getMagnitude() == 0 ) {
                                    // Create a random vector of min distance.
                                    System.out.println( "Creating random vector" );
                                    double angle = RAND.nextDouble() * Math.PI * 2;
                                    vectorToOther = new ImmutableVector2D( minDistance * Math.cos( angle ), minDistance * Math.sin( angle ) );
                                }
                                else {
                                    vectorToOther = vectorToOther.getInstanceOfMagnitude( minDistance );
                                }
                            }
                            p.applyForce( vectorToOther.getInstanceOfMagnitude( forceConstant / ( vectorToOther.getMagnitudeSq() ) ) );
                        }
                    }
                }
                else {
                    // Point is outside container, move it towards center of shape.
                    ImmutableVector2D vectorToCenter = new ImmutableVector2D( boundingRect.getCenterX(), boundingRect.getCenterY() ).getSubtractedInstance( p.position );
                    p.applyForce( vectorToCenter.getInstanceOfMagnitude( OUTSIDE_CONTAINER_FORCE ) );
                }
            }

            // Update the positions of the point masses and the corresponding
            // energy chunks.
            for ( PointMass p : map.values() ) {
                // Update the position of the point.
                p.updatePosition( timeStep );
                p.clearAcceleration();
            }
        }

        // Update the positions of the energy chunks.
        for ( EnergyChunk energyChunk : energyChunkList ) {
            energyChunk.position.set( map.get( energyChunk ).position );
        }
    }

    public static ImmutableVector2D generateRandomLocation( Rectangle2D rect ) {
        return new ImmutableVector2D( rect.getMinX() + ( RAND.nextDouble() * rect.getWidth() ), rect.getMinY() + ( RAND.nextDouble() * rect.getHeight() ) );
    }

    private static class PointMass {
        private static final double MASS = 1; // In kg.
        private Vector2D position = new Vector2D();
        private Vector2D velocity = new Vector2D( 0, 0 );
        private Vector2D acceleration = new Vector2D( 0, 0 );
        private final Shape containerShape;

        public PointMass( ImmutableVector2D initialPosition, Shape container ) {
            this.containerShape = container;
            position.setValue( initialPosition );
        }

        public void applyForce( ImmutableVector2D force ) {
            acceleration.add( force.getScaledInstance( force.getMagnitude() / MASS ) );
        }

        public void clearAcceleration() {
            acceleration.setComponents( 0, 0 );
        }

        public void updatePosition( double dt ) {

            // Update the velocity based on previous velocity and current acceleration.
            velocity.add( acceleration.getScaledInstance( dt ) );

            if ( containerShape.contains( position.toPoint2D() ) ) {

                // Limit the velocity.  This acts much like a drag force that
                // gets stronger as the velocity gets bigger.
                double maxVelocity = Math.min( containerShape.getBounds2D().getWidth(), containerShape.getBounds2D().getHeight() ) / 10 / dt;
                velocity.setMagnitude( maxVelocity * velocity.getMagnitude() / ( velocity.getMagnitude() + maxVelocity ) );

                // Check that the velocity won't move the point outside of the container.
                if ( containerShape.contains( position.toPoint2D() ) && !containerShape.contains( position.getAddedInstance( velocity.getScaledInstance( dt ) ).toPoint2D() ) ) {
                    velocity.setMagnitude( 0 );
                }
            }

            // Update the position.
            position.add( velocity.getScaledInstance( dt ) );
        }
    }

    // Test harness.
    public static void main( String[] args ) {
        Shape enclosingShape = new Rectangle2D.Double( -0.1, -0.1, 0.2, 0.2 );
        List<EnergyChunk> energyChunkList = new ArrayList<EnergyChunk>();
        energyChunkList.add( new EnergyChunk( new ConstantDtClock( 30 ), 0.05, 0, new BooleanProperty( true ), false ) );
        for ( int i = 0; i < 100; i++ ) {
            EnergyChunkDistributor.updatePositions( energyChunkList, enclosingShape, 0.033 );
            System.out.println( " Pos: " + energyChunkList.get( 0 ).position );
        }
    }
}

