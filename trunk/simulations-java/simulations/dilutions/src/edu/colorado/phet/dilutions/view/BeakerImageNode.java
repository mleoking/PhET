// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.dilutions.DilutionsResources.Images;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class encapsulates knowledge about points in the beaker image.
 * The image was built around the shape of a cylinder, and we can programmatically fill that cylinder with solution.
 * Rather than hard code details of the cylinder (offsets and sizes), this class allows us to name points in the image,
 * and provides an interface for interrogating the image.  If the image is ever changed, the points herein will
 * need to be changed.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeakerImageNode extends NamedPointsNode {

    private static final String CYLINDER_UPPER_LEFT = "cylinderUpperLeft";
    private static final String CYLINDER_LOWER_RIGHT = "cylinderLowerRight";
    private static final String CYLINDER_END_BACKGROUND = "cylinderEndBackground";
    private static final String CYLINDER_END_FOREGROUND = "cylinderEndForeground";

    public BeakerImageNode() {

        PImage imageNode = new PImage( Images.BEAKER_IMAGE );
        addChild( imageNode );

        // points of interest in the untransformed image, get these via inspection in Photoshop or other image-editing program
        addOffset( CYLINDER_UPPER_LEFT, new Point2D.Double( 102, 192 ) );
        addOffset( CYLINDER_LOWER_RIGHT, new Point2D.Double( 530, 644 ) );
        addOffset( CYLINDER_END_BACKGROUND, new Point2D.Double( 215, 166 ) );
        addOffset( CYLINDER_END_FOREGROUND, new Point2D.Double( 215, 218 ) );
    }

    public PDimension getCylinderSize() {
        Point2D pUpperLeft = getOffset( CYLINDER_UPPER_LEFT );
        Point2D pLowerRight = getOffset( CYLINDER_LOWER_RIGHT );
        return new PDimension( pLowerRight.getX() - pUpperLeft.getX(), pLowerRight.getY() - pUpperLeft.getY() );
    }

    public Point2D getCylinderOffset() {
        return getOffset( CYLINDER_UPPER_LEFT );
    }

    public double getCylinderEndHeight() {
        return getOffset( CYLINDER_END_FOREGROUND ).getY() - getOffset( CYLINDER_END_BACKGROUND ).getY();
    }

    // test
    public static void main( String[] args ) {
        // beaker image
        final BeakerImageNode beakerNode = new BeakerImageNode() {{
            setOffset( 30, 30 );
            scale( 0.75 );
        }};
        // draw the cylinder that represents the beaker's interior
        final Point2D cylinderOffset = beakerNode.getCylinderOffset();
        final PDimension cylinderSize = beakerNode.getCylinderSize();
        final PPath cylinderNode = new PPath() {{
            setPathTo( new Rectangle2D.Double( cylinderOffset.getX(), cylinderOffset.getY(), cylinderSize.getWidth(), cylinderSize.getHeight() ) );
            setStrokePaint( Color.RED );
        }};
        // canvas
        final PCanvas canvas = new PCanvas() {{
            getLayer().addChild( beakerNode );
            getLayer().addChild( cylinderNode );
            setPreferredSize( new Dimension( 800, 800 ) );
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
