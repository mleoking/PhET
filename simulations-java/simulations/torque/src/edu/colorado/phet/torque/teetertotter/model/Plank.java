// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.model;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * This is the plank upon which weights can be placed.
 *
 * @author John Blanco
 */
public class Plank extends ModelObject {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    public static final double LENGTH = 4;// meters
    public static final double THICKNESS = 0.05; // meters
    public static final int NUM_SNAP_TO_MARKERS = 19;
    public static final double MASS = 20; // kg

    // Moment of inertia.
    // TODO: I'm not certain that this is the correct formula, should check with Mike Dubson.
    public static final double MOMENT_OF_INERTIA = MASS * ( ( LENGTH * LENGTH ) + ( THICKNESS * THICKNESS ) ) / 12;

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    public double torqueFromWeights = 0;
    public double tiltAngle = 0;
    public double angularVelocity = 0; // radians/sec
    public final double maxTiltAngle;

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------

    public Plank( final ConstantDtClock clock, double centerHeight ) {
        super( generateShape( centerHeight, 0 ) );
        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                stepInTime( clockEvent.getSimulationTimeChange() );
            }
        } );
        maxTiltAngle = Math.asin( centerHeight / ( LENGTH / 2 ) );
    }

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    public double getLength() {
        return LENGTH;
    }

    private static Shape generateShape( final double centerHeight, double tiltAngle ) {
        // Create the outline shape of the plank.
        GeneralPath path = new GeneralPath();
        path.moveTo( 0, 0 );
        path.lineTo( LENGTH / 2, 0 );
        path.lineTo( LENGTH / 2, THICKNESS );
        path.lineTo( 0, THICKNESS );
        path.lineTo( -LENGTH / 2, THICKNESS );
        path.lineTo( -LENGTH / 2, 0 );
        path.lineTo( 0, 0 );
        // Add the "snap to" markers to the plank.
        double interMarkerDistance = LENGTH / (double) ( NUM_SNAP_TO_MARKERS + 1 );
        double markerXPos = -LENGTH / 2 + interMarkerDistance;
        for ( int i = 0; i < NUM_SNAP_TO_MARKERS; i++ ) {
            path.moveTo( markerXPos, 0 );
            path.lineTo( markerXPos, THICKNESS );
            markerXPos += interMarkerDistance;
        }
        // Rotate the appropriate amount.
        Shape shape = AffineTransform.getRotateInstance( tiltAngle ).createTransformedShape( path );
        // Translate to the appropriate height.
        shape = AffineTransform.getTranslateInstance( 0, centerHeight ).createTransformedShape( shape );

        return shape;
    }

    public Point2D getClosestOpenLocation( Point2D p ) {
        // TODO: Doesn't actually give open locations yet, just valid snap-to ones.
        // TODO: Doesn't handle rotation yet.
        double minX = getShape().getBounds2D().getMinX();
        double maxX = getShape().getBounds2D().getMaxX();
        double increment = getShape().getBounds2D().getWidth() / ( NUM_SNAP_TO_MARKERS + 1 );
        double xPos = 0;
        if ( p.getX() > maxX - increment ) {
            xPos = maxX - increment;
        }
        else if ( xPos < minX + increment ) {
            xPos = minX + increment;
        }
        else {
            int numLengths = (int) Math.round( ( p.getX() - minX ) / increment );
            xPos = minX + numLengths * increment;
        }
        return new Point2D.Double( xPos, getShape().getBounds2D().getMaxY() );
    }

    /**
     * Force the plank back to the level position.  This is generally done when some sort of support column has been
     * put into place.
     */
    public void forceToLevel() {
        tiltAngle = 0;
        updateShape();
    }

    public void setTorqueFromWeights( double torque ) {
        torqueFromWeights = torque;
    }

    public void stepInTime( double dt ) {
        // Update the angular velocity based on the current torque.
        angularVelocity += torqueFromWeights / MOMENT_OF_INERTIA;
        if ( angularVelocity != 0 ) {
            tiltAngle += angularVelocity * dt;
            if ( Math.abs( tiltAngle ) > maxTiltAngle ) {
                tiltAngle = maxTiltAngle * ( tiltAngle < 0 ? -1 : 1 );
                angularVelocity = 0;
            }
            updateShape();
        }
    }

    private void updateShape() {
        setShapeProperty( generateShape( positionHandle.getY(), tiltAngle ) );
    }

    //------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------
}
