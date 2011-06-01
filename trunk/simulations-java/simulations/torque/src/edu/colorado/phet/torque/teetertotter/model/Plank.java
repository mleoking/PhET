// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.model;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

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
}
