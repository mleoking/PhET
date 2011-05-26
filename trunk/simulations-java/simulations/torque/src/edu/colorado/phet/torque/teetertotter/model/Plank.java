// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.model;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

/**
 * This is the pivot point where the teeter-totter is balanced.
 *
 * @author Sam Reid
 */
public class Plank extends ModelObject {

    public static final double LENGTH = 4;// meters
    public static final double THICKNESS = 0.05; // meters
    public static final int NUM_SNAP_TO_MARKERS = 19;

    public Plank( double centerHeight ) {
        super( generateShape( centerHeight, 0 ) );
    }

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
}
