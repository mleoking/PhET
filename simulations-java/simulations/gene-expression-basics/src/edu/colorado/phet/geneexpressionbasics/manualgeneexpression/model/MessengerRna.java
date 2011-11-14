// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.geneexpressionbasics.common.model.BiomoleculeShapeUtils;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.PlacementHint;
import edu.colorado.phet.geneexpressionbasics.common.model.behaviorstates.BeingTranslatedState;
import edu.colorado.phet.geneexpressionbasics.common.model.behaviorstates.DetachingState;
import edu.colorado.phet.geneexpressionbasics.common.model.behaviorstates.IdleState;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view.MessengerRnaNode;
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

    // Standard distance between points that define the shape.  This is done to
    // keep the number of points reasonable and make the shape-defining
    // algorithm consistent.
    private static final double INTER_POINT_DISTANCE = 50; // In picometers, empirically determined.

    // Length of the "leader segment", which is the portion of the mRNA that
    // sticks out on the upper left side so that a ribosome can be attached.
    private static final double LEADER_LENGTH = INTER_POINT_DISTANCE * 2;

    // Random number generator used for creating "curviness" in the shape of
    // the RNA.
    private static final Random RAND = new Random();

    // Values to use when simulating a bunch of springs between the points in
    // order to get them to twist up but remain the same distance apart.
    private static final double SPRING_CONSTANT = 200; // In Newtons/meter
    private static final double TIME_SLICE = 0.1; // In seconds.

    // Max allowed inter-point-mass force, used to handle cases where the
    // points end up on top of each other, thus creating huge forces and
    // causing instabilities.  Empirically determined.
    private static final double MAX_INTER_POINT_FORCE = 100; // In Newtons.

    // Constant that governs the amount of force that pushes points away from
    // the wall.
    private static final double WALL_FORCE_CONSTANT = 4; // In Newtons/m.

    // Max force that can be exerted by the wall.
    private static final double MAX_WALL_FORCE = 100; // In Newtons.

    // Min height for the containment rectangle.  Arbitrarily small so that
    // points can fit inside, but not much more.
    private static final double MIN_CONTAINMENT_RECT_HEIGHT = 1;

    // Factor to use to avoid issues with floating point resolution.
    private static final double FLOATING_POINT_COMP_FACTOR = 1E-7;

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private PointMass firstShapeDefiningPoint = null;
    private PointMass lastShapeDefiningPoint = null;

    public final PlacementHint ribosomePlacementHint;

    public final ObservableList<ShapeSegment> shapeSegments = new ObservableList<ShapeSegment>();

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    /**
     * Constructor.  This creates the mRNA as a single point, with the intention
     * of growing it.
     *
     * @param position
     */
    public MessengerRna( final GeneExpressionModel model, Point2D position ) {
        super( model, new DoubleGeneralPath( position ).getGeneralPath(), NOMINAL_COLOR );

        // Add first shape defining point to the point list.
        firstShapeDefiningPoint = new PointMass( position, 0 );
        lastShapeDefiningPoint = firstShapeDefiningPoint;

        // Add the first segment to the shape segment list.  This segment will
        // contain the "leader" for the mRNA.
        shapeSegments.add( new ShapeSegment.HorizontalSegment( position, 0 ) );

        // Explicitly set the state to idle, so that this won't move (other
        // than growing) until it is released.
        behaviorState = new IdleState( this );

        // Add the placement hint that will show the user where the ribosome
        // can be attached.
        ribosomePlacementHint = new PlacementHint( new Ribosome( model ) );
        shapeProperty.addObserver( new SimpleObserver() {
            public void update() {
                // This hint always sits at the beginning of the RNA strand.
                ImmutableVector2D currentMRnaFirstPointPosition = new ImmutableVector2D( firstShapeDefiningPoint.getPosition() );
                ImmutableVector2D offsetToTranslationChannelEntrance = ( new Ribosome( model ) ).getEntranceOfRnaChannelPos();
                ribosomePlacementHint.setPosition( currentMRnaFirstPointPosition.getSubtractedInstance( offsetToTranslationChannelEntrance ).toPoint2D() );
            }
        } );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    @Override public void translate( ImmutableVector2D translationVector ) {
        // Translate the current shape user the superclass facility.
        super.translate( translationVector );
        // Translate each of the shape segments that define the outline shape.
        for ( ShapeSegment shapeSegment : shapeSegments ) {
            shapeSegment.translate( translationVector );
        }
        // Translate each of the points that define the curly mRNA shape.
        PointMass thisPoint = firstShapeDefiningPoint;
        while ( thisPoint != null ) {
            thisPoint.translate( translationVector );
            thisPoint = thisPoint.getNextPointMass();
        }
    }

    /**
     * Add a length to the mRNA from its current end point to the specified end
     * point.  This is usually done in small amounts, and is likely to look
     * weird if an attempt is made to grow to a distant point.  As a length is
     * added, the mRNA shape "curls up".
     */
    public void addLengthRandomRepos( Point2D newEndPosition ) {

        if ( newEndPosition.distance( lastShapeDefiningPoint.getPosition() ) == 0 ) {
            // Don't bother adding redundant points.
            return;
        }
        System.out.println( "-------------------------------------" );
        System.out.println( "newEndPosition = " + newEndPosition );

        // If the current last point is less than the min distance from the 2nd
        // to last point, remove the current last point.  This prevents having
        // zillions of shape-defining points, which is harder to work with.
        // TODO: Maybe just take this out if it isn't needed.
        /*
        if ( lastShapeDefiningPoint != firstShapeDefiningPoint &&
             lastShapeDefiningPoint.targetDistanceToPreviousPoint < MIN_DISTANCE_BETWEEN_POINTS ) {
            // If the current last point is less than the min distance from
            // the 2nd to last point, remove the current last point.  This
            // prevents having zillions of shape-defining points, which is
            // harder to work with.
            PointMass secondToLastPoint = lastShapeDefiningPoint.getPreviousPointMass();
            secondToLastPoint.setNextPointMass( null );
            lastShapeDefiningPoint = secondToLastPoint;
        }
        */

        // Add the new end point.
        System.out.println( "Distance from new point to previous end point = " + newEndPosition.distance( lastShapeDefiningPoint.getPosition() ) );
        PointMass newEndPoint = new PointMass( newEndPosition, newEndPosition.distance( lastShapeDefiningPoint.getPosition() ) );
        lastShapeDefiningPoint.setNextPointMass( newEndPoint );
        newEndPoint.setPreviousPointMass( lastShapeDefiningPoint );
        lastShapeDefiningPoint = newEndPoint;

        double currentUnfurledLength = getLength();
        System.out.println( "currentUnfurledLength = " + currentUnfurledLength );
        System.out.println( "number of points = " + getPointCount() );

        // Create the containment rect that will contain the curled up portion
        // of the strand.
        Rectangle2D woundUpRect = new Rectangle2D.Double( newEndPosition.getX() - currentUnfurledLength,
                                                          newEndPosition.getY() - MIN_CONTAINMENT_RECT_HEIGHT / 2,
                                                          1E-6,
                                                          1E-6 );
        if ( currentUnfurledLength > LEADER_LENGTH ) {
            // There are enough points to start winding up the mRNA.  Set the
            // size of the rectangle that contains the wound up portion to a
            // value that is shorter than the unfurled length.
            double diagonalLength = INTER_POINT_DISTANCE * ( Math.pow( currentUnfurledLength / INTER_POINT_DISTANCE, 0.5 ) );

            double squareSideLength = diagonalLength * Math.cos( Math.PI / 4 );
            woundUpRect = new Rectangle2D.Double( newEndPosition.getX() - squareSideLength,
                                                  newEndPosition.getY(),
                                                  squareSideLength,
                                                  squareSideLength );
        }

        System.out.println( "woundUpRect = " + woundUpRect );

        // If there are enough points, move the stick-out region to the upper
        // left corner of the strand.
        PointMass firstEnclosedPoint = getFirstEnclosedPoint();
        if ( firstEnclosedPoint != null ) {
            firstShapeDefiningPoint.setPosition( woundUpRect.getX() - LEADER_LENGTH, woundUpRect.getMaxY() );
            PointMass previousPoint = firstShapeDefiningPoint;
            PointMass thisPoint = firstShapeDefiningPoint.nextPointMass;
            while ( thisPoint != firstEnclosedPoint.getNextPointMass() ) {
                thisPoint.setPosition( previousPoint.getPosition().getX() + thisPoint.getTargetDistanceToPreviousPoint(), previousPoint.getPosition().getY() );
                previousPoint = thisPoint;
                thisPoint = thisPoint.getNextPointMass();
            }
        }

        // Position each of the points at the correct distance from the
        // previous point but at a random angle.  Any angle is valid as long as
        // the point ends up inside the containment rectangle.
        Random rand = new Random( 1 ); // Use a consistent random for predictable results.
        if ( firstEnclosedPoint != null && firstEnclosedPoint != lastShapeDefiningPoint ) {
            PointMass previousPoint = getFirstEnclosedPoint();
            PointMass thisPoint = previousPoint.getNextPointMass();
            while ( thisPoint != null && thisPoint != lastShapeDefiningPoint ) {
                Vector2D offsetFromPreviousPoint = new Vector2D( thisPoint.getTargetDistanceToPreviousPoint(), 0 );
                // Try a finite number of times to find a point within the bounding square.
                for ( int i = 0; i < 10; i++ ) {
                    offsetFromPreviousPoint.rotate( rand.nextDouble() * Math.PI * 2 );
                    if ( woundUpRect.contains( new ImmutableVector2D( previousPoint.getPosition() ).getAddedInstance( offsetFromPreviousPoint ).toPoint2D() ) ) {
                        break;
                    }
                }
                thisPoint.setPosition( new ImmutableVector2D( previousPoint.getPosition() ).getAddedInstance( offsetFromPreviousPoint ).toPoint2D() );
                previousPoint = thisPoint;
                thisPoint = thisPoint.getNextPointMass();
            }
            // Start at the end and move points towards the end point so that
            // all points are at the correct target distance.  This effectively
            // stretches out the curled up shape to be in the desired space.
//            thisPoint = lastShapeDefiningPoint;
//            previousPoint = thisPoint.getPreviousPointMass();
//            while ( previousPoint != null && previousPoint != firstEnclosedPoint ) {
//                ImmutableVector2D vectorToPreviousPoint = new ImmutableVector2D( previousPoint.getPosition().getX() - thisPoint.getPosition().getX(),
//                                                                                 previousPoint.getPosition().getY() - thisPoint.getPosition().getY() );
//                ImmutableVector2D vectorToTargetPreviousPointLocation = new ImmutableVector2D( thisPoint.targetDistanceToPreviousPoint, 0 ).getRotatedInstance( vectorToPreviousPoint.getAngle() );
//                previousPoint.setPosition( thisPoint.getPosition().getX() + vectorToTargetPreviousPointLocation.getX(),
//                                           thisPoint.getPosition().getY() + vectorToTargetPreviousPointLocation.getY() );
//                previousPoint = previousPoint.getPreviousPointMass();
//                thisPoint = thisPoint.getPreviousPointMass();
//            }
        }

//        System.out.println( "Dumping points: " );
//        dumpPointMasses();

        // Update the shape to reflect the newly added point.
        System.out.println( "firstShapeDefiningPoint = " + firstShapeDefiningPoint );
        System.out.println( "Done with addLength" );
        shapeProperty.set( BiomoleculeShapeUtils.createCurvyLineFromPoints( getPointList() ) );
    }

    /**
     * Advance the translation of the mRNA through the given ribosome by the
     * specified length.  The given ribosome must already be attached to the
     * mRNA.
     *
     * @param ribosome - The ribosome by which the mRNA is being translated.
     * @param length   - The amount of mRNA to move through the translation channel.
     * @return True if the mRNA is complete through the channel, false if not.
     *         Generally, this will be called a number of times returning "false" and
     *         then, the last time, "true" will be returned.
     */
    public boolean advanceTranslation( Ribosome ribosome, double length ) {
        return true;

    }

    /**
     * Add the specified amount of mRNA length to the tail end of the mRNA.
     * Adding a length will cause the winding algorithm to be re-run.
     *
     * @param length - Length of mRNA to add in picometers.
     */
    public void addLength( double length ) {

        // At least for now, the mRNA can't be grown by more than the inter-
        // point distance at one time.  This is a simplifying assumption that
        // can be changed if necessary.
        if ( length > INTER_POINT_DISTANCE ) {
            System.out.println( "Big growth!" );
        }
        assert length <= INTER_POINT_DISTANCE;

        if ( firstShapeDefiningPoint == lastShapeDefiningPoint ) {
            // This is the first length added to the strand.
            addPointToEnd( lastShapeDefiningPoint.getPosition(), length );
            assert getLastShapeSegment().isFlat(); // Should be creating the leader at this point.
            // Grow the leader segment to accommodate the additional length.
            getLastShapeSegment().growLeft( length );
            // Nothing else to do.
            return;
        }
        else if ( lastShapeDefiningPoint.getTargetDistanceToPreviousPoint() < INTER_POINT_DISTANCE ) {
            double prevDistance = lastShapeDefiningPoint.getTargetDistanceToPreviousPoint();
            if ( prevDistance + length <= INTER_POINT_DISTANCE ) {
                // No need to add a new point - just set the distance of the
                // current last point to be further away from the previous.
                lastShapeDefiningPoint.setTargetDistanceToPreviousPoint( prevDistance + length );
            }
            else {
                // Set the last point to be at the prescribed inter-point
                // distance, and then add a new point.
                lastShapeDefiningPoint.setTargetDistanceToPreviousPoint( INTER_POINT_DISTANCE );
                assert length - ( INTER_POINT_DISTANCE - prevDistance ) > 0; // If this fires, this code is wrong and needs to be fixed.
                addPointToEnd( lastShapeDefiningPoint.getPosition(), length - ( INTER_POINT_DISTANCE - prevDistance ) );
            }
        }
        else {
            // Just add a new point to the end.
            addPointToEnd( lastShapeDefiningPoint.getPosition(), length );
        }

        if ( getLastShapeSegment().isFlat() ) {
            // The leader portion is still being constructed.  Grow it to
            // accommodate the new length.
            ShapeSegment lastShapeSegment = getLastShapeSegment();
            assert lastShapeSegment.getLength() <= LEADER_LENGTH;
            if ( lastShapeSegment.getLength() + length <= LEADER_LENGTH ) {
                // Just grow the leader segment.
                lastShapeSegment.growLeft( length );
            }
            else {
                // Time to max out the leader segment and add a diagonal
                // segment where the mRNA can curl up.
                lastShapeSegment.growLeft( LEADER_LENGTH - lastShapeSegment.getLength() );
                assert getLength() > LEADER_LENGTH; // If this fires, this code is wrong and needs to be fixed.
                shapeSegments.add( new ShapeSegment.DiagonalSegment( lastShapeSegment.getLowerRightCornerPos(), getLength() - LEADER_LENGTH ) );
            }
        }
        else {
            // The leader segment is full and the winding segment is growing.
            // Set its size as a function of the current length that is
            // contained within it.
            double currentDiagonalLength = getLastShapeSegment().getLength();
            double desiredDiagonalLength = INTER_POINT_DISTANCE * ( Math.pow( ( getLength() - LEADER_LENGTH ) / INTER_POINT_DISTANCE, 0.5 ) );
            assert desiredDiagonalLength > currentDiagonalLength; // If this fires, something is wrong with this algorithm.
            getLastShapeSegment().growLeft( desiredDiagonalLength - currentDiagonalLength );
        }

        // Realign the segments, since some growth probably occurred.
        realignSegmentsFromEnd();

        // Now that the length has been added, rerun the winding algorithm.
        windPointsThroughSegments();
    }

    /**
     * Set the position of the lower right end of the mRNA strand.
     *
     * @param p
     */
    public void setLowerRightPosition( Point2D p ) {
        getLastShapeSegment().setLowerRightCornerPos( p );
        realignSegmentsFromEnd();
    }

    /**
     * This is the "winding algorithm" that positions the points within the
     * shape segments in order to look like a wound up piece of mRNA.  The
     * combination of this algorithm and the shape segments allow the mRNA to
     * look reasonable when it is being synthesized and when it is being
     * transcribed.
     */
    private void windPointsThroughSegments() {
        assert shapeSegments.size() > 0;
        ShapeSegment currentShapeSegment = shapeSegments.get( 0 );
        // TODO: This isn't "real" yet - it's just something that is good enough to start testing.
        Point2D leaderOrigin = currentShapeSegment.getUpperLeftCornerPos();
        PointMass currentPoint = firstShapeDefiningPoint;
        for ( double leaderLengthSoFar = 0;
              leaderLengthSoFar <= currentShapeSegment.getLength() + FLOATING_POINT_COMP_FACTOR && currentPoint != null;
              leaderLengthSoFar += ( currentPoint == null ? 0 : currentPoint.getTargetDistanceToPreviousPoint() ) ) {

            currentPoint.setPosition( leaderOrigin.getX() + leaderLengthSoFar, leaderOrigin.getY() );
            currentPoint = currentPoint.getNextPointMass();
        }
        if ( currentPoint != null ) {
            if ( shapeSegments.size() < 2 ) {
                // Shouldn't happen that we have more points but no segment in which to position them.
                assert false;
            }
            currentShapeSegment = shapeSegments.get( 1 );
            Rectangle2D boundsForSegment = currentShapeSegment.getBounds();
            randomizePointPositionsInRectangle( currentPoint, lastShapeDefiningPoint, boundsForSegment );
            runSpringAlgorithm( currentPoint, lastShapeDefiningPoint, boundsForSegment );
        }

        // Update the shape.
        shapeProperty.set( BiomoleculeShapeUtils.createCurvyLineFromPoints( getPointList() ) );
    }

    /**
     * Randomly position a set of points inside a rectangle.  The first point
     * is positioned at the upper left, the last at the lower right, and all
     * points in between are randomly placed.
     *
     * @param firstPoint
     * @param lastPoint
     */
    private void randomizePointPositionsInRectangle( PointMass firstPoint, PointMass lastPoint, Rectangle2D bounds ) {
        assert bounds.getHeight() != 0; // Only segments with some space in them should be used.
        assert firstPoint != null;
        if ( firstPoint == null ) {
            // Defensive programming.
            return;
        }

        // Position the first point at the upper left.
        firstPoint.setPosition( bounds.getMinX(), bounds.getMinY() + bounds.getHeight() );
        if ( firstPoint == lastPoint ) {
            // Nothing more to do.
            return;
        }

        // Position the last point at the lower right.
        lastPoint.setPosition( bounds.getMaxX(), bounds.getMinY() );

        // TODO: For now, this algorithm uses a random number generator with a
        // fixed seed in order to keep it from causing things to jump around
        // to much as more length is added.  At some point, it may make sense
        // to associate a seed with each segment and pass it in to this
        // algorithm, so that a single mRNA winds consistently, but individual
        // strands look different.
        Random rand = new Random( 1 );
        PointMass currentPoint = firstPoint;
        do {
            // Randomly position the points within the segment.
            currentPoint.setPosition( bounds.getMinX() + rand.nextDouble() * bounds.getWidth(),
                                      bounds.getMinY() + rand.nextDouble() * bounds.getHeight() );
            currentPoint = currentPoint.getNextPointMass();
        } while ( currentPoint != lastPoint && currentPoint != null );
    }

    /**
     * Position a set of points within a rectangle.  The first point stays at the
     * upper left, the last point stays at the lower right, and the points in
     * between are initially positioned randomly, then a spring algorithm is
     * run to position them such that each point is the appropriate distance
     * from the previous and next points.
     *
     * @param firstPoint
     * @param lastPoint
     * @param bounds
     */
    private static void runSpringAlgorithm( final PointMass firstPoint, final PointMass lastPoint, Rectangle2D bounds ) {
        assert firstPoint != null;
        if ( firstPoint == null ) {
            // Defensive programming.
            return;
        }

        // Position the first point at the upper left.
        firstPoint.setPosition( bounds.getMinX(), bounds.getMinY() + bounds.getHeight() );
        if ( firstPoint == lastPoint ) {
            // Nothing more to do.
            return;
        }

        // Position the last point at the lower right.
        lastPoint.setPosition( bounds.getMaxX(), bounds.getMinY() );

        // Run an algorithm that treats each pair of points as though there
        // is a spring between them, but doesn't allow the first or last
        // points to be moved.
        PointMass currentPoint = firstPoint;
        while ( currentPoint != null ) {
            currentPoint.clearVelocity();
            currentPoint = currentPoint.getNextPointMass();
        }
        double springConstant = 2; // In Newtons/m
        double dampingConstant = 1;
        double pointMass = PointMass.MASS;
        double dt = 0.025; // In seconds.
        int numUpdates = 20;
        for ( int i = 0; i < numUpdates; i++ ) {
            PointMass previousPoint = firstPoint;
            currentPoint = firstPoint.getNextPointMass();
            while ( currentPoint != null ) {
                if ( currentPoint.getNextPointMass() != null ) {
                    PointMass nextPoint = currentPoint.getNextPointMass();
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
                currentPoint = currentPoint.getNextPointMass();
            }
        }
    }

    /**
     * Count the number of points between two points on a list.  Endpoints are
     * included in the total.
     *
     * @param firstPoint
     * @param lastPoint
     * @return
     */
    private int countPoints( PointMass firstPoint, PointMass lastPoint ) {
        int count = 1;
        PointMass currentPoint = firstPoint;
        while ( currentPoint != lastPoint && currentPoint != null ) {
            count++;
            currentPoint = currentPoint.getNextPointMass();
        }
        if ( currentPoint != null ) {
            System.out.println( getClass().getName() + " Warning: End point never encountered when counting points." );
            return Integer.MAX_VALUE;
        }
        return count;
    }

    /**
     * Realign all the segments, making sure that the end of one connects to
     * the beginning of another, using the last segment on the list as the
     * starting point.
     */
    private void realignSegmentsFromEnd() {
        ArrayList<ShapeSegment> copyOfShapeSegments = new ArrayList<ShapeSegment>( shapeSegments );
        Collections.reverse( copyOfShapeSegments );
        for ( int i = 0; i < copyOfShapeSegments.size() - 1; i++ ) {
            // Assumes that the shape segments attach to one another in such
            // a way that they chain from the upper left to the lower right.
            copyOfShapeSegments.get( i + 1 ).setLowerRightCornerPos( copyOfShapeSegments.get( i ).getUpperLeftCornerPos() );
        }
    }

    private ShapeSegment getLastShapeSegment() {
        return shapeSegments.get( shapeSegments.size() - 1 );
    }

    /**
     * Add a point to the end of the list of shape defining points.  Note that
     * this will alter the last point on the list.
     *
     * @param position
     * @param targetDistanceToPreviousPoint
     */
    private void addPointToEnd( Point2D position, double targetDistanceToPreviousPoint ) {
        PointMass newPoint = new PointMass( position, targetDistanceToPreviousPoint );
        lastShapeDefiningPoint.nextPointMass = newPoint;
        newPoint.previousPointMass = lastShapeDefiningPoint;
        lastShapeDefiningPoint = newPoint;
    }

    /**
     * Add a length to the mRNA from its current end point to the specified end
     * point.  This is usually done in small amounts, and is likely to look
     * weird if an attempt is made to grow to a distant point.  As a length is
     * added, the mRNA shape "curls up".
     */
    public void addLengthOld( Point2D newEndPosition ) {

        if ( newEndPosition.distance( lastShapeDefiningPoint.getPosition() ) == 0 ) {
            // Don't bother adding redundant points.
            return;
        }
        System.out.println( "-------------------------------------" );
        System.out.println( "newEndPosition = " + newEndPosition );

        // If the current last point is less than the min distance from the 2nd
        // to last point, remove the current last point.  This prevents having
        // zillions of shape-defining points, which is harder to work with.
        // TODO: Maybe just take this out if it isn't needed.
//        if ( lastShapeDefiningPoint != firstShapeDefiningPoint &&
//             lastShapeDefiningPoint.targetDistanceToPreviousPoint < MIN_DISTANCE_BETWEEN_POINTS ) {
//            // If the current last point is less than the min distance from
//            // the 2nd to last point, remove the current last point.  This
//            // prevents having zillions of shape-defining points, which is
//            // harder to work with.
//            PointMass secondToLastPoint = lastShapeDefiningPoint.getPreviousPointMass();
//            secondToLastPoint.setNextPointMass( null );
//            lastShapeDefiningPoint = secondToLastPoint;
//        }

        // Add the new end point.
        System.out.println( "Distance from new point to previous end point = " + newEndPosition.distance( lastShapeDefiningPoint.getPosition() ) );
        PointMass newEndPoint = new PointMass( newEndPosition, newEndPosition.distance( lastShapeDefiningPoint.getPosition() ) );
        lastShapeDefiningPoint.setNextPointMass( newEndPoint );
        newEndPoint.setPreviousPointMass( lastShapeDefiningPoint );
        lastShapeDefiningPoint = newEndPoint;

        double currentUnfurledLength = getLength();
        System.out.println( "currentUnfurledLength = " + currentUnfurledLength );
        System.out.println( "number of points = " + getPointCount() );

        // Create the containment rect that will contain the curled up portion
        // of the strand.
        Rectangle2D woundUpRect = new Rectangle2D.Double( newEndPosition.getX() - currentUnfurledLength,
                                                          newEndPosition.getY() - MIN_CONTAINMENT_RECT_HEIGHT / 2,
                                                          1E-6,
                                                          1E-6 );
        if ( currentUnfurledLength > LEADER_LENGTH ) {
            // There are enough points to start winding up the mRNA.  Set the
            // size of the rectangle that contains the wound up portion to a
            // value that is shorter than the unfurled length.
            double diagonalLength = INTER_POINT_DISTANCE * ( Math.pow( currentUnfurledLength / INTER_POINT_DISTANCE, 0.5 ) );

            double squareSideLength = diagonalLength * Math.cos( Math.PI / 4 );
            woundUpRect = new Rectangle2D.Double( newEndPosition.getX() - squareSideLength,
                                                  newEndPosition.getY(),
                                                  squareSideLength,
                                                  squareSideLength );
        }

        System.out.println( "woundUpRect = " + woundUpRect );

        // If there are enough points, move the stick-out region to the upper
        // left corner of the strand.
        PointMass firstEnclosedPoint = getFirstEnclosedPoint();
        if ( firstEnclosedPoint != null ) {
            firstShapeDefiningPoint.setPosition( woundUpRect.getX() - LEADER_LENGTH, woundUpRect.getMaxY() );
            PointMass previousPoint = firstShapeDefiningPoint;
            PointMass thisPoint = firstShapeDefiningPoint.nextPointMass;
            while ( thisPoint != firstEnclosedPoint.getNextPointMass() ) {
                thisPoint.setPosition( previousPoint.getPosition().getX() + thisPoint.getTargetDistanceToPreviousPoint(), previousPoint.getPosition().getY() );
                previousPoint = thisPoint;
                thisPoint = thisPoint.getNextPointMass();
            }
        }

        // Position each of the points within the wound up region along a
        // diagonal line from the first enclosed point to the last point (which
        // is the one that was just added).
        if ( firstEnclosedPoint != null ) {
            ImmutableVector2D interPointVector = new ImmutableVector2D( firstEnclosedPoint.distance( newEndPoint ) / getNumEnclosedPoints(), 0 ).getRotatedInstance( Math.atan2( newEndPoint.getPosition().getY() - firstEnclosedPoint.getPosition().getY(),
                                                                                                                                                                                 newEndPoint.getPosition().getX() - firstEnclosedPoint.getPosition().getX() ) );
            PointMass previousPoint = getFirstEnclosedPoint();
            PointMass thisPoint = previousPoint.getNextPointMass();
            while ( thisPoint != null && thisPoint != lastShapeDefiningPoint ) {
                thisPoint.setPosition( previousPoint.getPosition().getX() + interPointVector.getX(), previousPoint.getPosition().getY() + interPointVector.getY() );
                previousPoint = thisPoint;
                thisPoint = thisPoint.getNextPointMass();
            }
        }

        // TODO: Perturb some of the points.
        // Perturb some of the points so that the spring algorithm - used
        // later - can do its thing.
        if ( firstEnclosedPoint != null ) {
            Random perturbationRand = new Random( 1 );
            ImmutableVector2D interPointVector = new ImmutableVector2D( firstEnclosedPoint.distance( newEndPoint ) / getNumEnclosedPoints(), 0 ).getRotatedInstance( Math.atan2( newEndPoint.getPosition().getY() - firstEnclosedPoint.getPosition().getY(),
                                                                                                                                                                                 newEndPoint.getPosition().getX() - firstEnclosedPoint.getPosition().getX() ) );
            PointMass previousPoint = getFirstEnclosedPoint();
            PointMass thisPoint = previousPoint.getNextPointMass();
            while ( thisPoint != null && thisPoint != lastShapeDefiningPoint ) {
                if ( perturbationRand.nextDouble() > 0.9 ) {
                    ImmutableVector2D perturbationVector = interPointVector.getRotatedInstance( Math.PI / 2 * ( perturbationRand.nextBoolean() ? 1 : -1 ) ).getScaledInstance( 5 );
                    thisPoint.setPosition( thisPoint.getPosition().getX() + perturbationVector.getX(),
                                           thisPoint.getPosition().getY() + perturbationVector.getY() );
                }
                previousPoint = thisPoint;
                thisPoint = thisPoint.getNextPointMass();
            }
        }

        // TODO: Run the spring spacing algorithm.
        // Position the points in the "enclosed region" so that the mRNA looks
        // like it is wound up.
        if ( firstEnclosedPoint != null && firstEnclosedPoint.getNextPointMass() != null && firstEnclosedPoint.getNextPointMass().getNextPointMass() != null ) {
            for ( int i = 0; i < 100; i++ ) {
                PointMass previousPoint = firstEnclosedPoint;
                PointMass currentPoint = previousPoint.getNextPointMass();
                PointMass nextPoint = currentPoint.getNextPointMass();
                double maxForce = 0;
                double maxForce2 = 0;
                clearAllPointMassVelocities();
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

                    // Determine the force exerted by the walls of the containment rectangle.
                    double forceXDueToWalls;
                    if ( currentPoint.getPosition().getX() < woundUpRect.getMinX() ) {
                        forceXDueToWalls = MAX_WALL_FORCE;
                    }
                    else if ( currentPoint.getPosition().getX() < woundUpRect.getMinX() ) {
                        forceXDueToWalls = -MAX_WALL_FORCE;
                    }
                    else {
                        forceXDueToWalls = WALL_FORCE_CONSTANT / Math.pow( currentPoint.getPosition().getX() - woundUpRect.getX(), 2 ) -
                                           WALL_FORCE_CONSTANT / Math.pow( woundUpRect.getMaxX() - currentPoint.getPosition().getX(), 2 );
                        forceXDueToWalls = MathUtil.clamp( -MAX_WALL_FORCE, forceXDueToWalls, MAX_WALL_FORCE );
                    }
                    double forceYDueToWalls;
                    if ( currentPoint.getPosition().getY() < woundUpRect.getMinY() ) {
                        forceYDueToWalls = MAX_WALL_FORCE;
                    }
                    else if ( currentPoint.getPosition().getY() < woundUpRect.getMinY() ) {
                        forceYDueToWalls = -MAX_WALL_FORCE;
                    }
                    else {
                        forceYDueToWalls = WALL_FORCE_CONSTANT / Math.pow( currentPoint.getPosition().getY() - woundUpRect.getY(), 2 ) -
                                           WALL_FORCE_CONSTANT / Math.pow( woundUpRect.getMaxY() - currentPoint.getPosition().getY(), 2 );
                        forceYDueToWalls = MathUtil.clamp( -MAX_WALL_FORCE, forceYDueToWalls, MAX_WALL_FORCE );
                    }
                    ImmutableVector2D forceVectorDueToWalls = new ImmutableVector2D( forceXDueToWalls, forceYDueToWalls );
//                    System.out.println( "forceVectorDueToWalls = " + forceVectorDueToWalls );

                    ImmutableVector2D totalForceVector = forceVectorDueToPreviousPoint.getAddedInstance( forceVectorDueToNextPoint ).getAddedInstance( forceVectorDueToWalls );
                    currentPoint.updateVelocity( totalForceVector, TIME_SLICE );
                    currentPoint.updatePosition( TIME_SLICE );

                    // Move down the chain to the next shape-defining point.
                    previousPoint = currentPoint;
                    currentPoint = nextPoint;
                    nextPoint = currentPoint.nextPointMass;
                }
            }
        }


//        System.out.println( "Dumping points: " );
//        dumpPointMasses();

        // Update the shape to reflect the newly added point.
        shapeProperty.set( BiomoleculeShapeUtils.createCurvyLineFromPoints( getPointList() ) );
    }

    private int getNumEnclosedPoints() {
        int numEnclosedPoints = 0;
        PointMass currentPoint = getFirstEnclosedPoint();
        while ( currentPoint != null ) {
            numEnclosedPoints++;
            currentPoint = currentPoint.getNextPointMass();
        }
        return numEnclosedPoints;
    }

    public void addLengthRandomize( Point2D newEndPosition ) {

        if ( newEndPosition.distance( lastShapeDefiningPoint.getPosition() ) == 0 ) {
            // Don't bother adding redundant points.
            return;
        }
        System.out.println( "-------------------------------------" );
        System.out.println( "newEndPosition = " + newEndPosition );

        // Create a vector that represents the distance between the current
        // end point and this new one.  This will be used to figure out how to
        // move the existing points to "drag them along" with this new one".
        ImmutableVector2D distanceAddedByNewPoint = new ImmutableVector2D( lastShapeDefiningPoint.getPosition().getX() - newEndPosition.getX(),
                                                                           lastShapeDefiningPoint.getPosition().getY() - newEndPosition.getY() );

        // If the current last point is less than the min distance from the 2nd
        // to last point, remove the current last point.  This prevents having
        // zillions of shape-defining points, which is harder to work with.
        if ( lastShapeDefiningPoint != firstShapeDefiningPoint &&
             lastShapeDefiningPoint.targetDistanceToPreviousPoint < INTER_POINT_DISTANCE ) {
            // If the current last point is less than the min distance from
            // the 2nd to last point, remove the current last point.  This
            // prevents having zillions of shape-defining points, which is
            // harder to work with.
            PointMass secondToLastPoint = lastShapeDefiningPoint.getPreviousPointMass();
            secondToLastPoint.setNextPointMass( null );
            lastShapeDefiningPoint = secondToLastPoint;
        }

        // Since the strand is expected to grow up and to the left, create a
        // vector that moves the existing points this way in an amount that is
        // proportional to the newly added segment.
//        ImmutableVector2D collectiveMotionVector = new ImmutableVector2D( distanceAddedByNewPoint.getMagnitude() * 0.5,
//                                                                          distanceAddedByNewPoint.getMagnitude() * 0.25 );

        // Move the existing points.
//        moveAllPoints( collectiveMotionVector );


        // Add the new end point.
        System.out.println( "Distance from new point to previous end point = " + newEndPosition.distance( lastShapeDefiningPoint.getPosition() ) );
        PointMass newEndPoint = new PointMass( newEndPosition, newEndPosition.distance( lastShapeDefiningPoint.getPosition() ) );
        lastShapeDefiningPoint.setNextPointMass( newEndPoint );
        newEndPoint.setPreviousPointMass( lastShapeDefiningPoint );
        lastShapeDefiningPoint = newEndPoint;

        double currentUnfurledLength = getLength();
        System.out.println( "currentUnfurledLength = " + currentUnfurledLength );
        System.out.println( "number of points = " + getPointCount() );

        // Create the containment rect, and specify it initially to contain
        // only the stick out portion.
        Rectangle2D containmentRect = new Rectangle2D.Double( newEndPosition.getX() - currentUnfurledLength,
                                                              newEndPosition.getY() - MIN_CONTAINMENT_RECT_HEIGHT / 2,
                                                              currentUnfurledLength,
                                                              MIN_CONTAINMENT_RECT_HEIGHT );
        if ( currentUnfurledLength > LEADER_LENGTH ) {
            // There are enough points to start winding up the mRNA.  Create a
            // rectangle that will define the area where the strand must
            // within (except for the stick-out area).
            double diagonalLength = INTER_POINT_DISTANCE * ( Math.pow( currentUnfurledLength / INTER_POINT_DISTANCE, 0.5 ) );
            double squareSideLength = diagonalLength * Math.cos( Math.PI / 4 );
            containmentRect = new Rectangle2D.Double( newEndPosition.getX() - squareSideLength,
                                                      newEndPosition.getY(),
                                                      squareSideLength,
                                                      squareSideLength );
        }

        System.out.println( "containmentRect = " + containmentRect );

        // If there are enough points, move the stick-out region to the upper
        // left corner of the strand.
        PointMass firstEnclosedPoint = getFirstEnclosedPoint();
        if ( firstEnclosedPoint != null ) {
            firstShapeDefiningPoint.setPosition( containmentRect.getX() - LEADER_LENGTH, containmentRect.getMaxY() );
            PointMass previousPoint = firstShapeDefiningPoint;
            PointMass thisPoint = firstShapeDefiningPoint.nextPointMass;
            while ( thisPoint != firstEnclosedPoint.getNextPointMass() ) {
                thisPoint.setPosition( previousPoint.getPosition().getX() + thisPoint.getTargetDistanceToPreviousPoint(), previousPoint.getPosition().getY() );
                previousPoint = thisPoint;
                thisPoint = thisPoint.getNextPointMass();
            }
        }

        // TODO: Keep this for testing.  It is an alternative positioning algorithm in which the enlcosed points are positioned randomly within the rectangle.
        // Position the points in the "enclosed region" so that the mRNA looks
        // like it is wound up.
        if ( firstEnclosedPoint != null ) {
            PointMass previousPoint = getFirstEnclosedPoint();
            PointMass thisPoint = previousPoint.getNextPointMass();
            while ( thisPoint != null && thisPoint != lastShapeDefiningPoint ) {
                thisPoint.setPosition( containmentRect.getMinX() + RAND.nextDouble() * containmentRect.getWidth(),
                                       containmentRect.getMinY() + RAND.nextDouble() * containmentRect.getHeight() );
                thisPoint = thisPoint.getNextPointMass();
            }
        }

        // Position the points in the "enclosed region" so that the mRNA looks
        // like it is wound up.
        if ( firstEnclosedPoint != null && firstEnclosedPoint.getNextPointMass() != null && firstEnclosedPoint.getNextPointMass().getNextPointMass() != null ) {
            for ( int i = 0; i < 20; i++ ) {
                PointMass previousPoint = firstEnclosedPoint;
                PointMass currentPoint = previousPoint.getNextPointMass();
                PointMass nextPoint = currentPoint.getNextPointMass();
                double maxForce = 0;
                double maxForce2 = 0;
                clearAllPointMassVelocities();
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

                    // Determine the force exerted by the walls of the containment rectangle.
                    double forceXDueToWalls;
                    if ( currentPoint.getPosition().getX() < containmentRect.getMinX() ) {
                        forceXDueToWalls = MAX_WALL_FORCE;
                    }
                    else if ( currentPoint.getPosition().getX() < containmentRect.getMinX() ) {
                        forceXDueToWalls = -MAX_WALL_FORCE;
                    }
                    else {
                        forceXDueToWalls = WALL_FORCE_CONSTANT / Math.pow( currentPoint.getPosition().getX() - containmentRect.getX(), 2 ) -
                                           WALL_FORCE_CONSTANT / Math.pow( containmentRect.getMaxX() - currentPoint.getPosition().getX(), 2 );
                        forceXDueToWalls = MathUtil.clamp( -MAX_WALL_FORCE, forceXDueToWalls, MAX_WALL_FORCE );
                    }
                    double forceYDueToWalls;
                    if ( currentPoint.getPosition().getY() < containmentRect.getMinY() ) {
                        forceYDueToWalls = MAX_WALL_FORCE;
                    }
                    else if ( currentPoint.getPosition().getY() < containmentRect.getMinY() ) {
                        forceYDueToWalls = -MAX_WALL_FORCE;
                    }
                    else {
                        forceYDueToWalls = WALL_FORCE_CONSTANT / Math.pow( currentPoint.getPosition().getY() - containmentRect.getY(), 2 ) -
                                           WALL_FORCE_CONSTANT / Math.pow( containmentRect.getMaxY() - currentPoint.getPosition().getY(), 2 );
                        forceYDueToWalls = MathUtil.clamp( -MAX_WALL_FORCE, forceYDueToWalls, MAX_WALL_FORCE );
                    }
                    ImmutableVector2D forceVectorDueToWalls = new ImmutableVector2D( forceXDueToWalls, forceYDueToWalls );
//                    System.out.println( "forceVectorDueToWalls = " + forceVectorDueToWalls );

                    ImmutableVector2D totalForceVector = forceVectorDueToPreviousPoint.getAddedInstance( forceVectorDueToNextPoint ).getAddedInstance( forceVectorDueToWalls );
                    currentPoint.updateVelocity( totalForceVector, TIME_SLICE );
                    currentPoint.updatePosition( TIME_SLICE );

                    // Move down the chain to the next shape-defining point.
                    previousPoint = currentPoint;
                    currentPoint = nextPoint;
                    nextPoint = currentPoint.nextPointMass;
                }
            }
        }

//        System.out.println( "Dumping points: " );
//        dumpPointMasses();

        // Update the shape to reflect the newly added point.
//        shapeProperty.set( BiomoleculeShapeUtils.createCurvyLineFromPoints( getPointList() ) );
        shapeProperty.set( BiomoleculeShapeUtils.createSegmentedLineFromPoints( getPointList() ) );
    }

    private void moveAllPoints( ImmutableVector2D movementAmount ) {
        PointMass thisPoint = firstShapeDefiningPoint;
        while ( thisPoint != null ) {
            thisPoint.setPosition( thisPoint.getPosition().getX() + movementAmount.getX(),
                                   thisPoint.getPosition().getY() + movementAmount.getY() );
            thisPoint = thisPoint.getNextPointMass();
        }
    }

    /**
     * Get the first point of the "enclosed region", meaning the part beyond
     * the stick-out region.
     *
     * @return
     */
    PointMass getFirstEnclosedPoint() {
        double lengthSoFar = 0;
        PointMass thisPoint = firstShapeDefiningPoint.getNextPointMass();
        while ( thisPoint != null ) {
            lengthSoFar += thisPoint.getTargetDistanceToPreviousPoint();
            if ( lengthSoFar >= LEADER_LENGTH ) {
                break;
            }
            thisPoint = thisPoint.getNextPointMass();
        }

        return thisPoint;
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

    // Get the points that define the shape as a list.
    // TODO: Created for debugging, may not be needed long term.
    public List<Point2D> getPointList() {
        ArrayList<Point2D> pointList = new ArrayList<Point2D>();
        PointMass thisPoint = firstShapeDefiningPoint;
        while ( thisPoint != null ) {
            pointList.add( thisPoint.getPosition() );
            thisPoint = thisPoint.getNextPointMass();
        }
        return pointList;
    }

    // Get the points that define the shape as a list.
    // TODO: Created for debugging, may not be needed long term.
    public List<PointMass> getPointMassList() {
        ArrayList<PointMass> pointMasses = new ArrayList<PointMass>();
        PointMass thisPoint = firstShapeDefiningPoint;
        while ( thisPoint != null ) {
            pointMasses.add( thisPoint );
            thisPoint = thisPoint.getNextPointMass();
        }
        return pointMasses;
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
     * @return length in picometers
     */
    public double getLength() {
        double length = 0;
        PointMass thisPoint = firstShapeDefiningPoint.getNextPointMass();
        while ( thisPoint != null ) {
            length += thisPoint.getTargetDistanceToPreviousPoint();
            thisPoint = thisPoint.getNextPointMass();
        }
        return length;
    }

    /**
     * Activate the placement hint(s).
     *
     * @param biomolecule
     */
    public void activateHints( MobileBiomolecule biomolecule ) {
        if ( ribosomePlacementHint.isMatchingBiomolecule( biomolecule ) ) {
            ribosomePlacementHint.active.set( true );
        }
    }

    public void deactivateAllHints() {
        ribosomePlacementHint.active.set( false );
    }

    public void connectToRibosome( Ribosome ribosome ) {
        behaviorState = new BeingTranslatedState( this, ribosome );
    }

    private void dumpPointMasses() {
        PointMass thisPoint = firstShapeDefiningPoint;
        int count = 0;
        while ( thisPoint != null ) {
            System.out.println( count + " : " + thisPoint );
            thisPoint = thisPoint.getNextPointMass();
            count++;
        }
    }

    public static class PointMass {
        public static final double MASS = 0.25; // In kg.  Arbitrarily chosen to get the desired behavior.
        private final Point2D position = new Point2D.Double( 0, 0 );
        private final Vector2D velocity = new Vector2D( 0, 0 );
        private final Vector2D acceleration = new Vector2D( 0, 0 );
        private PointMass previousPointMass = null;
        private PointMass nextPointMass = null;

        private double targetDistanceToPreviousPoint; // In picometers.

        private PointMass( Point2D initialPosition, double targetDistanceToPreviousPoint ) {
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

        public void setVelocity( double x, double y ) {
            velocity.setComponents( x, y );
        }

        private void setVelocity( ImmutableVector2D velocity ) {
            setVelocity( velocity.getX(), velocity.getY() );
        }

        public ImmutableVector2D getVelocity() {
            return new ImmutableVector2D( velocity.getX(), velocity.getY() );
        }

        public void setAcceleration( ImmutableVector2D acceleration ) {
            this.acceleration.setValue( acceleration );
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

        public void update( double deltaTime ) {
            velocity.setValue( velocity.getAddedInstance( acceleration.getScaledInstance( deltaTime ) ) );
            position.setLocation( position.getX() + velocity.getX() * deltaTime, position.getY() + velocity.getY() * deltaTime );
        }

        public void translate( ImmutableVector2D translationVector ) {
            setPosition( position.getX() + translationVector.getX(), position.getY() + translationVector.getY() );
        }

        public void setTargetDistanceToPreviousPoint( double targetDistance ) {
            targetDistanceToPreviousPoint = targetDistance;
        }

        public void clearVelocity() {
            velocity.setComponents( 0, 0 );
        }
    }

    /**
     * A shape the encloses a segment of the mRNA.  These segments, connected
     * together, are used to define the outline shape of the mRNA strand.
     * The path of the strand within these shape segments is worked out
     * elsewhere.
     */
    public abstract static class ShapeSegment {

        public final Property<Rectangle2D> bounds = new Property<Rectangle2D>( new Rectangle2D.Double() );

        public Point2D getLowerRightCornerPos() {
            return new Point2D.Double( bounds.get().getMaxX(), bounds.get().getMinY() );
        }

        public void setLowerRightCornerPos( Point2D newLowerRightCornerPos ) {
            ImmutableVector2D currentLowerRightCornerPos = new ImmutableVector2D( getLowerRightCornerPos() );
            ImmutableVector2D delta = new ImmutableVector2D( newLowerRightCornerPos ).getSubtractedInstance( currentLowerRightCornerPos );
            Rectangle2D newBounds = AffineTransform.getTranslateInstance( delta.getX(), delta.getY() ).createTransformedShape( bounds.get() ).getBounds2D();
            bounds.set( newBounds );
        }

        ;

        public Point2D getUpperLeftCornerPos() {
            return new Point2D.Double( bounds.get().getMinX(), bounds.get().getMaxY() );
        }

        public void translate( ImmutableVector2D translationVector ) {
            bounds.set( new Rectangle2D.Double( bounds.get().getX() + translationVector.getX(),
                                                bounds.get().getY() + translationVector.getY(),
                                                bounds.get().getWidth(),
                                                bounds.get().getHeight() ) );
        }

        public Rectangle2D getBounds() {
            // TODO: Test this.  Not sure it will work correctly.
            return (Rectangle2D) bounds.get().clone();
        }

        public boolean isFlat() {
            return getBounds().getHeight() == 0;
        }

        public double getLength() {
            Point2D upperLeft = new Point2D.Double( bounds.get().getMinX(), bounds.get().getMaxY() );
            Point2D lowerRight = new Point2D.Double( bounds.get().getMaxX(), bounds.get().getMinY() );
            return upperLeft.distance( lowerRight );
        }

        /**
         * Grow the the left if a horizontal segment, or up and to the left if
         * a diagonal segment, by the specified length.
         *
         * @param length
         */
        public abstract void growLeft( double length );

        public static class HorizontalSegment extends ShapeSegment {

            public HorizontalSegment( Point2D origin, double length ) {
                bounds.set( new Rectangle2D.Double( origin.getX(), origin.getY(), length, 0 ) );
            }

            @Override public void growLeft( double length ) {
                Rectangle2D newBounds = new Rectangle2D.Double( bounds.get().getMinX() - length, bounds.get().getY(), bounds.get().getWidth() + length, 0 );
                bounds.set( newBounds );
            }
        }

        public static class DiagonalSegment extends ShapeSegment {
            public DiagonalSegment( Point2D origin, double length ) {
                ImmutableVector2D diagonalVector = new ImmutableVector2D( length, 0 ).getRotatedInstance( Math.PI / 4 );
                bounds.set( new Rectangle2D.Double( origin.getX(),
                                                    origin.getY(),
                                                    diagonalVector.getX(),
                                                    diagonalVector.getY() ) );
            }

            // Strictly speaking, this actually grows UP and to the left, not
            // just to the left.

            /**
             * Grow diagonally by the specified amount.  Despite the name, this
             * actually grows UP and to the left, not just to the left.
             *
             * @param length
             */
            @Override public void growLeft( double length ) {
                assert length >= 0;
                double growthAmount = length * Math.cos( Math.PI / 4 );
                Rectangle2D newBounds = new Rectangle2D.Double( bounds.get().getMinX() - growthAmount,
                                                                bounds.get().getY(),
                                                                bounds.get().getWidth() + growthAmount,
                                                                bounds.get().getHeight() + growthAmount );
                bounds.set( newBounds );
            }
        }
    }

    /**
     * Main routine that constructs a PhET Piccolo canvas in a window.
     *
     * @param args
     */
    public static void main( String[] args ) {

        Dimension2D stageSize = new PDimension( 1000, 400 );

        PhetPCanvas canvas = new PhetPCanvas();
        // Set up the canvas-screen transform.
        canvas.setWorldTransformStrategy( new PhetPCanvas.CenteredStage( canvas, stageSize ) );

        ModelViewTransform mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( stageSize.getWidth() * 0.2 ), (int) Math.round( stageSize.getHeight() * 0.50 ) ),
                0.2 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        canvas.getLayer().addChild( new PhetPPath( new Rectangle2D.Double( -20, -20, 40, 40 ), Color.PINK ) );

        // Boiler plate app stuff.
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( (int) stageSize.getWidth(), (int) stageSize.getHeight() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null ); // Center.
        frame.setVisible( true );

        MessengerRna messengerRna = new MessengerRna( new ManualGeneExpressionModel(), mvt.modelToView( new Point2D.Double( 0, 0 ) ) );
        canvas.addWorldChild( new MessengerRnaNode( mvt, messengerRna ) );
        for ( int i = 0; i < 200; i++ ) {
            messengerRna.addLength( 25 ); // Number derived from what polymerase tends to do.
            try {
                Thread.sleep( 500 );
            }
            catch ( InterruptedException e ) {
                e.printStackTrace();
            }
            canvas.repaint();
        }
    }
}
