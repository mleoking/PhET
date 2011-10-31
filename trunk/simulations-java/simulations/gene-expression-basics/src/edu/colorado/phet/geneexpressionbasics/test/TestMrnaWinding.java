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
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
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

    private static final int NUM_POINTS = 4;
    private static final double INTER_POINT_DISTANCE = 100; // Model coords, which are nanometers.
    private static final double CONTAINMENT_RECT_WIDTH = 1000;
    private static final double CONTAINMENT_RECT_HEIGHT = 1000;
    private static final Random RAND = new Random();

    private final Rectangle2D containmentRect;
    private final LinkablePoint firstShapeDefiningPoint;

    public TestMrnaWinding() {

        // Create the containment rectangle.  Size is in model space, i.e.
        // nanometers.
        containmentRect = new Rectangle2D.Double( 0, 0, CONTAINMENT_RECT_WIDTH, CONTAINMENT_RECT_HEIGHT );

        // Create a linked list of points.
        firstShapeDefiningPoint = new LinkablePoint( new Point2D.Double( 0, 0 ), 0 );
        LinkablePoint currentPoint = firstShapeDefiningPoint;
        LinkablePoint nextPoint;
        for ( int i = 0; i < NUM_POINTS - 1; i++ ) {
            // Add a point.
            nextPoint = new LinkablePoint( new Point2D.Double( 0, 0 ), INTER_POINT_DISTANCE );
            currentPoint.setNextPointMass( nextPoint );
            nextPoint.setPreviousPointMass( currentPoint );
            currentPoint = nextPoint;
        }

        // Set initial positions of points.
        setInitialPointPositions01( firstShapeDefiningPoint, containmentRect );
    }

    /**
     * Position randomly in rect.
     */
    private static void setInitialPointPositions01( LinkablePoint firstPointOnList, Rectangle2D containmentRect ) {
        assert firstPointOnList != null;
        // Place the points randomly within the rectangle, but with the first
        // and last points and the corners of the rect.
//        firstPointOnList.setPosition( containmentRect.getMinX(), containmentRect.getMaxY() );
//        LinkablePoint currentPoint = firstPointOnList.getNextPoint();
//        while ( currentPoint != null ) {
//            if ( currentPoint.getNextPoint() != null ){
//                currentPoint.setPosition( containmentRect.getMinX() + RAND.nextDouble() * containmentRect.getWidth(),
//                                          containmentRect.getMinY() + RAND.nextDouble() * containmentRect.getHeight() );
//            }
//            else{
//                // Last point on list, put it in the lower right of the
//                // containment rect.
//                currentPoint.setPosition( containmentRect.getMaxX(), containmentRect.getMinY() );
//            }
//            currentPoint = currentPoint.getNextPoint();
//        }

        // Test - 3 points only, with middle in center.
//        firstPointOnList.setPosition( containmentRect.getMinX(), containmentRect.getMaxY() );
//        LinkablePoint currentPoint = firstPointOnList.getNextPoint();
//        currentPoint.setPosition( containmentRect.getMinX() + containmentRect.getWidth() / 2,
//                                  containmentRect.getMinY() + containmentRect.getHeight() / 2 );
//        currentPoint = currentPoint.getNextPoint();
//        currentPoint.setPosition( containmentRect.getMaxX(), containmentRect.getMinY() );

        // Test - 3 points only, with middle in upper right.
//        firstPointOnList.setPosition( containmentRect.getMinX(), containmentRect.getMaxY() );
//        LinkablePoint currentPoint = firstPointOnList.getNextPoint();
//        currentPoint.setPosition( containmentRect.getMaxX(),
//                                  containmentRect.getMaxY() );
//        currentPoint = currentPoint.getNextPoint();
//        currentPoint.setPosition( containmentRect.getMaxX(), containmentRect.getMinY() );

        // Test - 4 points only, with 2nd in upper right, 3rd in lower left.
        firstPointOnList.setPosition( containmentRect.getMinX(), containmentRect.getMaxY() );
        LinkablePoint currentPoint = firstPointOnList.getNextPoint();
        currentPoint.setPosition( containmentRect.getMaxX(),
                                  containmentRect.getMaxY() );
        currentPoint = currentPoint.getNextPoint();
        currentPoint.setPosition( containmentRect.getMinX(),
                                  containmentRect.getMinY() );
        currentPoint = currentPoint.getNextPoint();
        currentPoint.setPosition( containmentRect.getMaxX(), containmentRect.getMinY() );
    }

    /**
     * Position by first placing points randomly in the rect, then running the
     * spring algorithm.  Hooke's law: F = -kx, where x is displacement from
     * equilibrium position.
     */
    private static void updatePointPositions01( final LinkablePoint firstPointOnList, Rectangle2D containmentRect, double dt ) {
        assert firstPointOnList != null;

        // Run an algorithm that treats each pair of points as though there
        // is a spring between them, but doesn't allow the first or last
        // points to be moved.
        LinkablePoint currentPoint = firstPointOnList;
        while ( currentPoint != null ) {
            currentPoint.clearVelocity();
            currentPoint = currentPoint.getNextPoint();
        }
        double springConstant = 1; // In Newtons/m
        double dampingConstant = 1;
        double pointMass = 0.1; // In kg.
        LinkablePoint previousPoint = firstPointOnList;
        currentPoint = firstPointOnList.getNextPoint();
        while ( currentPoint != null ) {
            if ( currentPoint.getNextPoint() != null ) {
                LinkablePoint nextPoint = currentPoint.getNextPoint();
                // This is not the last point on the list, so go ahead and
                // run the spring algorithm on it.
                // TODO: Check for performance and, if needed, all the memory allocations could be done once and reused.
                ImmutableVector2D vectorToPreviousPoint = new ImmutableVector2D( previousPoint.getPosition() ).getSubtractedInstance( new ImmutableVector2D( currentPoint.getPosition() ) );
                double scalarForceDueToPreviousPoint = ( -springConstant ) * ( currentPoint.targetDistanceToPreviousPoint - currentPoint.distance( previousPoint ) );
                ImmutableVector2D forceDueToPreviousPoint = vectorToPreviousPoint.getNormalizedInstance().getScaledInstance( scalarForceDueToPreviousPoint );
                ImmutableVector2D vectorToNextPoint = new ImmutableVector2D( nextPoint.getPosition() ).getSubtractedInstance( new ImmutableVector2D( currentPoint.getPosition() ) );
                double scalarForceDueToNextPoint = ( -springConstant ) * ( currentPoint.targetDistanceToPreviousPoint - currentPoint.distance( nextPoint ) );
                ImmutableVector2D forceDueToNextPoint = vectorToNextPoint.getNormalizedInstance().getScaledInstance( scalarForceDueToNextPoint );
                ImmutableVector2D dampingForce = currentPoint.getVelocity().getScaledInstance( -dampingConstant );
                ImmutableVector2D totalForce = forceDueToPreviousPoint.getAddedInstance( forceDueToNextPoint ).getAddedInstance( dampingForce );
                ImmutableVector2D acceleration = totalForce.getScaledInstance( 1 / pointMass );
                currentPoint.setAcceleration( acceleration );
                currentPoint.update( dt );
            }
            previousPoint = currentPoint;
            currentPoint = currentPoint.getNextPoint();
        }
    }

    /**
     * Takes link list of point and draws both points and lines between them.
     */
    private static class SquiggleNode extends PNode {
        private static final Stroke STROKE = new BasicStroke( 1 );

        private PNode lineLayer = new PNode();
        private PNode pointLayer = new PNode();
        private final LinkablePoint firstPointInList;
        private final ModelViewTransform mvt;

        private SquiggleNode( LinkablePoint firstPointInList, ModelViewTransform mvt ) {
            this.firstPointInList = firstPointInList;
            this.mvt = mvt;
            addChild( lineLayer );
            addChild( pointLayer );
            update();
        }

        public void update() {
            lineLayer.removeAllChildren();
            pointLayer.removeAllChildren();
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
        private final Vector2D velocity = new Vector2D( 0, 0 );
        private final Vector2D acceleration = new Vector2D( 0, 0 );

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

        public void clearVelocity() {
            velocity.setComponents( 0, 0 );
        }

        public ImmutableVector2D getVelocity() {
            return new ImmutableVector2D( velocity );
        }

        public void setAcceleration( ImmutableVector2D acceleration ) {
            this.acceleration.setValue( acceleration );
        }

        public void update( double deltaTime ) {
            velocity.setValue( velocity.getAddedInstance( acceleration.getScaledInstance( deltaTime ) ) );
            position.setLocation( position.getX() + velocity.getX() * deltaTime, position.getY() + velocity.getY() * deltaTime );
        }

        public double distance( LinkablePoint p ) {
            return this.getPosition().distance( p.getPosition() );
        }

        public void translate( ImmutableVector2D translationVector ) {
            setPosition( position.getX() + translationVector.getX(), position.getY() + translationVector.getY() );
        }
    }

    public void stepInTime( double dt ) {
        updatePointPositions01( firstShapeDefiningPoint, containmentRect, dt );
    }

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

        // Create an instance of this class.
        final TestMrnaWinding testMrnaWinding = new TestMrnaWinding();

        // Put the containment rect on the canvas.
        canvas.addWorldChild( new PhetPPath( mvt.modelToView( testMrnaWinding.containmentRect ), new BasicStroke( 1 ), Color.BLACK ) );

        // Add the line defined by the points.
        final SquiggleNode squiggleNode = new SquiggleNode( testMrnaWinding.firstShapeDefiningPoint, mvt );
        canvas.addWorldChild( squiggleNode );

        // Create a clock and hook it up.
        final ConstantDtClock clock = new ConstantDtClock( 30.0 );
//        final ConstantDtClock clock = new ConstantDtClock( 1.0 );
//        final ConstantDtClock clock = new ConstantDtClock( 1000, 30.0 );
        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                testMrnaWinding.stepInTime( clockEvent.getSimulationTimeChange() );
                squiggleNode.update();
            }
        } );
        clock.start();

        // Boiler plate app stuff.
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( (int) stageSize.getWidth(), (int) stageSize.getHeight() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null ); // Center.
        frame.setVisible( true );
    }
}
