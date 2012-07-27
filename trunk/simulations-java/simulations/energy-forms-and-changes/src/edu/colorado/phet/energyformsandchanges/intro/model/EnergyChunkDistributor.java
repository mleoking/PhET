// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
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

    private static final double OUTSIDE_CONTAINER_FORCE = 2.5; // In Newtons, empirically determined.
    private static final double MAX_TIME_STEP = 5E-3; // In seconds, for algorithm that moves the points.
    private static final Random RAND = new Random( 2 ); // Seeded for greater consistency.

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
                    p.applyForce( new Vector2D( forceConstant / Math.pow( p.position.getX() - enclosingRect.getX(), 2 ), 0 ) );

                    // Force from right side of rectangle.
                    p.applyForce( new Vector2D( -forceConstant / Math.pow( enclosingRect.getMaxX() - p.position.getX(), 2 ), 0 ) );

                    // Force from bottom of rectangle.
                    p.applyForce( new Vector2D( 0, forceConstant / Math.pow( p.position.getY() - enclosingRect.getY(), 2 ) ) );

                    // Force from top of rectangle.
                    p.applyForce( new Vector2D( 0, -forceConstant / Math.pow( enclosingRect.getMaxY() - p.position.getY(), 2 ) ) );

                    // Apply the force from each of the other particles.
                    double minDistance = Math.min( enclosingRect.getWidth(), enclosingRect.getHeight() ) / 100; // Divisor empirically determined.
                    for ( PointMass otherP : map.values() ) {
                        if ( p != otherP ) {
                            // Calculate force vector, but handle cases where too close.
                            Vector2D vectorToOther = p.position.minus( otherP.position );
                            if ( vectorToOther.magnitude() < minDistance ) {
                                if ( vectorToOther.magnitude() == 0 ) {
                                    // Create a random vector of min distance.
                                    System.out.println( "Creating random vector" );
                                    double angle = RAND.nextDouble() * Math.PI * 2;
                                    vectorToOther = new Vector2D( minDistance * Math.cos( angle ), minDistance * Math.sin( angle ) );
                                }
                                else {
                                    vectorToOther = vectorToOther.getInstanceOfMagnitude( minDistance );
                                }
                            }
                            p.applyForce( vectorToOther.getInstanceOfMagnitude( forceConstant / ( vectorToOther.magnitudeSquared() ) ) );
                        }
                    }
                }
                else {
                    // Point is outside container, move it towards center of rectangle.
                    Vector2D vectorToCenter = new Vector2D( enclosingRect.getCenterX(), enclosingRect.getCenterY() ).minus( p.position );
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
            energyChunk.position.set( new Vector2D( map.get( energyChunk ).position ) );
        }
    }

    // TODO: Potentially obsoleted on 6/23/2012, remove at some point if never needed again.
    private static void updatePositions( List<EnergyChunk> energyChunkList, Shape enclosingShape, double dt ) {

        Rectangle2D boundingRect = enclosingShape.getBounds2D();

        // Create a map that relates each energy chunk to a point mass.
        Map<EnergyChunk, PointMass> map = new HashMap<EnergyChunk, PointMass>();
        for ( EnergyChunk energyChunk : energyChunkList ) {
            map.put( energyChunk, new PointMass( energyChunk.position.get(), enclosingShape ) );
        }

        // Determine the force constant to use for the repulsive algorithm that
        // positions the energy chunks.  Formula was made up and has some
        // tweak factors, so it may require some adjustments.
        double particleForceConstant = ( boundingRect.getWidth() * boundingRect.getHeight() / energyChunkList.size() ) * 0.5;

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
                            Vector2D vectorToEdge = new Vector2D( lengthBounds.getCenter(), 0 ).getRotatedInstance( angle );
                            if ( enclosingShape.contains( p.position.plus( vectorToEdge ).toPoint2D() ) ) {
                                lengthBounds = new DoubleRange( lengthBounds.getCenter(), lengthBounds.getMax() );
                            }
                            else {
                                lengthBounds = new DoubleRange( lengthBounds.getMin(), lengthBounds.getCenter() );
                            }
                        }

                        // Handle case where point is too close to the container's edge.
                        if ( lengthBounds.getCenter() < minDistance ) {
                            lengthBounds = new DoubleRange( minDistance, minDistance );
                        }

                        // Apply the force due to this edge.
                        Vector2D forceVector = new Vector2D( particleForceConstant / Math.pow( lengthBounds.getCenter(), 2 ), 0 ).getRotatedInstance( angle + Math.PI );
                        p.applyForce( forceVector );
                    }

                    // Apply the force from each of the other particles, but
                    // set some limits on the max force that can be applied.
                    for ( PointMass otherP : map.values() ) {
                        if ( p != otherP ) {
                            // Calculate force vector, but handle cases where too close.
                            Vector2D vectorToOther = p.position.minus( otherP.position );
                            if ( vectorToOther.magnitude() < minDistance ) {
                                if ( vectorToOther.magnitude() == 0 ) {
                                    // Create a random vector of min distance.
                                    System.out.println( "Creating random vector" );
                                    double angle = RAND.nextDouble() * Math.PI * 2;
                                    vectorToOther = new Vector2D( minDistance * Math.cos( angle ), minDistance * Math.sin( angle ) );
                                }
                                else {
                                    vectorToOther = vectorToOther.getInstanceOfMagnitude( minDistance );
                                }
                            }
                            p.applyForce( vectorToOther.getInstanceOfMagnitude( particleForceConstant / ( vectorToOther.magnitudeSquared() ) ) );
                        }
                    }
                }
                else {
                    // Point is outside container, move it towards center of shape.
                    Vector2D vectorToCenter = new Vector2D( boundingRect.getCenterX(), boundingRect.getCenterY() ).minus( p.position );
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
            energyChunk.position.set( new Vector2D( map.get( energyChunk ).position ) );
        }
    }

    public static void updatePositions( List<EnergyChunkContainerSlice> energyChunkContainerSlices, double dt ) {

        // Create a map that relates each energy chunk to a point mass.
        Map<EnergyChunk, PointMass> map = new HashMap<EnergyChunk, PointMass>();
        for ( EnergyChunkContainerSlice slice : energyChunkContainerSlices ) {
            for ( EnergyChunk energyChunk : slice.energyChunkList ) {
                map.put( energyChunk, new PointMass( energyChunk.position.get(), slice.getShape() ) );
            }
        }

        // Determine a rectangle that bounds all of the slices.
        Rectangle2D boundingRect;
        {
            double minX = Double.POSITIVE_INFINITY;
            double minY = Double.POSITIVE_INFINITY;
            double maxX = Double.NEGATIVE_INFINITY;
            double maxY = Double.NEGATIVE_INFINITY;
            for ( EnergyChunkContainerSlice slice : energyChunkContainerSlices ) {
                minX = Math.min( slice.getShape().getBounds2D().getMinX(), minX );
                maxX = Math.max( slice.getShape().getBounds2D().getMaxX(), maxX );
                minY = Math.min( slice.getShape().getBounds2D().getMinY(), minY );
                maxY = Math.max( slice.getShape().getBounds2D().getMaxY(), maxY );
            }
            boundingRect = new Rectangle2D.Double( minX, minY, maxX - minX, maxY - minY );
        }

        // Determine the force constants to use for the repulsive algorithm
        // that positions the energy chunks.  Formula was made up and has some
        // tweak factors, so it may require some adjustments.
        double particleForceConstant = boundingRect.getWidth() * boundingRect.getHeight() / map.size() * 2;
        double edgeForceConstant = particleForceConstant / 2;

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
                if ( p.getContainerShape().contains( p.position.toPoint2D() ) ) {

                    // Determine the max possible distance to an edge.
                    double maxDistanceToEdge = Math.sqrt( Math.pow( p.getContainerShape().getBounds2D().getWidth(), 2 ) +
                                                          Math.pow( p.getContainerShape().getBounds2D().getHeight(), 2 ) );

                    // Loop on several angles, calculating the forces from the
                    // edges at the given angle.
                    for ( double angle = 0; angle < 2 * Math.PI; angle += Math.PI / 2 ) {
                        int edgeDetectSteps = 8;
                        DoubleRange lengthBounds = new DoubleRange( 0, maxDistanceToEdge );
                        for ( int edgeDetectStep = 0; edgeDetectStep < edgeDetectSteps; edgeDetectStep++ ) {
                            Vector2D vectorToEdge = new Vector2D( lengthBounds.getCenter(), 0 ).getRotatedInstance( angle );
                            if ( p.getContainerShape().contains( p.position.plus( vectorToEdge ).toPoint2D() ) ) {
                                lengthBounds = new DoubleRange( lengthBounds.getCenter(), lengthBounds.getMax() );
                            }
                            else {
                                lengthBounds = new DoubleRange( lengthBounds.getMin(), lengthBounds.getCenter() );
                            }
                        }

                        // Handle case where point is too close to the container's edge.
                        if ( lengthBounds.getCenter() < minDistance ) {
                            lengthBounds = new DoubleRange( minDistance, minDistance );
                        }

                        // Apply the force due to this edge.
                        Vector2D forceVector = new Vector2D( edgeForceConstant / Math.pow( lengthBounds.getCenter(), 2 ), 0 ).getRotatedInstance( angle + Math.PI );
                        p.applyForce( forceVector );
                    }

                    // Apply the force from each of the other particles, but
                    // set some limits on the max force that can be applied.
                    for ( PointMass otherP : map.values() ) {
                        if ( p != otherP ) {
                            // Calculate force vector, but handle cases where too close.
                            Vector2D vectorToOther = p.position.minus( otherP.position );
                            if ( vectorToOther.magnitude() < minDistance ) {
                                if ( vectorToOther.magnitude() == 0 ) {
                                    // Create a random vector of min distance.
                                    System.out.println( "Creating random vector" );
                                    double angle = RAND.nextDouble() * Math.PI * 2;
                                    vectorToOther = new Vector2D( minDistance * Math.cos( angle ), minDistance * Math.sin( angle ) );
                                }
                                else {
                                    vectorToOther = vectorToOther.getInstanceOfMagnitude( minDistance );
                                }
                            }
                            p.applyForce( vectorToOther.getInstanceOfMagnitude( particleForceConstant / ( vectorToOther.magnitudeSquared() ) ) );
                        }
                    }
                }
                else {
                    // Point is outside container, move it towards center of shape.
                    Vector2D vectorToCenter = new Vector2D( boundingRect.getCenterX(), boundingRect.getCenterY() ).minus( p.position );
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
        for ( EnergyChunkContainerSlice slice : energyChunkContainerSlices ) {
            for ( EnergyChunk energyChunk : slice.energyChunkList ) {
                energyChunk.position.set( new Vector2D( map.get( energyChunk ).position ) );
            }
        }
    }

    public static Vector2D generateRandomLocation( Rectangle2D rect ) {
        return new Vector2D( rect.getMinX() + ( RAND.nextDouble() * rect.getWidth() ), rect.getMinY() + ( RAND.nextDouble() * rect.getHeight() ) );
    }

    private static class PointMass {
        private static final double MASS = 1; // In kg.
        private final MutableVector2D position = new MutableVector2D();
        private final MutableVector2D velocity = new MutableVector2D( 0, 0 );
        private final MutableVector2D acceleration = new MutableVector2D( 0, 0 );
        private final Shape containerShape;

        public PointMass( Vector2D initialPosition, Shape container ) {
            this.containerShape = container;
            position.setValue( initialPosition );
        }

        public void applyForce( Vector2D force ) {
            acceleration.add( force.times( force.magnitude() / MASS ) );
        }

        public void clearAcceleration() {
            acceleration.setComponents( 0, 0 );
        }

        public void updatePosition( double dt ) {

            // Update the velocity based on previous velocity and current acceleration.
            velocity.add( acceleration.times( dt ) );

            if ( containerShape.contains( position.toPoint2D() ) ) {

                // Limit the velocity.  This acts much like a drag force that
                // gets stronger as the velocity gets bigger.
                double maxVelocity = Math.min( containerShape.getBounds2D().getWidth(), containerShape.getBounds2D().getHeight() ) / 20 / dt;
                velocity.setMagnitude( maxVelocity * velocity.magnitude() / ( velocity.magnitude() + maxVelocity ) );
                velocity.setMagnitude( velocity.magnitude() * 0.75 ); // Drag.

                // Check that the velocity won't move the point outside of the container.
                if ( containerShape.contains( position.toPoint2D() ) && !containerShape.contains( position.plus( velocity.times( dt ) ).toPoint2D() ) ) {
                    System.out.println( "Limiting the velocity" );
                    velocity.setMagnitude( 0 );
                }
            }

            // Update the position.
            position.add( velocity.times( dt ) );
        }

        public Shape getContainerShape() {
            return containerShape;
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

