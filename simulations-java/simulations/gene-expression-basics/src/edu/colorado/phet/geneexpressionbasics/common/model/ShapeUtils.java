// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PCanvas;

/**
 * @author John Blanco
 */
public class ShapeUtils {

    public static Shape createShapeFromPoints( List<Point2D> points ) {
        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( points.get( 0 ) );
        for ( Point2D point : points ) {
            path.lineTo( point );
        }
        path.closePath();
        return path.getGeneralPath();
    }

    public static Shape createRoundedShapeFromPoints( List<Point2D> points ) {
        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( points.get( 0 ) );
        for ( int i = 0; i < points.size(); i++ ) {
            ImmutableVector2D segmentStartPoint = new ImmutableVector2D( points.get( i ) );
            ImmutableVector2D segmentEndPoint = new ImmutableVector2D( points.get( ( i + 1 ) % points.size() ) );
            ImmutableVector2D previousPoint = new ImmutableVector2D( points.get( i - 1 >= 0 ? i - 1 : points.size() - 1 ) );
            ImmutableVector2D nextPoint = new ImmutableVector2D( points.get( ( i + 2 ) % points.size() ) );
            ImmutableVector2D controlPoint1 = extrapolateControlPoint( previousPoint, segmentStartPoint, segmentEndPoint );
            ImmutableVector2D controlPoint2 = extrapolateControlPoint( nextPoint, segmentEndPoint, segmentStartPoint );
            path.curveTo( controlPoint1.getX(), controlPoint1.getY(), controlPoint2.getX(), controlPoint2.getY(), segmentEndPoint.getX(), segmentEndPoint.getY() );
        }
        return path.getGeneralPath();
    }

    // Extrapolate a control point for a curve based on three points.  This
    // is used to "go around the corner" at y, staring from x, and heading
    // towards z.  If that makes any sense.
    private static ImmutableVector2D extrapolateControlPoint( ImmutableVector2D x, ImmutableVector2D y, ImmutableVector2D z ) {
        ImmutableVector2D xy = y.getSubtractedInstance( x );
        ImmutableVector2D yz = z.getSubtractedInstance( y );
        return y.getAddedInstance( xy.getScaledInstance( 0.25 ).getAddedInstance( yz.getScaledInstance( 0.25 ) ) );
    }

    // First extrapolation attempt.  Should work well in theory, but didn't
    // due to issues with angles, pi vs -pi, etc.
    private static ImmutableVector2D extrapolateControlPoint2( ImmutableVector2D x, ImmutableVector2D y, ImmutableVector2D z ) {
        Vector2D scaledYZ = new Vector2D( z.getSubtractedInstance( y ) ).scale( 0.33 );
        double xyAngle = x.getSubtractedInstance( y ).getAngle();
        if ( xyAngle < 0 ) {
            xyAngle += Math.PI;
        }
        double zyAngle = z.getSubtractedInstance( y ).getAngle();
        if ( zyAngle < 0 ) {
            zyAngle += Math.PI;
        }
        double xyzAngle = xyAngle - zyAngle;
        return y.getAddedInstance( scaledYZ.rotate( xyzAngle / 2 ) );
    }

    public static void main( String[] args ) {
        System.out.println( "Shape testing..." );

        PCanvas canvas = new PCanvas();
        List<Point2D> point2DList = new ArrayList<Point2D>() {{
            // V-like shape.
            add( new Point2D.Double( 0, 0 ) );
            add( new Point2D.Double( 20, -10 ) );
            add( new Point2D.Double( 0, 40 ) );
            add( new Point2D.Double( -20, -10 ) );
            // Unit square.
//            add( new Point2D.Double( 0, 0 ) );
//            add( new Point2D.Double( 1, 0 ) );
//            add( new Point2D.Double( 1, -1 ) );
//            add( new Point2D.Double( 0, -1 ) );
            // larger square.
//            add( new Point2D.Double( 0, 0 ) );
//            add( new Point2D.Double( 20, 0 ) );
//            add( new Point2D.Double( 20, -20 ) );
//            add( new Point2D.Double( 0, -20 ) );
        }};
        Shape angularShape = ShapeUtils.createShapeFromPoints( point2DList );
        canvas.getLayer().addChild( new PhetPPath( AffineTransform.getTranslateInstance( 50, 50 ).createTransformedShape( angularShape ), Color.PINK ) );
        Shape roundedShape = ShapeUtils.createRoundedShapeFromPoints( point2DList );
        canvas.getLayer().addChild( new PhetPPath( AffineTransform.getTranslateInstance( 100, 100 ).createTransformedShape( roundedShape ), Color.GREEN ) );

        JFrame frame = new JFrame( "Shape Util Testing" );
        frame.setContentPane( canvas );
        frame.setSize( (int) 500, (int) 500 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
