// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.geneexpressionbasics.common.model.BiomoleculeShapeUtils;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.behaviorstates.DetachingState;
import edu.colorado.phet.geneexpressionbasics.common.model.behaviorstates.IdleState;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view.MobileBiomoleculeNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Class that represents messenger ribonucleic acid, or mRNA, in the model.
 *
 * @author John Blanco
 */
public class MessengerRna extends MobileBiomolecule {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    // Color used by this molecule.  Since mRNA is depicted as a line and not
    // as a closed shape, a transparent color is used.  This enables reuse of
    // generic biomolecule classes.
    private static final Color NOMINAL_COLOR = new Color( 0, 0, 0, 0 );

    // Minimum distance between points that define the shape.  This is done so
    // that this doesn't end up defined by so many points that the shape is
    // strange looking.
    private static final double MIN_DISTANCE_BETWEEN_POINTS = 50; // In picometers, empirically determined.

    // Random number generator used for creating "curviness" in the shape of
    // the RNA.
    private static final Random RAND = new Random();

    // Values to use when simulating a bunch of springs between the points in
    // order to get them to twist up but remain the same distance apart.
    private static final double SPRING_CONSTANT = .5; // In Newtons/meter
    private static final double TIME_SLICE = 0.1; // In seconds.

    // Max allowed inter-point-mass force, used to handle cases where the
    // points end up on top of each other, thus creating huge forces and
    // causing instabilities.  Empirically determined.
    private static final double MAX_INTER_POINT_FORCE = 10; // In Newtons.

    // Constant that governs the amount of force that pulls the points toward
    // the center point.
    private static final double FORCE_CONSTANT_FOR_MIDPOINT_ATTRACTION = 0.0001; // In Newtons.

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

//    private final List<Point2D> shapeDefiningPoints = new ArrayList<Point2D>();
//    private final List<Double> distanceToPreviousPoint = new ArrayList<Double>();

    // This vector controls the way in which the mRNA drifts as it grows.  Each
    // time growth occurs, the scaler that represents the amount of growth is
    // multiplied by this vector, and the vector is then applied to the
    // positions of all of the shape-defining points.  At the time of this
    // writing, there is no need to have anything other than one way of
    // drifting while growing, but this could easily be made settable or into
    // a constructor param.
//    private ImmutableVector2D driftWhileGrowingVector = new Vector2D( 0.15, 0.4 );

    // Rectangle in which all the points that define the mRNA strand should be
    // contained.  This is used to guide the algorithm that twists up the mRNA.
    // Note that sometimes - such as when the mRNA is being translated - that
    // this rectangle is ignored.
//    private Rectangle2D containingRect = new Rectangle2D.Double();

