// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Tests 3D model to 2D view transform.
 * The transform is not a true 3D perspective, it is a parallelogram perspective, which has no vanishing point.
 * This perspective is very specific to the Capacitor Lab simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Test3DTransform extends JFrame {

    private static final double MVT_SCALE = 3;
    private static final double PITCH = Math.toRadians( 60 ); // rotation about horizontal axis
    private static final double YAW = Math.toRadians( -45 ); // rotation about vertical axis

    public Test3DTransform() {

        CLModelViewTransform3D mvt = new CLModelViewTransform3D( MVT_SCALE, PITCH, YAW );

        /*
         * Model-to-view transform that defines the top face of a capacitor plate.
         * The corners of the face are specified in 3D model coordinate frame.
         * Path starts at the lower-left corner and proceeds clockwise.
         * Origin is at the center of the plate.
         */
        double width = 100; // model units
        double depth = 50; // model units
        Point2D p1 = mvt.modelToView( -width / 2, 0, -depth / 2 );
        Point2D p2 = mvt.modelToView( -width / 2, 0, depth / 2 );
        Point2D p3 = mvt.modelToView( width / 2, 0, depth / 2 );
        Point2D p4 = mvt.modelToView( width / 2, 0, -depth / 2 );

        /*
         * Create the geometry using view coordinates.
         */
        GeneralPath path = new GeneralPath();
        path.moveTo( (float) p1.getX(), (float) p1.getY() );
        path.lineTo( (float) p2.getX(), (float) p2.getY() );
        path.lineTo( (float) p3.getX(), (float) p3.getY() );
        path.lineTo( (float) p4.getX(), (float) p4.getY() );
        path.closePath();
        PPath pathNode = new PPath( path );
        pathNode.setStroke( new BasicStroke( 1f ) );
        pathNode.setStrokePaint( Color.BLACK );
        pathNode.setPaint( Color.RED );
        pathNode.setOffset( 300, 200 );

        // canvas
        PCanvas canvas = new PCanvas();
        canvas.setPreferredSize( new Dimension( 1024, 768 ) );
        canvas.getLayer().addChild( pathNode );
        setContentPane( canvas );

        pack();
    }

    public static void main( String[] args ) {
        JFrame frame = new Test3DTransform();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
