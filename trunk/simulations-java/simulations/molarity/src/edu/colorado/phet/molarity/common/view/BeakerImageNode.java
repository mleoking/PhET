// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.molarity.common.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.molarity.MolarityResources.Images;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class encapsulates knowledge about points in the beaker image.
 * The image was built around the 2D projection of a 3D cylinder,
 * and we can programmatically fill that cylinder with solution.
 * Methods are provided to access points of interest in the image file.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeakerImageNode extends PImage {

    // points of interest in the image file
    private static final Point2D CYLINDER_UPPER_LEFT = new Point2D.Double( 98, 192 );
    private static final Point2D CYLINDER_LOWER_RIGHT = new Point2D.Double( 526, 644 );
    private static final Point2D CYLINDER_END_BACKGROUND = new Point2D.Double( 210, 166 );
    private static final Point2D CYLINDER_END_FOREGROUND = new Point2D.Double( 210, 218 );

    public BeakerImageNode() {
        super( Images.BEAKER_IMAGE );
    }

    // Gets the cylinder dimensions.
    public PDimension getCylinderSize() {
        Point2D pUpperLeft = getTransformedPoint( CYLINDER_UPPER_LEFT );
        Point2D pLowerRight = getTransformedPoint( CYLINDER_LOWER_RIGHT );
        return new PDimension( pLowerRight.getX() - pUpperLeft.getX(), pLowerRight.getY() - pUpperLeft.getY() );
    }

    // Gets the offset of the cylinder from the upper-left corner of the image.
    public Point2D getCylinderOffset() {
        return getTransformedPoint( CYLINDER_UPPER_LEFT );
    }

    // Gets the 2D height of the cylinder's end cap.
    public double getCylinderEndHeight() {
        return getTransformedPoint( CYLINDER_END_FOREGROUND ).getY() - getTransformedPoint( CYLINDER_END_BACKGROUND ).getY();
    }

    private Point2D getTransformedPoint( Point2D p ) {
        return getTransform().transform( p, null );
    }

    // run this test to check alignment of cylinder with image file
    public static void main( String[] args ) {
        // beaker image
        final BeakerImageNode beakerNode = new BeakerImageNode() {{
            setOffset( 30, 30 );
            getTransformReference( true ).scale( 0.5, 1 );
        }};
        // draw the cylinder that represents the beaker's interior
        final PPath cylinderNode = new PPath() {{
            PDimension cylinderSize = beakerNode.getCylinderSize();
            double cylinderEndHeight = beakerNode.getCylinderEndHeight();
            Area area = new Area( new Rectangle2D.Double( 0, 0, cylinderSize.width, cylinderSize.height ) );
            area.add( new Area( new Ellipse2D.Double( 0, -cylinderEndHeight / 2, cylinderSize.width, cylinderEndHeight ) ) );
            area.add( new Area( new Ellipse2D.Double( 0, cylinderSize.height - ( cylinderEndHeight / 2 ), cylinderSize.width, cylinderEndHeight ) ) );
            setPathTo( area );
            setStrokePaint( Color.RED );
            setOffset( beakerNode.getCylinderOffset() );
        }};
        // canvas
        final PCanvas canvas = new PCanvas() {{
            getLayer().addChild( beakerNode );
            getLayer().addChild( cylinderNode );
            setPreferredSize( new Dimension( 600, 600 ) );
        }};
        // frame
        JFrame frame = new JFrame() {{
            setContentPane( canvas );
            pack();
            setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        }};
        frame.setVisible( true );
    }
}