    private PointMass firstShapeDefiningPoint = null;
    private PointMass lastShapeDefiningPoint = null;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    /**
     * Constructor.  This creates the mRNA as a single point, with the intention
     * of growing it.
     *
     * @param position
     */
    public MessengerRna( GeneExpressionModel model, Point2D position ) {
        super( model, new DoubleGeneralPath( position ).getGeneralPath(), NOMINAL_COLOR );
        firstShapeDefiningPoint = new PointMass( position, 0 );
        lastShapeDefiningPoint = firstShapeDefiningPoint;
        // Explicitly set the state to idle, so that this won't move (other
        // than growing) until it is released.
        behaviorState = new IdleState( this );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    /**
     * Add a length to the mRNA from its current end point to the specified end
     * point.  This is usually done in small amounts, and is likely to look
     * weird if an attempt is made to grow to a distant point.
     */
    public void addLength( Point2D newEndPosition ) {

        if ( newEndPosition.distance( lastShapeDefiningPoint.getPosition() ) == 0 ) {
            // Don't bother adding redundant points.
            return;
        }

        // If the current last point is less than the min distance from the 2nd
        // to last point, remove the current last point.  This prevents having
        // zillions of shape-defining points, which is harder to work with.
        if ( lastShapeDefiningPoint != firstShapeDefiningPoint &&
             lastShapeDefiningPoint.distance( lastShapeDefiningPoint.getPreviousPointMass() ) < MIN_DISTANCE_BETWEEN_POINTS ) {
            // If the current last point is less than the min distance from
            // the 2nd to last point, remove the current last point.  This
            // prevents having zillions of shape-defining points, which is
            // harder to work with.
            PointMass secondToLastPoint = lastShapeDefiningPoint.getPreviousPointMass();
            secondToLastPoint.setNextPointMass( null );
            lastShapeDefiningPoint = secondToLastPoint;
        }

        // Add the new end point.
        System.out.println( "-------------------------------------" );
        System.out.println( "Distance from new point to previous end point = " + newEndPosition.distance( lastShapeDefiningPoint.getPosition() ) );
        PointMass newEndPoint = new PointMass( newEndPosition, newEndPosition.distance( lastShapeDefiningPoint.getPosition() ) );
        lastShapeDefiningPoint.setNextPointMass( newEndPoint );
        newEndPoint.setPreviousPointMass( lastShapeDefiningPoint );
        lastShapeDefiningPoint = newEndPoint;

        // Determine the length of the diagonal from the first point to this
        // new point.  The mRNA will be curled up between these two locations.
        double diagonalLength = 0;
        double currentUnfurledLength = getLength();
        System.out.println( "currentUnfurledLength = " + currentUnfurledLength );
        if ( currentUnfurledLength < MIN_DISTANCE_BETWEEN_POINTS ) {
            // Strand is pretty short, so don't start curling yet.  Let the
            // length be the distance between the straightened-out points.
            diagonalLength = firstShapeDefiningPoint.distance( lastShapeDefiningPoint );
        }
        else {
            // The diagonal length gets a little larger with added points, but
            // the longer it gets the more slowly the overall size grows.
//            diagonalLength = MIN_DISTANCE_BETWEEN_POINTS * ( Math.log( currentUnfurledLength / MIN_DISTANCE_BETWEEN_POINTS ) + 1 );
            diagonalLength = MIN_DISTANCE_BETWEEN_POINTS * ( Math.pow( currentUnfurledLength / MIN_DISTANCE_BETWEEN_POINTS, 0.5 ) );
        }
        System.out.println( "diagonalLength = " + diagonalLength );

        // Reposition the first point to be at the other end of the diagonal
        // vector from this new point.  This is fixed to be up and to the left,
        // but could be generalized if needed.
        ImmutableVector2D diagonalVector = new ImmutableVector2D( diagonalLength, 0 ).getRotatedInstance( Math.PI * 0.75 );
        firstShapeDefiningPoint.setPosition( newEndPosition.getX() + diagonalVector.getX(), newEndPosition.getY() + diagonalVector.getY() );

        // Determine the mid point between the two end points.
        Point2D centerOfGravityPoint = new Point2D.Double( ( firstShapeDefiningPoint.getPosition().getX() + lastShapeDefiningPoint.getPosition().getX() ) / 2,
                                                           ( firstShapeDefiningPoint.getPosition().getY() + lastShapeDefiningPoint.getPosition().getY() ) / 2 );

        // Reposition the middle shape-defining points to be "curled up"
        // between the first and last point.  This is done by simulating a
        // series of springs between the points.
        double maxForce = 0;
        double maxForce2 = 0;
        clearAllPointMassVelocities();
        if ( getPointCount() >= 3 ) {
            for ( int i = 0; i < 100; i++ ) {
                PointMass previousPoint = firstShapeDefiningPoint;
                PointMass currentPoint = firstShapeDefiningPoint.getNextPointMass();
                PointMass nextPoint = currentPoint.getNextPointMass();
                while ( previousPoint != null && currentPoint != null && nextPoint != null ) {
                    double targetDistanceToPreviousPoint = currentPoint.getTargetDistanceToPreviousPoint();
                    double targetDistanceToNextPoint = nextPoint.getTargetDistanceToPreviousPoint();

                    // Determine the force exerted by the previous point mass.
                    double forceDueToPreviousPoint = MathUtil.clamp( -MAX_INTER_POINT_FORCE,
                                                                     SPRING_CONSTANT * ( currentPoint.distance( previousPoint ) - targetDistanceToPreviousPoint ),
                                                                     MAX_INTER_POINT_FORCE );
                    if ( forceDueToPreviousPoint > maxForce ) {
                        maxForce = forceDueToPreviousPoint;
                        System.out.println( "maxForce = " + maxForce );
                    }
                    ImmutableVector2D forceVectorDueToPreviousPoint = new Vector2D( previousPoint.getPosition().getX() - currentPoint.getPosition().getX(),
                                                                                    previousPoint.getPosition().getY() - currentPoint.getPosition().getY() ).getNormalizedInstance().getScaledInstance( forceDueToPreviousPoint );
                    // Determine the force exerted by the next point mass.
                    double forceDueToNextPoint = MathUtil.clamp( -MAX_INTER_POINT_FORCE,
                                                                 SPRING_CONSTANT * ( currentPoint.distance( nextPoint ) - targetDistanceToNextPoint ),
                                                                 MAX_INTER_POINT_FORCE );
                    if ( forceDueToNextPoint > maxForce2 ) {
                        maxForce2 = forceDueToNextPoint;
                        System.out.println( "maxForce2 = " + maxForce2 );
                    }
                    ImmutableVector2D forceVectorDueToNextPoint = new Vector2D( nextPoint.getPosition().getX() - currentPoint.getPosition().getX(),
                                                                                nextPoint.getPosition().getY() - currentPoint.getPosition().getY() ).getNormalizedInstance().getScaledInstance( forceDueToNextPoint );

                    // Determine the force due to "gravity", i.e. the force
                    // that moves the points toward the center point between
                    // the two end points.  This actually gets stronger as one
                    // moves further away, so it isn't really quite like gravity.
                    double forceDueToGravity = currentPoint.getPosition().distance( centerOfGravityPoint ) * FORCE_CONSTANT_FOR_MIDPOINT_ATTRACTION;
                    ImmutableVector2D forceVectorDueToGravity = new Vector2D( centerOfGravityPoint.getX() - currentPoint.getPosition().getX(),
                                                                              centerOfGravityPoint.getY() - currentPoint.getPosition().getY() ).getNormalizedInstance().getScaledInstance( forceDueToGravity );

                    ImmutableVector2D totalForceVector = forceVectorDueToPreviousPoint.getAddedInstance( forceVectorDueToNextPoint ).getAddedInstance( forceVectorDueToGravity );
                    currentPoint.updateVelocity( totalForceVector, TIME_SLICE );
                    currentPoint.updatePosition( TIME_SLICE );
                    // Move down the chain to the next shape-defining point.
                    previousPoint = currentPoint;
                    currentPoint = nextPoint;
                    nextPoint = currentPoint.nextPointMass;
                }
            }
        }

        // Update the shape to reflect the newly added point.
        shapeProperty.set( BiomoleculeShapeUtils.createCurvyLineFromPoints( convertPointMassesToPointList() ) );
    }

    public void addLength( double x, double y ) {
        addLength( new Point2D.Double( x, y ) );
    }

    public void clearAllPointMassVelocities() {
        PointMass thisPoint = firstShapeDefiningPoint;
        while ( thisPoint != null ) {
            thisPoint.setVelocity( 0, 0 );
            thisPoint = thisPoint.getNextPointMass();
        }
    }

    private int getPointCount() {
        int pointCount = 0;
        PointMass thisPoint = firstShapeDefiningPoint;
        while ( thisPoint != null ) {
            pointCount++;
            thisPoint = thisPoint.getNextPointMass();
        }
        return pointCount;
    }

    private List<Point2D> convertPointMassesToPointList() {
        ArrayList<Point2D> pointList = new ArrayList<Point2D>();
        PointMass thisPoint = firstShapeDefiningPoint;
        while ( thisPoint != null ) {
            pointList.add( thisPoint.getPosition() );
            thisPoint = thisPoint.getNextPointMass();
        }
        return pointList;
    }

    /**
     * Add a length to the mRNA from its current end point to the specified end
     * point.  This is usually done in small amounts, and is likely to look
     * weird if an attempt is made to grow to a distant point.
     */
//    public void addLengthOld( Point2D p ) {
//        if ( shapeDefiningPoints.size() > 0 ) {
//            Point2D lastPoint = shapeDefiningPoints.get( shapeDefiningPoints.size() - 1 );
//            double growthAmount = lastPoint.distance( p );
//            // Cause all existing points to "drift" so that this doesn't just
//            // create a straight line.
//            ImmutableVector2D driftVector = driftWhileGrowingVector.getScaledInstance( growthAmount );
//            for ( Point2D point : shapeDefiningPoints ) {
//                point.setLocation( point.getX() + driftVector.getX(),
//                                   point.getY() + driftVector.getY() );
//            }
//            if ( shapeDefiningPoints.size() >= 2 ) {
//                // If the current last point is less than the min distance from
//                // the 2nd to last point, replace the current last point.  This
//                // prevents having zillions of shape-defining points, which is
//                // harder to work with.
//                Point2D secondToLastPoint = shapeDefiningPoints.get( shapeDefiningPoints.size() - 2 );
//                if ( lastPoint.distance( secondToLastPoint ) < MIN_DISTANCE_BETWEEN_POINTS ) {
//                    shapeDefiningPoints.remove( lastPoint );
//                }
//                else {
//                    // Add a random offset to the second-to-last point, which
//                    // is far enough away to keep.  This is done in order to
//                    // make the shape of the mRNA a bit curvy.
//                    ImmutableVector2D randomizationVector2 = new ImmutableVector2D( 1, 0 ).getScaledInstance( MIN_DISTANCE_BETWEEN_POINTS / 2 ).getRotatedInstance( RAND.nextDouble() * Math.PI * 2 );
//                    secondToLastPoint.setLocation( secondToLastPoint.getX() + driftVector.getX() + randomizationVector2.getX(),
//                                                   secondToLastPoint.getY() + driftVector.getY() + randomizationVector2.getY() );
//                }
//            }
//        }
//        // Add the new point.
//        shapeDefiningPoints.add( p );
//        // Update the shape to reflect the newly added point.
//        shapeProperty.set( BiomoleculeShapeUtils.createCurvyLineFromPoints( shapeDefiningPoints ) );
//    }
    public void release() {
        // Set the state to just be drifting around in the cytoplasm.
        behaviorState = new DetachingState( this, new ImmutableVector2D( 0, 1 ) );
    }

    /**
     * Get the length of the strand.  The length is calculated by adding up
     * the intended distances between the points, and does not account for
     * curvature.
     *
     * @return
     */
    private double getLength() {
        double length = 0;
        PointMass thisPoint = firstShapeDefiningPoint.getNextPointMass();
        while ( thisPoint != null ) {
            length += thisPoint.getTargetDistanceToPreviousPoint();
            thisPoint = thisPoint.getNextPointMass();
        }
        return length;
    }

    /**
     * Main routine that constructs a PhET Piccolo canvas in a window.
     *
     * @param args
     */
    public static void main( String[] args ) {

        Dimension2D stageSize = new PDimension( 500, 400 );

        PhetPCanvas canvas = new PhetPCanvas();
        // Set up the canvas-screen transform.
        canvas.setWorldTransformStrategy( new PhetPCanvas.CenteredStage( canvas, stageSize ) );

        ModelViewTransform mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( stageSize.getWidth() * 0.5 ), (int) Math.round( stageSize.getHeight() * 0.50 ) ),
                0.1 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        canvas.getLayer().addChild( new PhetPPath( new Rectangle2D.Double( -5, -5, 10, 10 ), Color.PINK ) );

        // Boiler plate app stuff.
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( (int) stageSize.getWidth(), (int) stageSize.getHeight() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null ); // Center.
        frame.setVisible( true );

        MessengerRna messengerRna = new MessengerRna( new ManualGeneExpressionModel(), mvt.modelToView( new Point2D.Double( 0, 0 ) ) );
        canvas.addWorldChild( new MobileBiomoleculeNode( mvt, messengerRna ) );
        for ( int i = 0; i < 200; i++ ) {
            messengerRna.addLength( mvt.modelToView( i * 100, 0 ) );
            try {
                Thread.sleep( 30 );
            }
            catch ( InterruptedException e ) {
                e.printStackTrace();
            }
            canvas.repaint();
        }
    }

