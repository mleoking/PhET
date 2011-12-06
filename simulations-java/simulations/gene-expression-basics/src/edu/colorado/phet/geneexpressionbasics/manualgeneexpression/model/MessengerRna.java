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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.geneexpressionbasics.common.model.BiomoleculeShapeUtils;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.PlacementHint;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.RandomWalkMotionStrategy;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.StillnessMotionStrategy;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view.MessengerRnaNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Class that represents messenger ribonucleic acid, or mRNA, in the model.
 * This class is fairly complex, due to the need for mRNA to wind up and
 * unwind as it is transcribed, translated, and destroyed.
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

    // Factor to use to avoid issues with floating point resolution.
    private static final double FLOATING_POINT_COMP_FACTOR = 1E-7;

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private PointMass firstShapeDefiningPoint = null;
    private PointMass lastShapeDefiningPoint = null;

    public final PlacementHint ribosomePlacementHint;
    public final PlacementHint mRnaDestroyerPlacementHint;

    // List of the shape segments that define the outline shape.
    public final EnhancedObservableList<ShapeSegment> shapeSegments = new EnhancedObservableList<ShapeSegment>();

    // Map from ribosomes to the shape segment to which they are attached.
    private final Map<Ribosome, ShapeSegment> mapRibosomeToShapeSegment = new HashMap<Ribosome, ShapeSegment>();

    // Protein prototype, used to keep track of protein that should be
    // synthesized from this particular strand of mRNA.
    private final Protein proteinPrototype;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    /**
     * Constructor.  This creates the mRNA as a single point, with the intention
     * of growing it.
     *
     * @param position
     */
    public MessengerRna( final GeneExpressionModel model, Protein proteinPrototype, Point2D position ) {
        super( model, new DoubleGeneralPath( position ).getGeneralPath(), NOMINAL_COLOR );
        this.proteinPrototype = proteinPrototype;

        // Add first shape defining point to the point list.
        firstShapeDefiningPoint = new PointMass( position, 0 );
        lastShapeDefiningPoint = firstShapeDefiningPoint;

        // Add the first segment to the shape segment list.  This segment will
        // contain the "leader" for the mRNA.
        shapeSegments.add( new ShapeSegment.FlatSegment( position ) {{
            setCapacity( LEADER_LENGTH );
        }} );

        // Add the placement hints for the locations where the user can attach
        // a ribosome or and mRNA destroyer.
        ribosomePlacementHint = new PlacementHint( new Ribosome( model ) );
        mRnaDestroyerPlacementHint = new PlacementHint( new MessengerRnaDestroyer( model ) );
        shapeProperty.addObserver( new SimpleObserver() {
            public void update() {
                // This hint always sits at the beginning of the RNA strand.
                ImmutableVector2D currentMRnaFirstPointPosition = new ImmutableVector2D( firstShapeDefiningPoint.getPosition() );
                ImmutableVector2D offsetToTranslationChannelEntrance = ( new Ribosome( model ) ).getEntranceOfRnaChannelPos();
                ribosomePlacementHint.setPosition( currentMRnaFirstPointPosition.getSubtractedInstance( offsetToTranslationChannelEntrance ).toPoint2D() );
                mRnaDestroyerPlacementHint.setPosition( currentMRnaFirstPointPosition.getSubtractedInstance( offsetToTranslationChannelEntrance ).toPoint2D() );
            }
        } );

        // Since mRNA is synthesized by polymerase, it starts its life in the
        // attached state.
        // TODO: For now this just sets the motion strategy, but eventually should be "attached" to polymerase at the beginning of its life.
        setMotionStrategy( new StillnessMotionStrategy() );
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
     * Get the first shape-defining point enclosed in the provided length range.
     *
     * @param lengthRange
     * @return
     */
    private PointMass getFirstEnclosedPoint( DoubleRange lengthRange ) {
        PointMass currentPoint = firstShapeDefiningPoint;
        double currentLength = 0;
        while ( currentPoint != null ) {
            if ( currentLength >= lengthRange.getMin() && currentLength < lengthRange.getMax() ) {
                // We've found the first point.
                break;
            }
            currentPoint = currentPoint.getNextPointMass();
            currentLength += currentPoint != null ? currentPoint.getTargetDistanceToPreviousPoint() : 0;
        }
        return currentPoint;
    }

    /**
     * Get the last shape-defining point enclosed in the provided length range.
     *
     * @param lengthRange
     * @return
     */
    private PointMass getLastEnclosedPoint( DoubleRange lengthRange ) {
        PointMass currentPoint = firstShapeDefiningPoint;
        double currentLength = 0;
        while ( currentPoint != null ) {
            if ( currentLength >= lengthRange.getMin() && currentLength < lengthRange.getMax() ) {
                break;
            }
            currentPoint = currentPoint.getNextPointMass();
            currentLength += currentPoint != null ? currentPoint.getTargetDistanceToPreviousPoint() : 0;
        }

        if ( currentPoint != null ) {
            while ( currentPoint.getNextPointMass() != null &&
                    currentPoint.getNextPointMass().getTargetDistanceToPreviousPoint() + currentLength < lengthRange.getMax() ) {
                currentPoint = currentPoint.getNextPointMass();
                currentLength += currentPoint.getTargetDistanceToPreviousPoint();
            }
        }

        if ( currentPoint == null ) {
            System.out.println( "No last point." );
        }

        return currentPoint;
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

        ShapeSegment segmentToAdvance = mapRibosomeToShapeSegment.get( ribosome );

        // Error checking.
        if ( segmentToAdvance == null ) {
            System.out.println( getClass().getName() + " - Warning: Attempt to advance translation by a ribosome that isn't attached." );
            return true;
        }

        // Advance the translation by advancing the position of the mRNA in the
        // segment that corresponds to the translation channel of the ribosome.
        segmentToAdvance.advance( length, shapeSegments );

        // Realign the segments, since they may well have changed shape.  
        if ( shapeSegments.contains( segmentToAdvance ) ) {
            realignSegmentsFrom( segmentToAdvance );
        }

        // Since the sizes and relationships of the segments probably changed,
        // the winding algorithm needs to be rerun.
        windPointsThroughSegments();

        // If there is anything left in this segment, then transcription is not
        // yet complete.
        return segmentToAdvance.getContainedLength() <= 0;
    }

    /**
     * Add the specified amount of mRNA length to the tail end of the mRNA.
     * Adding a length will cause the winding algorithm to be re-run.
     *
     * @param length - Length of mRNA to add in picometers.
     */
    public void addLength( double length ) {

        // Add the length to the set of shape-defining points.  This may add
        // a new point, or simply reposition the current last point.
        if ( firstShapeDefiningPoint == lastShapeDefiningPoint ) {
            // This is the first length added to the strand, so put it on.
            addPointToEnd( lastShapeDefiningPoint.getPosition(), length );
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

        // Update the shape segments that define the outline shape.
        getLastShapeSegment().add( length, shapeSegments );

        // Realign the segments, since some growth probably occurred.
        realignSegmentsFromEnd();

        // Now that the points and shape segments are updated, run the
        // algorithm that winds the points through the shapes to produce the
        // shape of the strand that will be presented to the user.
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

    public void setLowerRightPosition( double x, double y ) {
        setLowerRightPosition( new Point2D.Double( x, y ) );
    }

    /**
     * Create a new version of the protein that should result when this strand
     * of mRNA is translated.
     *
     * @return
     */
    public Protein getProteinPrototype() {
        return proteinPrototype;
    }

    /**
     * This is the "winding algorithm" that positions the points that define
     * the shape of the mRNA within the shape segments.  The combination of
     * this algorithm and the shape segments allow the mRNA to look reasonable
     * when it is being synthesized and when it is being transcribed.
     */
    private void windPointsThroughSegments() {
        assert shapeSegments.size() > 0;
        double handledLength = 0;

        // Loop through the shape segments positioning the shape-defining
        // points within them.
        for ( ShapeSegment shapeSegment : shapeSegments ) {
            DoubleRange lengthRange;
            if ( shapeSegment != getLastShapeSegment() ) {
                lengthRange = new DoubleRange( handledLength, handledLength + shapeSegment.getContainedLength() );
            }
            else {
                // This is the last segment, so set the max to be infinite in
                // order to be sure that the last point is always included.  If
                // this isn't done, accumulation of floating point errors can
                // cause the last point to fall outside of the range, and it
                // won't get positioned.  Which is bad.
                lengthRange = new DoubleRange( handledLength, Double.POSITIVE_INFINITY );

                // Watch for unexpected conditions and spit out warning if found.
                double totalShapeSegmentLength = getTotalLengthInShapeSegments();
                if ( Math.abs( getLength() - totalShapeSegmentLength ) > 1 ) {
                    // If this message appears, something may well be wrong
                    // with the way the shape segments are being updated.
                    System.out.println( getClass().getName() + " Warning: Larger than expected difference between mRNA length and shape segment length." );
                }
            }

            PointMass firstEnclosedPoint = getFirstEnclosedPoint( lengthRange );
            PointMass lastEnclosedPoint = getLastEnclosedPoint( lengthRange );
            if ( firstEnclosedPoint == null ) {
                // The segment contains no points.
                continue;
            }
            else if ( shapeSegment.isFlat() ) {
                // Position the contained points in a flat line.
                positionPointsInLine( firstEnclosedPoint, lastEnclosedPoint, shapeSegment.getUpperLeftCornerPos() );
            }
            else {
                // Segment must be square, so position the points within it
                // using the spring algorithm.
                randomizePointPositionsInRectangle( firstEnclosedPoint, lastEnclosedPoint, shapeSegment.getBounds() );
                runSpringAlgorithm( firstEnclosedPoint, lastEnclosedPoint, shapeSegment.getBounds() );
            }

            handledLength += shapeSegment.getContainedLength();
        }

        // Update the shape property based on the newly positioned points.
        shapeProperty.set( BiomoleculeShapeUtils.createCurvyLineFromPoints( getPointList() ) );
    }

    private double getTotalLengthInShapeSegments() {
        double totalShapeSegmentLength = 0;
        for ( ShapeSegment shapeSeg : shapeSegments ) {
            totalShapeSegmentLength += shapeSeg.getContainedLength();
        }
        return totalShapeSegmentLength;
    }

    /**
     * Get the point in model space where the entrance of the given ribosome's
     * translation channel should be in order to be correctly attached to
     * this strand of messenger RNA.  This allows the ribosome to "follow" the
     * mRNA if it is moving or changing shape.
     * <p/>
     * TODO: Consider making this a property.  Might be cleaner.
     *
     * @param ribosome
     * @return
     */
    public Point2D getRibosomeAttachmentPoint( Ribosome ribosome ) {
        if ( !mapRibosomeToShapeSegment.containsKey( ribosome ) ) {
            System.out.println( getClass().getName() + " Warning: Ignoring attempt to obtain attachment point for non-attached ribosom." );
            return null;
        }
        Point2D attachmentPoint;
        ShapeSegment segment = mapRibosomeToShapeSegment.get( ribosome );
        if ( shapeSegments.getPreviousItem( segment ) == null ) {
            // There is no previous segment, which means that the segment to
            // which this ribosome is attached is the leader segment.  The
            // attachment point is thus the leader length from its rightmost
            // edge.
            attachmentPoint = new Point2D.Double( segment.getLowerRightCornerPos().getX() - LEADER_LENGTH, segment.getLowerRightCornerPos().getY() );
        }
        else {
            // The segment has filled up the channel, so calculate the
            // position based on its left edge.
            attachmentPoint = new Point2D.Double( segment.getUpperLeftCornerPos().getX() + ribosome.getTranslationChannelLength(),
                                                  segment.getUpperLeftCornerPos().getY() );
        }
        return attachmentPoint;
    }

    /**
     * Position a series of point masses in a straight line.  The distances
     * between the point masses are set to be their target distances.  This is
     * generally used when positioning the point masses in a flat shape segment.
     *
     * @param firstPoint
     * @param lastPoint
     * @param origin
     */
    private void positionPointsInLine( PointMass firstPoint, PointMass lastPoint, Point2D origin ) {
        PointMass currentPoint = firstPoint;
        double xOffset = 0;
        while ( currentPoint != lastPoint && currentPoint != null ) {
            currentPoint.setPosition( origin.getX() + xOffset, origin.getY() );
            currentPoint = currentPoint.getNextPointMass();
            xOffset += currentPoint != null ? currentPoint.getTargetDistanceToPreviousPoint() : 0;
        }
        if ( currentPoint == null ) {
            System.out.println( getClass().getName() + " Error: Last point not found when positioning points." );
            assert false; // This function has been misused - the end of the point list was hit before the last point.
        }
        else {
            // Position the last point.
            currentPoint.setPosition( origin.getX() + xOffset, origin.getY() );
        }
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
                    if ( vectorToPreviousPoint.getMagnitude() == 0 ) {
                        // This point is sitting on top of the previous point,
                        // so create an arbitrary vector away from it.
                        vectorToPreviousPoint = new ImmutableVector2D( 1, 1 );
                    }
                    double scalarForceDueToPreviousPoint = ( -springConstant ) * ( currentPoint.targetDistanceToPreviousPoint - currentPoint.distance( previousPoint ) );
                    ImmutableVector2D forceDueToPreviousPoint = vectorToPreviousPoint.getNormalizedInstance().getScaledInstance( scalarForceDueToPreviousPoint );
                    ImmutableVector2D vectorToNextPoint = new ImmutableVector2D( nextPoint.getPosition() ).getSubtractedInstance( new ImmutableVector2D( currentPoint.getPosition() ) );
                    if ( vectorToNextPoint.getMagnitude() == 0 ) {
                        // This point is sitting on top of the next point,
                        // so create an arbitrary vector away from it.
                        vectorToNextPoint = new ImmutableVector2D( -1, -1 );
                    }
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

    /**
     * Realign the positions of all segments star
     *
     * @param segmentToAlignFrom
     */
    private void realignSegmentsFrom( ShapeSegment segmentToAlignFrom ) {
        if ( shapeSegments.indexOf( segmentToAlignFrom ) == -1 ) {
            System.out.println( getClass().getName() + " Warning: Ignoring attempt to align to segment that is not on the list." );
            assert false;
            return;
        }

        // Align segments that follow this one.
        ShapeSegment currentSegment = segmentToAlignFrom;
        ShapeSegment nextSegment = shapeSegments.getNextItem( currentSegment );
        while ( nextSegment != null ) {
            nextSegment.setUpperLeftCornerPosition( currentSegment.getLowerRightCornerPos() );
            currentSegment = nextSegment;
            nextSegment = shapeSegments.getNextItem( currentSegment );
        }

        // Align segments that precede this one.
        currentSegment = segmentToAlignFrom;
        ShapeSegment previousSegment = shapeSegments.getPreviousItem( currentSegment );
        while ( previousSegment != null ) {
            previousSegment.setLowerRightCornerPos( currentSegment.getUpperLeftCornerPos() );
            currentSegment = previousSegment;
            previousSegment = shapeSegments.getPreviousItem( currentSegment );
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
        lastShapeDefiningPoint.setNextPointMass( newPoint );
        newPoint.setPreviousPointMass( lastShapeDefiningPoint );
        lastShapeDefiningPoint = newPoint;
    }

    public Point2D getTranslationAttachmentPoint() {
        return firstShapeDefiningPoint.getPosition();
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

    // Get the points that define the shape as a list.
    private List<Point2D> getPointList() {
        ArrayList<Point2D> pointList = new ArrayList<Point2D>();
        PointMass thisPoint = firstShapeDefiningPoint;
        while ( thisPoint != null ) {
            pointList.add( thisPoint.getPosition() );
            thisPoint = thisPoint.getNextPointMass();
        }
        return pointList;
    }

    /**
     * Release this mRNA from a ribosome.  If this is the only ribosome to
     * which the mRNA is connected, the mRNA will start wandering.
     *
     * @param ribosome
     */
    public void releaseFromRibosome( Ribosome ribosome ) {
        assert mapRibosomeToShapeSegment.containsKey( ribosome ); // This shouldn't be called if the ribosome wasn't connected.
        mapRibosomeToShapeSegment.remove( ribosome );
        if ( mapRibosomeToShapeSegment.isEmpty() ) {
            attachmentStateMachine.detach();
        }
    }

    /**
     * Release this mRNA from the polymerase which is, presumably, transcribing
     * it.
     */
    public void releaseFromPolymerase() {
        // Set the state to just be drifting around in the cytoplasm.
        // TODO: Need to detach here, but can't until mRNA is starting out as attached to polymerase.
//        attachmentStateMachine.detach();
        setMotionStrategy( new RandomWalkMotionStrategy( motionBoundsProperty ) );
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
     * Activate the placement hint(s) as appropriate for the given biomolecule.
     *
     * @param biomolecule - And instance of the type of biomolecule for which
     *                    any matching hints should be activated.
     */
    public void activateHints( MobileBiomolecule biomolecule ) {
        ribosomePlacementHint.activateIfMatch( biomolecule );
        mRnaDestroyerPlacementHint.activateIfMatch( biomolecule );
    }

    public void deactivateAllHints() {
        ribosomePlacementHint.active.set( false );
        mRnaDestroyerPlacementHint.active.set( false );
    }

    // TODO: This needs to be replaced with standard attachment behavior.
    public void connectToRibosome( Ribosome ribosome ) {
//        behaviorState = new BeingTranslatedState( this, ribosome );
        assert !mapRibosomeToShapeSegment.containsKey( ribosome ); // State checking.

        // Set the capacity of the first segment to the size of the channel
        // through which it will be pulled plus the leader length.
        ShapeSegment firstShapeSegment = shapeSegments.get( 0 );
        assert firstShapeSegment.isFlat();
        firstShapeSegment.setCapacity( ribosome.getTranslationChannelLength() + LEADER_LENGTH );

        // Map the ribosome to the shape segment.
        mapRibosomeToShapeSegment.put( ribosome, firstShapeSegment );
    }

    /**
     * Get the proportion of the entire mRNA that has been translated by the
     * given ribosome.
     *
     * @param ribosome
     * @return
     */
    public double getProportionOfRnaTranslated( Ribosome ribosome ) {
        if ( !mapRibosomeToShapeSegment.containsKey( ribosome ) ) {
            System.out.println( getClass().getName() + " - Warning: Attempt to obtain amount of mRNA translated by a ribosome that isn't attached." );
            return 0;
        }
        double translatedLength = 0;
        for ( ShapeSegment shapeSegment : shapeSegments ) {
            translatedLength += shapeSegment.getContainedLength();
            if ( shapeSegment == mapRibosomeToShapeSegment.get( ribosome ) ) {
                break;
            }
        }
        return translatedLength / getLength();
    }

    /**
     * This class defines a point in model space that also has mass.  It is
     * is used to define the overall shape of the mRNA, which uses a spring
     * algorithm to implement the winding/twisting behavior.
     */
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
     * This class defines a shape that encloses a segment of the mRNA.  These
     * segments, connected together, are used to define the outline shape of
     * the mRNA strand. The path of the strand within these shape segments is
     * worked out elsewhere.
     * <p/>
     * Shape segments keep track of the length of mRNA that they contain, but
     * the don't explicitly contain references to the points that define the
     * shape.
     */
    public abstract static class ShapeSegment {

        public final Property<Rectangle2D> bounds = new Property<Rectangle2D>( new Rectangle2D.Double() );

        // Max length of mRNA that this segment can contain.
        public double capacity = Double.POSITIVE_INFINITY;

        public void setCapacity( double capacity ) {
            this.capacity = capacity;
        }

        public double getRemainingCapacity() {
            return capacity - getContainedLength();
        }

        public Point2D getLowerRightCornerPos() {
            return new Point2D.Double( bounds.get().getMaxX(), bounds.get().getMinY() );
        }

        public void setLowerRightCornerPos( Point2D newLowerRightCornerPos ) {
            ImmutableVector2D currentLowerRightCornerPos = new ImmutableVector2D( getLowerRightCornerPos() );
            ImmutableVector2D delta = new ImmutableVector2D( newLowerRightCornerPos ).getSubtractedInstance( currentLowerRightCornerPos );
            Rectangle2D newBounds = AffineTransform.getTranslateInstance( delta.getX(), delta.getY() ).createTransformedShape( bounds.get() ).getBounds2D();
            bounds.set( newBounds );
        }

        public Point2D getUpperLeftCornerPos() {
            return new Point2D.Double( bounds.get().getMinX(), bounds.get().getMaxY() );
        }

        public void setUpperLeftCornerPosition( Point2D upperLeftCornerPosition ) {
            bounds.set( new Rectangle2D.Double( upperLeftCornerPosition.getX(),
                                                upperLeftCornerPosition.getY() - bounds.get().getHeight(),
                                                bounds.get().getWidth(),
                                                bounds.get().getHeight() ) );
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

        /**
         * Get the length of the mRNA contained by this segment.
         *
         * @return
         */
        public abstract double getContainedLength();

        /**
         * Add the specified length of mRNA to the segment.  This will
         * generally cause the segment to grow.  By design, flat segments grow
         * to the left, square segments grow up and left.  The shape segment
         * list is also passed in so that new segments can be added if needed.
         *
         * @param length
         * @param shapeSegmentList
         */
        public abstract void add( double length, EnhancedObservableList<ShapeSegment> shapeSegmentList );

        /**
         * Remove the specified amount of mRNA from the segment.  This will
         * generally cause a segment to shrink.  Flat segments shrink in from
         * the right, square segments shrink from the lower right corner
         * towards the upper left.  The shape segment list is also a parameter
         * so that shape segments can be removed if necessary.
         *
         * @param length
         * @param shapeSegmentList
         */
        public abstract void remove( double length, EnhancedObservableList<ShapeSegment> shapeSegmentList );


        /**
         * Advance the mRNA through this shape segment.  This is what happens
         * when the mRNA is being translated by a ribosome into a protein.  The
         * list of shape segments is also a parameter in case segments need to
         * be added or removed.
         *
         * @param length
         * @param shapeSegmentList
         */
        public abstract void advance( double length, EnhancedObservableList<ShapeSegment> shapeSegmentList );

        /**
         * Flat segment - has no height, so rRNA contained in this segment is
         * not wound.
         */
        public static class FlatSegment extends ShapeSegment {

            public FlatSegment( Point2D origin ) {
                bounds.set( new Rectangle2D.Double( origin.getX(), origin.getY(), 0, 0 ) );
            }

            @Override public double getContainedLength() {
                // For a flat segment, the length of mRNA contained is equal to
                // the width.
                return bounds.get().getWidth();
            }

            @Override public void add( double length, EnhancedObservableList<ShapeSegment> shapeSegmentList ) {
                assert getContainedLength() <= capacity; // This shouldn't be called if there is no remaining capacity.
                double growthAmount = length;
                if ( getContainedLength() + length > capacity ) {
                    // This segment can't hold the specified length.  Add a new
                    // square segment to the end of the segment list and put
                    // the excess in there.
                    ShapeSegment newSquareSegment = new SquareSegment( getLowerRightCornerPos() );
                    growthAmount = capacity - getContainedLength(); // Clamp growth at remaining capacity.
                    newSquareSegment.add( length - growthAmount, shapeSegmentList );
                    shapeSegmentList.insertAfter( this, newSquareSegment );
                }
                // Grow the bounds linearly to the left to accommodate the
                // additional length.
                bounds.set( new Rectangle2D.Double( bounds.get().getX() - growthAmount,
                                                    bounds.get().getY(),
                                                    bounds.get().getWidth() + growthAmount,
                                                    0 ) );
            }

            @Override public void remove( double length, EnhancedObservableList<ShapeSegment> shapeSegmentList ) {
                bounds.set( new Rectangle2D.Double( bounds.get().getX(), bounds.get().getY(), bounds.get().getWidth() - length, 0 ) );
                // If the length has gotten to zero, remove this segment from
                // the list.
                if ( getContainedLength() < FLOATING_POINT_COMP_FACTOR ) {
                    shapeSegmentList.remove( this );
                }
            }

            @Override public void advance( double length, EnhancedObservableList<ShapeSegment> shapeSegmentList ) {
                ShapeSegment outputSegment = shapeSegmentList.getPreviousItem( this );
                ShapeSegment inputSegment = shapeSegmentList.getNextItem( this );
                if ( inputSegment == null ) {
                    // There is no input segment, meaning that the end of the
                    // mRNA strand is contained in THIS segment, so this
                    // segment needs to shrink.
                    double lengthToAdvance = Math.min( length, getContainedLength() );
                    this.remove( lengthToAdvance, shapeSegmentList );
                    outputSegment.add( lengthToAdvance, shapeSegmentList );
                }
                else if ( inputSegment.getContainedLength() > length ) {
                    // The input segment contains enough mRNA length to supply
                    // this segment with the needed length.
                    if ( getContainedLength() + length <= capacity ) {
                        // The new length isn't enough to fill up this segment,
                        // so this segment just needs to grow.
                        add( length, shapeSegmentList );
                    }
                    else {
                        // This segment is full or close enough to being full
                        // that it can't accommodate all of the specified
                        // length.  Some or all of that length must go in the
                        // output segment.
                        double remainingCapacity = getRemainingCapacity();
                        if ( remainingCapacity > FLOATING_POINT_COMP_FACTOR ) {
                            // Not quite full yet - fill it up.
                            maxOutLength();
                            // This situation - one in which a segment that is
                            // having the mRNA advanced through it but it not
                            // yet full - should only occur when this segment
                            // is the first one on the shape segment list.  So,
                            // add a new one to the front of the segment list,
                            // but first, make sure there isn't something there
                            // already.
                            assert outputSegment == null;
                            ShapeSegment newLeaderSegment = new FlatSegment( this.getUpperLeftCornerPos() ) {{
                                setCapacity( LEADER_LENGTH );
                            }};
                            shapeSegmentList.insertBefore( this, newLeaderSegment );
                            outputSegment = newLeaderSegment;
                        }
                        // Add some or all of the length to the output segment.
                        outputSegment.add( length - remainingCapacity, shapeSegmentList );
                    }
                    // Remove the length from the input segment.
                    inputSegment.remove( length, shapeSegmentList );
                }
                else {
                    // The input segment is still around, but doesn't have
                    // the specified advancement length within it.  Shrink it
                    // to zero, which will remove it, and then shrink the
                    // advancing segment by the remaining amount.
                    this.remove( length - inputSegment.getContainedLength(), shapeSegmentList );
                    inputSegment.remove( inputSegment.getContainedLength(), shapeSegmentList );
                    outputSegment.add( length, shapeSegmentList );
                }
            }

            // Set size to be exactly the capacity.  Do not create any new
            // segments.
            private void maxOutLength() {
                double growthAmount = getRemainingCapacity();
                bounds.set( new Rectangle2D.Double( bounds.get().getX() - growthAmount,
                                                    bounds.get().getY(),
                                                    capacity,
                                                    0 ) );
            }
        }

        /**
         * Class that defines a square segment, which is one in which the mRNA
         * can be (and generally is) curled up.
         */
        public static class SquareSegment extends ShapeSegment {

            // Maintain an explicit value for the length of the mRNA contained
            // within this segment even though the bounds essentially define
            // said length.  This helps to avoid floating point issues.
            private double containedLength = 0;

            public SquareSegment( Point2D origin ) {
                bounds.set( new Rectangle2D.Double( origin.getX(), origin.getY(), 0, 0 ) );
            }

            @Override public double getContainedLength() {
                return containedLength;
            }

            @Override public void add( double length, EnhancedObservableList<ShapeSegment> shapeSegmentList ) {
                containedLength += length;
                // Grow the bounds up and to the left to accommodate the
                // additional length.
                double sideGrowthAmount = calculateSideLength() - bounds.get().getWidth();
                assert length >= 0 && sideGrowthAmount >= 0; //
                bounds.set( new Rectangle2D.Double( bounds.get().getX() - sideGrowthAmount,
                                                    bounds.get().getY(),
                                                    bounds.get().getWidth() + sideGrowthAmount,
                                                    bounds.get().getHeight() + sideGrowthAmount ) );
            }

            @Override public void remove( double length, EnhancedObservableList<ShapeSegment> shapeSegmentList ) {
                containedLength -= length;
                // Shrink by moving the lower right corner up and to the left.
                double sideShrinkageAmount = bounds.get().getWidth() - calculateSideLength();
                assert length >= 0 && sideShrinkageAmount >= 0; //
                bounds.set( new Rectangle2D.Double( bounds.get().getX(),
                                                    bounds.get().getY() + sideShrinkageAmount,
                                                    bounds.get().getWidth() - sideShrinkageAmount,
                                                    bounds.get().getHeight() - sideShrinkageAmount ) );
                // If the length has gotten to zero, remove this segment from
                // the list.
                if ( getContainedLength() <= FLOATING_POINT_COMP_FACTOR ) {
                    shapeSegmentList.remove( this );
                }
            }

            @Override public void advance( double length, EnhancedObservableList<ShapeSegment> shapeSegmentList ) {
                // This should never be called for square shape segments, since
                // translation should only occur based around flat segments.
                assert false;
            }

            // Determine the length of a side as a function of the contained
            // length of mRNA.
            private double calculateSideLength() {
                double desiredDiagonalLength = Math.pow( containedLength, 0.7 ); // Power value was empirically determined.
                return Math.sqrt( 2 * desiredDiagonalLength * desiredDiagonalLength );
            }
        }
    }

    /**
     * Class that defines an observable list with some additional methods that
     * make it easier to get next and previous items and to insert items before
     * and after existing items.
     *
     * @param <T>
     */
    public static class EnhancedObservableList<T> extends ObservableList<T> {
        private T getNextItem( T item ) {
            int index = indexOf( item );
            assert index != -1; // This function shouldn't be used for segments not on the list.
            if ( index == size() - 1 ) {
                // The given segment is the last element on the list, so null is returned.
                return null;
            }
            else {
                return get( index + 1 );
            }
        }

        private T getPreviousItem( T item ) {
            int index = indexOf( item );
            assert index != -1; // This function shouldn't be used for segments not on the list.
            if ( index == 0 ) {
                // The given segment is the first element on the list, so null is returned.
                return null;
            }
            else {
                return get( index - 1 );
            }
        }

        private void insertAfter( T existingItem, T itemToInsert ) {
            assert contains( existingItem );
            add( indexOf( existingItem ) + 1, itemToInsert );
        }

        private void insertBefore( T existingItem, T itemToInsert ) {
            assert contains( existingItem );
            add( indexOf( existingItem ), itemToInsert );
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

        // Boiler plate app stuff.
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( (int) stageSize.getWidth(), (int) stageSize.getHeight() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null ); // Center.
        frame.setVisible( true );

        MessengerRna messengerRna = new MessengerRna( new ManualGeneExpressionModel(),
                                                      new ProteinA( new StubGeneExpressionModel(), new Ribosome( new StubGeneExpressionModel() ) ),
                                                      mvt.modelToView( new Point2D.Double( 0, 0 ) ) );
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
