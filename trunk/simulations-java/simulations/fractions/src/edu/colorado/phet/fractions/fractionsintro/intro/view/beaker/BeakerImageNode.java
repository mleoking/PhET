// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionsintro.intro.view.beaker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.fractions.FractionsResources;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Taken from the Dilutions sim
 * This class encapsulates knowledge about points in the beaker image.
 * The image was built around the shape of a cylinder, and we can programmatically fill that cylinder with solution.
 * Rather than hard code details of the cylinder (offsets and sizes), this class allows us to name points in the image,
 * and provides an interface for interrogating the image.  If the image is ever changed, the points herein will
 * need to be changed.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeakerImageNode extends PImage {

    private static final String CYLINDER_UPPER_LEFT = "cylinderUpperLeft";
    private static final String CYLINDER_LOWER_RIGHT = "cylinderLowerRight";
    private static final String CYLINDER_END_BACKGROUND = "cylinderEndBackground";
    private static final String CYLINDER_END_FOREGROUND = "cylinderEndForeground";

    private final NamedPoints points;

    public BeakerImageNode( BufferedImage image ) {
        super( image );

        // points of interest in the untransformed image, get these via inspection in Photoshop or other image-editing program
        points = new NamedPoints( this ) {{
            addOffset( CYLINDER_UPPER_LEFT, new Point2D.Double( 98, 82 ) );
            addOffset( CYLINDER_LOWER_RIGHT, new Point2D.Double( 526, 644 ) );
            addOffset( CYLINDER_END_BACKGROUND, new Point2D.Double( 210, 166 ) );
            addOffset( CYLINDER_END_FOREGROUND, new Point2D.Double( 210, 218 ) );
        }};
    }

    public PDimension getCylinderSize() {
        Point2D pUpperLeft = points.getOffset( CYLINDER_UPPER_LEFT );
        Point2D pLowerRight = points.getOffset( CYLINDER_LOWER_RIGHT );
        return new PDimension( pLowerRight.getX() - pUpperLeft.getX(), pLowerRight.getY() - pUpperLeft.getY() );
    }

    public Point2D getCylinderOffset() {
        return points.getOffset( CYLINDER_UPPER_LEFT );
    }

    public double getCylinderEndHeight() {
        return points.getOffset( CYLINDER_END_FOREGROUND ).getY() - points.getOffset( CYLINDER_END_BACKGROUND ).getY();
    }

    // run this test to check alignment of cylinder with image file
    public static void main( String[] args ) {
        // beaker image
        final BeakerImageNode beakerNode = new BeakerImageNode( FractionsResources.Images.WATER_GLASS_FRONT ) {{
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