    private static class PointMass {
        public static final double MASS = 0.25; // In kg.  Arbitrarily chosen to get the desired behavior.
        private final Point2D position = new Point2D.Double( 0, 0 );
        private final Vector2D velocity = new Vector2D( 0, 0 );
        private PointMass previousPointMass = null;
        private PointMass nextPointMass = null;

        private final double targetDistanceToPreviousPoint;

        private PointMass( Point2D initialPosition, double targetDistanceToPreviousPoint ) {
            setPosition( initialPosition );
            this.targetDistanceToPreviousPoint = targetDistanceToPreviousPoint;
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

        public void setVelocity( double x, double y ) {
            velocity.setComponents( x, y );
        }

        private void setVelocity( ImmutableVector2D velocity ) {
            setVelocity( velocity.getX(), velocity.getY() );
        }

        public ImmutableVector2D getVelocity() {
            return new ImmutableVector2D( velocity.getX(), velocity.getY() );
        }

        public PointMass getPreviousPointMass() {
            return previousPointMass;
        }

        public void setPreviousPointMass( PointMass previousPointMass ) {
            this.previousPointMass = previousPointMass;
        }

        public PointMass getNextPointMass() {
            return nextPointMass;
        }

        public void setNextPointMass( PointMass nextPointMass ) {
            this.nextPointMass = nextPointMass;
        }

        public double getTargetDistanceToPreviousPoint() {
            return targetDistanceToPreviousPoint;
        }

        public double distance( PointMass p ) {
            return this.getPosition().distance( p.getPosition() );
        }

        public void updateVelocity( ImmutableVector2D force, double time ) {
            setVelocity( velocity.plus( force.times( time / MASS ) ) );
        }

        public void updatePosition( double time ) {
            setPosition( position.getX() + velocity.getX() * time, position.getY() + velocity.getY() * time );
        }
    }
}
