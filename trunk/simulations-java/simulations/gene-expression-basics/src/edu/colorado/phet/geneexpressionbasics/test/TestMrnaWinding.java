// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author John Blanco
 */
public class TestMrnaWinding {

    private static final int NUM_POINTS = 20;
    private static final double INTER_POINT_DISTANCE = 100; // Model coords, which are nanometers.
    private static final double CONTAINMENT_RECT_WIDTH = 1000;
    private static final double CONTAINMENT_RECT_HEIGHT = 1000;
    private static final Random RAND = new Random();

    /**
     * Main routine that constructs a PhET Piccolo canvas in a window.
     *
     * @param args
     */
    public static void main( String[] args ) {

        Dimension2D stageSize = new PDimension( 800, 600 );

        PhetPCanvas canvas = new PhetPCanvas();
        // Set up the canvas-screen transform.
        canvas.setWorldTransformStrategy( new PhetPCanvas.CenteredStage( canvas, stageSize ) );

        ModelViewTransform mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( stageSize.getWidth() * 0.5 ), (int) Math.round( stageSize.getHeight() * 0.50 ) ),
                0.1 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        // Set the background color.
        canvas.setBackground( new Color( 220, 241, 255 ) );

        // Create the containment rectangle.  Size is in model space, i.e.
        // nanometers.
        Rectangle2D containmentRect = new Rectangle2D.Double( 0, 0, CONTAINMENT_RECT_WIDTH, CONTAINMENT_RECT_HEIGHT );

        // Put the containment rect on the canvas.
        canvas.addWorldChild( new PhetPPath( mvt.modelToView( containmentRect ), new BasicStroke( 1 ), Color.BLACK ) );

        // Create a linked list of points.
        final LinkablePoint firstPoint = new LinkablePoint( new Point2D.Double( 0, 0 ), 0 );
        LinkablePoint currentPoint = firstPoint;
        LinkablePoint nextPoint;
        for ( int i = 0; i < NUM_POINTS - 1; i++ ) {
            // Add a point.
            nextPoint = new LinkablePoint( new Point2D.Double( 0, 0 ), INTER_POINT_DISTANCE );
            currentPoint.setNextPointMass( nextPoint );
            nextPoint.setPreviousPointMass( currentPoint );
            currentPoint = nextPoint;
        }

        // Position the points in the containment rectangle.
        currentPoint = firstPoint;
        while ( currentPoint != null ) {
            currentPoint.setPosition( containmentRect.getMinX() + RAND.nextDouble() * CONTAINMENT_RECT_WIDTH,
                                      containmentRect.getMinY() + RAND.nextDouble() * CONTAINMENT_RECT_HEIGHT );
            currentPoint = currentPoint.getNextPoint();
        }

        // Draw the line defined by the points.
        canvas.addWorldChild( new SquiggleNode( firstPoint, mvt ) );

        // Boiler plate app stuff.
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( (int) stageSize.getWidth(), (int) stageSize.getHeight() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null ); // Center.
        frame.setVisible( true );
    }

    /**
     * Takes link list of point and draws both points and lines between them.
     */
    private static class SquiggleNode extends PNode {
        private static final Stroke STROKE = new BasicStroke( 1 );

        private SquiggleNode( LinkablePoint firstPointInList, ModelViewTransform mvt ) {
            PNode lineLayer = new PNode();
            addChild( lineLayer );
            PNode pointLayer = new PNode();
            addChild( pointLayer );

            LinkablePoint currentPoint = firstPointInList;
            while ( currentPoint != null ) {
                pointLayer.addChild( new PointNode( currentPoint, mvt ) );
                if ( currentPoint.getNextPoint() != null ) {
                    LinkablePoint nextPoint = currentPoint.getNextPoint();
                    pointLayer.addChild( new PointNode( nextPoint, mvt ) );
                    DoubleGeneralPath path = new DoubleGeneralPath( mvt.modelToView( currentPoint.getPosition() ) );
                    path.lineTo( mvt.modelToView( nextPoint.getPosition() ) );
                    lineLayer.addChild( new PhetPPath( path.getGeneralPath(), STROKE, Color.BLACK ) );
                }
                currentPoint = currentPoint.getNextPoint();
            }
        }
    }


    /**
     * PNode to show a single point.
     */
    private static class PointNode extends PNode {
        private static final double DIAMETER = 4; // In screen coords, basically pixels.
        private static final Color COLOR = Color.RED;

        private PointNode( LinkablePoint linkablePoint, ModelViewTransform mvt ) {
            PhetPPath pointNode = new PhetPPath( new Ellipse2D.Double( -DIAMETER / 2, -DIAMETER / 2, DIAMETER, DIAMETER ), COLOR );
            pointNode.setOffset( mvt.modelToView( linkablePoint.getPosition() ) );
            addChild( pointNode );
        }
    }

    /**
     * Point that has references to a previous and next point, and thus can
     * be used in a link list that defines an open shape.
     */
    public static class LinkablePoint {
        private final Point2D position = new Point2D.Double( 0, 0 );
        private LinkablePoint previousPointMass = null;
        private LinkablePoint nextPointMass = null;

        private final double targetDistanceToPreviousPoint;

        private LinkablePoint( Point2D initialPosition, double targetDistanceToPreviousPoint ) {
            setPosition( initialPosition );
            this.targetDistanceToPreviousPoint = targetDistanceToPreviousPoint;
        }

        @Override public String toString() {
            return getClass().getName() + " Position: " + position.toString();
        }

        public void setPosition( double x, double y ) {
            position.setLocation( x, y );
        }

        public void setPosition( Point2D position ) {
            setPosition( position.getX(), position.getY() );
        }

        public Point2D getPosition() {
            return new Point2D.Double( position.getX(), position.getY() );
        }

        public LinkablePoint getPreviousPointMass() {
            return previousPointMass;
        }

        public void setPreviousPointMass( LinkablePoint previousPointMass ) {
            this.previousPointMass = previousPointMass;
        }

        public LinkablePoint getNextPoint() {
            return nextPointMass;
        }

        public void setNextPointMass( LinkablePoint nextPointMass ) {
            this.nextPointMass = nextPointMass;
        }

        public double getTargetDistanceToPreviousPoint() {
            return targetDistanceToPreviousPoint;
        }

        public double distance( LinkablePoint p ) {
            return this.getPosition().distance( p.getPosition() );
        }

        public void translate( ImmutableVector2D translationVector ) {
            setPosition( position.getX() + translationVector.getX(), position.getY() + translationVector.getY() );
        }
    }
}
