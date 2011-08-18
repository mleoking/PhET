// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Several utilities for making it easier to create some complex and somewhat
 * random shapes.  This was created initially to make it easier to create the
 * shapes associated with biomolecules, but may have other uses.
 *
 * @author John Blanco
 */
public class ShapeCreationUtils {

    public static Shape createShapeFromPoints( List<Point2D> points ) {
        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( points.get( 0 ) );
        for ( Point2D point : points ) {
            path.lineTo( point );
        }
        path.closePath();
        return path.getGeneralPath();
    }

    /**
     * Create a shape from a set of points.  The points must be in an order
     * that, if connected by straight lines, would form a closed shape.  If not,
     * the shape will look quite odd.
     *
     * @param points
     * @return
     */
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

    /**
     * Create a shape based on a set of points.  The points must be in an order
     * that, if connected by straight lines, would form a closed shape.  Some of
     * the segments will be straight lines and some will be curved, and which is
     * which will be based on a pseudo-random variable.
     *
     * @param points
     * @param seed
     * @return
     */
    public static Shape createRandomShapeFromPoints( List<Point2D> points, int seed ) {
        Random rand = new Random( seed );
        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( points.get( 0 ) );
        for ( int i = 0; i < points.size(); i++ ) {
            ImmutableVector2D segmentStartPoint = new ImmutableVector2D( points.get( i ) );
            ImmutableVector2D segmentEndPoint = new ImmutableVector2D( points.get( ( i + 1 ) % points.size() ) );
            ImmutableVector2D previousPoint = new ImmutableVector2D( points.get( i - 1 >= 0 ? i - 1 : points.size() - 1 ) );
            ImmutableVector2D nextPoint = new ImmutableVector2D( points.get( ( i + 2 ) % points.size() ) );
            ImmutableVector2D controlPoint1 = extrapolateControlPoint( previousPoint, segmentStartPoint, segmentEndPoint );
            ImmutableVector2D controlPoint2 = extrapolateControlPoint( nextPoint, segmentEndPoint, segmentStartPoint );
            if ( rand.nextBoolean() ) {
                // Curved segment.
                path.curveTo( controlPoint1.getX(), controlPoint1.getY(), controlPoint2.getX(), controlPoint2.getY(), segmentEndPoint.getX(), segmentEndPoint.getY() );
            }
            else {
                // Straight segment.
                path.lineTo( segmentEndPoint.getX(), segmentEndPoint.getY() );
            }
        }
        return path.getGeneralPath();
    }

    public static Shape createRandomShape( Dimension2D size, int seed ) {
        Random rand = new Random( seed );
        DoubleGeneralPath path = new DoubleGeneralPath();
        List<Point2D> pointList = new ArrayList<Point2D>();
        // Create a series of points that will enclose a space.
        for ( double angle = 0; angle < 1.9 * Math.PI; angle += Math.PI / 10 + rand.nextDouble() * Math.PI / 10 ) {
            pointList.add( ImmutableVector2D.parseAngleAndMagnitude( 0.5 + rand.nextDouble(), angle ).toPoint2D() );
        }

        Shape unscaledShape = createRandomShapeFromPoints( pointList, seed );

        // Scale the shape to the specified size.
        double horizontalScale = size.getWidth() / unscaledShape.getBounds2D().getWidth();
        double verticalScale = size.getHeight() / unscaledShape.getBounds2D().getHeight();
        Shape scaledShape = AffineTransform.getScaleInstance( horizontalScale, verticalScale ).createTransformedShape( unscaledShape );
        return scaledShape;
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
        List<Point2D> vLikePointList = new ArrayList<Point2D>() {{
            // V-like shape.
            add( new Point2D.Double( 0, 0 ) );
            add( new Point2D.Double( 20, -10 ) );
            add( new Point2D.Double( 0, 40 ) );
            add( new Point2D.Double( -20, -10 ) );
        }};

        List<Point2D> squarePointList = new ArrayList<Point2D>() {{
            add( new Point2D.Double( 0, 0 ) );
            add( new Point2D.Double( 20, 0 ) );
            add( new Point2D.Double( 20, -20 ) );
            add( new Point2D.Double( 0, -20 ) );
            // Unit square - useful for debug in some cases.
//            add( new Point2D.Double( 0, 0 ) );
//            add( new Point2D.Double( 1, 0 ) );
//            add( new Point2D.Double( 1, -1 ) );
//            add( new Point2D.Double( 0, -1 ) );
        }};

        // Add the shapes.  Many are translated somewhat to avoid overlap.
        PCanvas canvas = new PCanvas();
        Shape shape = ShapeCreationUtils.createShapeFromPoints( vLikePointList );
        canvas.getLayer().addChild( new PhetPPath( AffineTransform.getTranslateInstance( 50, 50 ).createTransformedShape( shape ), Color.PINK ) );
        shape = ShapeCreationUtils.createRoundedShapeFromPoints( vLikePointList );
        canvas.getLayer().addChild( new PhetPPath( AffineTransform.getTranslateInstance( 100, 50 ).createTransformedShape( shape ), Color.GREEN ) );
        shape = ShapeCreationUtils.createRandomShapeFromPoints( vLikePointList, 101 );
        canvas.getLayer().addChild( new PhetPPath( AffineTransform.getTranslateInstance( 50, 100 ).createTransformedShape( shape ), Color.ORANGE ) );
        shape = ShapeCreationUtils.createRandomShape( new PDimension( 40, 40 ), 100 );
        canvas.getLayer().addChild( new PhetPPath( AffineTransform.getTranslateInstance( 100, 100 ).createTransformedShape( shape ), Color.BLUE ) );
        shape = ShapeCreationUtils.createRandomShape( new PDimension( 80, 40 ), 123 );
        canvas.getLayer().addChild( new PhetPPath( AffineTransform.getTranslateInstance( 50, 200 ).createTransformedShape( shape ), Color.MAGENTA ) );

        // Boiler plate app stuff.
        JFrame frame = new JFrame( "Shape Util Testing" );
        frame.setContentPane( canvas );
        frame.setSize( 500, 500 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
