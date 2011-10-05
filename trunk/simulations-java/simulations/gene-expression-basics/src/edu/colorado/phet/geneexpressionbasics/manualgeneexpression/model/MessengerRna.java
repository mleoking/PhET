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
    private static final double MIN_DISTANCE_BETWEEN_POINTS = 20; // In picometers, empirically determined.

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

    // Length of the portion that "sticks out" to create a clear place for the
    // ribosome to attach.
    private static final double STICK_OUT_LENGTH = 100; // In nanometers.

    // Min height for the containment rectangle.  Arbitrarily small so that
    // points can fit inside, but not much more.
    private static final double MIN_CONTAINMENT_RECT_HEIGHT = 1;

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private PointMass firstShapeDefiningPoint = null;
    private PointMass lastShapeDefiningPoint = null;

    public final PlacementHint ribosomePlacementHint;

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

        // Add the placement hint that will show the user where the ribosome
        // can be attached.
        ribosomePlacementHint = new PlacementHint( new Ribosome( model ) );
        shapeProperty.addObserver( new SimpleObserver() {
            public void update() {
                // This hint always sits at the beginning of the RNA strand.
                ribosomePlacementHint.setPosition( firstShapeDefiningPoint.getPosition() );
            }
        } );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------


    @Override public void translate( ImmutableVector2D translationVector ) {
        // Translate the current shape user the superclass facility.
        super.translate( translationVector );
        // Move each of the shape-defining points.
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
    public void addLength( Point2D newEndPosition ) {

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
        if ( currentUnfurledLength > STICK_OUT_LENGTH ) {
            // There are enough points to start winding up the mRNA.  Set the
            // size of the rectangle that contains the wound up portion to a
            // value that is shorter than the unfurled length.
            double diagonalLength = MIN_DISTANCE_BETWEEN_POINTS * ( Math.pow( currentUnfurledLength / MIN_DISTANCE_BETWEEN_POINTS, 0.5 ) );

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
            firstShapeDefiningPoint.setPosition( woundUpRect.getX() - STICK_OUT_LENGTH, woundUpRect.getMaxY() );
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
     * Add a length to the mRNA from its current end point to the specified end
     * point.  This is usually done in small amounts, and is likely to look
     * weird if an attempt is made to grow to a distant point.  As a length is
     * added, the mRNA shape "curls up".
     */
    public void addLengthSpringy( Point2D newEndPosition ) {

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
        if ( currentUnfurledLength > STICK_OUT_LENGTH ) {
            // There are enough points to start winding up the mRNA.  Set the
            // size of the rectangle that contains the wound up portion to a
            // value that is shorter than the unfurled length.
            double diagonalLength = MIN_DISTANCE_BETWEEN_POINTS * ( Math.pow( currentUnfurledLength / MIN_DISTANCE_BETWEEN_POINTS, 0.5 ) );

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
            firstShapeDefiningPoint.setPosition( woundUpRect.getX() - STICK_OUT_LENGTH, woundUpRect.getMaxY() );
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
             lastShapeDefiningPoint.targetDistanceToPreviousPoint < MIN_DISTANCE_BETWEEN_POINTS ) {
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
        if ( currentUnfurledLength > STICK_OUT_LENGTH ) {
            // There are enough points to start winding up the mRNA.  Create a
            // rectangle that will define the area where the strand must
            // within (except for the stick-out area).
            double diagonalLength = MIN_DISTANCE_BETWEEN_POINTS * ( Math.pow( currentUnfurledLength / MIN_DISTANCE_BETWEEN_POINTS, 0.5 ) );
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
            firstShapeDefiningPoint.setPosition( containmentRect.getX() - STICK_OUT_LENGTH, containmentRect.getMaxY() );
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
            if ( lengthSoFar >= STICK_OUT_LENGTH ) {
                break;
            }
            thisPoint = thisPoint.getNextPointMass();
        }

        return thisPoint;
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
        private PointMass previousPointMass = null;
        private PointMass nextPointMass = null;

        private final double targetDistanceToPreviousPoint;

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

        public void translate( ImmutableVector2D translationVector ) {
            setPosition( position.getX() + translationVector.getX(), position.getY() + translationVector.getY() );
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
            messengerRna.addLength( mvt.modelToView( i * 200, 0 ) );
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
