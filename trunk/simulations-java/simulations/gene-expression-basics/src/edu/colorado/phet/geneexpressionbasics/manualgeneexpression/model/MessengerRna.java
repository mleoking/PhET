// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.Point;
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
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.geneexpressionbasics.common.model.AttachmentSite;
import edu.colorado.phet.geneexpressionbasics.common.model.BioShapeUtils;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.PlacementHint;
import edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines.AttachmentStateMachine;
import edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines.MessengerRnaAttachmentStateMachine;
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
    public static final double LEADER_LENGTH = INTER_POINT_DISTANCE * 2;

    // Distance within which this will connect to a ribosome.
    private static final double RIBOSOME_CONNECTION_DISTANCE = 200; // picometers
    private static final double MRNA_DESTROYER_CONNECT_DISTANCE = 200; // picometers

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private PointMass firstShapeDefiningPoint = null;
    private PointMass lastShapeDefiningPoint = null;

    public final PlacementHint ribosomePlacementHint;
    public final PlacementHint mRnaDestroyerPlacementHint;

    // List of the shape segments that define the outline shape.
    public final EnhancedObservableList<ShapeSegment> shapeSegments = new EnhancedObservableList<ShapeSegment>();

    // Externally visible indicator for whether this mRNA is being synthesized.
    // Assumes that it is being synthesized when created.
    public final BooleanProperty beingSynthesized = new BooleanProperty( true );

    // Map from ribosomes to the shape segment to which they are attached.
    private final Map<Ribosome, ShapeSegment> mapRibosomeToShapeSegment = new HashMap<Ribosome, ShapeSegment>();

    // mRNA destroyer that is destroying this mRNA.  Null until and unless
    // destruction has begun.
    private MessengerRnaDestroyer messengerRnaDestroyer = null;

    // Shape segment where the mRNA destroyer is connected.  This is null until
    // and unless destruction has begun.
    private ShapeSegment segmentWhereDestroyerConnects = null;

    // Protein prototype, used to keep track of protein that should be
    // synthesized from this particular strand of mRNA.
    private final Protein proteinPrototype;

    // Local reference to the non-generic state machine used by this molecule.
    private final MessengerRnaAttachmentStateMachine mRnaAttachmentStateMachine;

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
        mRnaAttachmentStateMachine = (MessengerRnaAttachmentStateMachine) super.attachmentStateMachine;

        // Add first shape defining point to the point list.
        firstShapeDefiningPoint = new PointMass( position, 0 );
        lastShapeDefiningPoint = firstShapeDefiningPoint;

        // Add the first segment to the shape segment list.  This segment will
        // contain the "leader" for the mRNA.
        shapeSegments.add( new ShapeSegment.FlatSegment( position ) {{
            setCapacity( LEADER_LENGTH );
        }} );

        // Add the placement hints for the locations where the user can attach
        // a ribosome or an mRNA destroyer.
        ribosomePlacementHint = new PlacementHint( new Ribosome( model ) );
        mRnaDestroyerPlacementHint = new PlacementHint( new MessengerRnaDestroyer( model ) );
        shapeProperty.addObserver( new SimpleObserver() {
            public void update() {
                // This hint always sits at the beginning of the RNA strand.
                ImmutableVector2D currentMRnaFirstPointPosition = new ImmutableVector2D( firstShapeDefiningPoint.getPosition() );
                ribosomePlacementHint.setPosition( currentMRnaFirstPointPosition.getSubtractedInstance( Ribosome.OFFSET_TO_TRANSLATION_CHANNEL_ENTRANCE ).toPoint2D() );
                mRnaDestroyerPlacementHint.setPosition( currentMRnaFirstPointPosition.toPoint2D() );
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
     * Command this mRNA strand to fade away when it has become fully formed.
     * This was created for use in the 2nd tab, where mRNA is never translated
     * once it is produced.
     */
    public void setFadeAwayWhenFormed( boolean fadeAwayWhenFormed ) {
        // Just pass this through to the state machine.
        mRnaAttachmentStateMachine.setFadeAwayWhenFormed( fadeAwayWhenFormed );
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
     * @return - true if the mRNA is completely through the channel, indicating,
     *         that transcription is complete, and false if not.
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
     * Advance the destruction of the mRNA by the specified length.  This pulls
     * the strand into the lead segment much like translation does, but does
     * not move the points into new segment, it just gets rid of them.
     *
     * @param length
     */
    public boolean advanceDestruction( double length ) {

        // Error checking.
        if ( segmentWhereDestroyerConnects == null ) {
            System.out.println( getClass().getName() + " - Warning: Attempt to advance the destruction of mRNA that has no content left." );
            return true;
        }

        // Advance the destruction by reducing the length of the mRNA.
        reduceLength( length );

        // Realign the segments, since they may well have changed shape.
        if ( shapeSegments.contains( segmentWhereDestroyerConnects ) ) {
            realignSegmentsFrom( segmentWhereDestroyerConnects );
        }

        if ( shapeSegments.size() > 0 ) {
            // Since the sizes and relationships of the segments probably changed,
            // the winding algorithm needs to be rerun.
            windPointsThroughSegments();
        }

        // If there is any length left, then the destruction is not yet
        // complete.  This is a quick way to test this.
        return firstShapeDefiningPoint == lastShapeDefiningPoint;
    }

    // Reduce the length of the mRNA.  This handles both the shape segments and
    // the shape-defining points.
    private void reduceLength( double reductionAmount ) {
        if ( reductionAmount >= getLength() ) {
            // Reduce length to be zero.
            lastShapeDefiningPoint = firstShapeDefiningPoint;
            lastShapeDefiningPoint.setNextPointMass( null );
            shapeSegments.clear();
        }
        else {
            // Remove the length from the shape segments.
            segmentWhereDestroyerConnects.advanceAndRemove( reductionAmount, shapeSegments );
            // Remove the length from the shape defining points.
            for ( double amountRemoved = 0; amountRemoved < reductionAmount; ) {
                if ( lastShapeDefiningPoint.getTargetDistanceToPreviousPoint() <= reductionAmount - amountRemoved ) {
                    // Remove the last point from the list.
                    amountRemoved += lastShapeDefiningPoint.getTargetDistanceToPreviousPoint();
                    lastShapeDefiningPoint = lastShapeDefiningPoint.getPreviousPointMass();
                    lastShapeDefiningPoint.setNextPointMass( null );
                }
                else {
                    // Reduce the distance of the last point from the previous point.
                    lastShapeDefiningPoint.setTargetDistanceToPreviousPoint( lastShapeDefiningPoint.getTargetDistanceToPreviousPoint() - ( reductionAmount - amountRemoved ) );
                    amountRemoved = reductionAmount;
                }
            }
        }
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
        shapeProperty.set( BioShapeUtils.createCurvyLineFromPoints( getPointList() ) );
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
    public Point2D getRibosomeAttachmentLocation( Ribosome ribosome ) {
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
                    ImmutableVector2D vectorToPreviousPoint = new ImmutableVector2D( previousPoint.getPosition() ).getSubtractedInstance( new ImmutableVector2D( currentPoint.getPosition() ) );
                    if ( vectorToPreviousPoint.getMagnitude() == 0 ) {
                        // This point is sitting on top of the previous point,
                        // so create an arbitrary vector away from it.
                        vectorToPreviousPoint = new ImmutableVector2D( 1, 1 );
                    }
                    double scalarForceDueToPreviousPoint = ( -springConstant ) * ( currentPoint.getTargetDistanceToPreviousPoint() - currentPoint.distance( previousPoint ) );
                    ImmutableVector2D forceDueToPreviousPoint = vectorToPreviousPoint.getNormalizedInstance().getScaledInstance( scalarForceDueToPreviousPoint );
                    ImmutableVector2D vectorToNextPoint = new ImmutableVector2D( nextPoint.getPosition() ).getSubtractedInstance( new ImmutableVector2D( currentPoint.getPosition() ) );
                    if ( vectorToNextPoint.getMagnitude() == 0 ) {
                        // This point is sitting on top of the next point,
                        // so create an arbitrary vector away from it.
                        vectorToNextPoint = new ImmutableVector2D( -1, -1 );
                    }
                    double scalarForceDueToNextPoint = ( -springConstant ) * ( currentPoint.getTargetDistanceToPreviousPoint() - currentPoint.distance( nextPoint ) );
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
            mRnaAttachmentStateMachine.allRibosomesDetached();
        }
    }

    /**
     * Release this mRNA from the polymerase which is, presumably, transcribing
     * it.
     */
    public void releaseFromPolymerase() {
        mRnaAttachmentStateMachine.detach();
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

    /**
     * Initiate the translation process by setting up the segments as needed.
     * This should only be called after a ribosome that was moving to attach
     * with this mRNA arrives at the attachment point.
     *
     * @param ribosome
     */
    public void initiateTranslation( Ribosome ribosome ) {
        assert mapRibosomeToShapeSegment.containsKey( ribosome ); // State checking.

        // Set the capacity of the first segment to the size of the channel
        // through which it will be pulled plus the leader length.
        ShapeSegment firstShapeSegment = shapeSegments.get( 0 );
        assert firstShapeSegment.isFlat();
        firstShapeSegment.setCapacity( ribosome.getTranslationChannelLength() + LEADER_LENGTH );
    }

    /**
     * Initiate the destruction of this mRNA strand by setting up the segments
     * as needed.  This should only be called after an mRNA destroyer has
     * attached to the front of the mRNA strand.  Once initiated, destruction
     * cannot be stopped.
     *
     * @param messengerRnaDestroyer
     */
    public void initiateDestruction( MessengerRnaDestroyer messengerRnaDestroyer ) {
        assert this.messengerRnaDestroyer == messengerRnaDestroyer; // Shouldn't get this from unattached destroyers.

        // Set the capacity of the first segment to the size of the channel
        // through which it will be pulled plus the leader length.
        segmentWhereDestroyerConnects = shapeSegments.get( 0 );
        assert segmentWhereDestroyerConnects.isFlat();
        segmentWhereDestroyerConnects.setCapacity( messengerRnaDestroyer.getDestructionChannelLength() + LEADER_LENGTH );
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

        ShapeSegment segmentInRibosomeChannel = mapRibosomeToShapeSegment.get( ribosome );
        assert segmentInRibosomeChannel.isFlat(); // Make sure things are as we expect.

        // Add the length for each segment that precedes this ribosome.
        for ( ShapeSegment shapeSegment : shapeSegments ) {
            if ( shapeSegment == segmentInRibosomeChannel ) {
                break;
            }
            translatedLength += shapeSegment.getContainedLength();
        }

        // Add the length for the segment that is inside the translation
        // channel of this ribosome.
        translatedLength += segmentInRibosomeChannel.getContainedLength() - ( segmentInRibosomeChannel.getLowerRightCornerPos().getX() - segmentInRibosomeChannel.attachmentSite.locationProperty.get().getX() );

        return Math.max( translatedLength / getLength(), 0 );
    }

    public AttachmentSite considerProposalFrom( Ribosome ribosome ) {
        assert !mapRibosomeToShapeSegment.containsKey( ribosome ); // Shouldn't get redundant proposals from a ribosome.
        AttachmentSite returnValue = null;

        // Can't consider proposal if currently being destroyed.
        if ( messengerRnaDestroyer == null ) {
            // See if the attachment site at the leading edge of the mRNA is
            // available.
            AttachmentSite leadingEdgeAttachmentSite = shapeSegments.get( 0 ).attachmentSite;
            if ( leadingEdgeAttachmentSite.attachedOrAttachingMolecule.get() == null &&
                 leadingEdgeAttachmentSite.locationProperty.get().distance( ribosome.getEntranceOfRnaChannelPos().toPoint2D() ) < RIBOSOME_CONNECTION_DISTANCE ) {
                // This attachment site is in range and available.
                returnValue = leadingEdgeAttachmentSite;
                // Update the attachment state machine.
                mRnaAttachmentStateMachine.attachedToRibosome();
                // Enter this connection in the map.
                mapRibosomeToShapeSegment.put( ribosome, shapeSegments.get( 0 ) );
            }
        }

        return returnValue;
    }

    public AttachmentSite considerProposalFrom( MessengerRnaDestroyer messengerRnaDestroyer ) {
        assert this.messengerRnaDestroyer != messengerRnaDestroyer; // Shouldn't get redundant proposals from same destroyer.
        AttachmentSite returnValue = null;

        // Make sure that this mRNA is not already being destroyed.
        if ( this.messengerRnaDestroyer == null ) {
            // See if the attachment site at the leading edge of the mRNA is
            // available.
            AttachmentSite leadingEdgeAttachmentSite = shapeSegments.get( 0 ).attachmentSite;
            if ( leadingEdgeAttachmentSite.attachedOrAttachingMolecule.get() == null &&
                 leadingEdgeAttachmentSite.locationProperty.get().distance( messengerRnaDestroyer.getPosition() ) < MRNA_DESTROYER_CONNECT_DISTANCE ) {
                // This attachment site is in range and available.
                returnValue = leadingEdgeAttachmentSite;
                // Update the attachment state machine.
                mRnaAttachmentStateMachine.attachToDestroyer();
                // Keep track of the destroyer.
                this.messengerRnaDestroyer = messengerRnaDestroyer;
            }
        }

        return returnValue;
    }

    @Override protected AttachmentStateMachine createAttachmentStateMachine() {
        return new MessengerRnaAttachmentStateMachine( this );
    }

    public Point2D getDestroyerAttachmentLocation() {
        assert segmentWhereDestroyerConnects != null; // State checking - shouldn't be called before this is set.
        // Avoid null pointer exception.
        if ( segmentWhereDestroyerConnects == null ) {
            return new Point2D.Double( 0, 0 );
        }
        // The attachment location is at the right most side of the segment
        // minus the leader length.
        return new Point2D.Double( segmentWhereDestroyerConnects.getLowerRightCornerPos().getX() - LEADER_LENGTH,
                                   segmentWhereDestroyerConnects.getLowerRightCornerPos().getY() );
    }

    /**
     * Class that defines an observable list with some additional methods that
     * make it easier to get next and previous items and to insert items before
     * and after existing items.
     *
     * @param <T>
     */
    public static class EnhancedObservableList<T> extends ObservableList<T> {
        public T getNextItem( T item ) {
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

        public T getPreviousItem( T item ) {
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

        public void insertAfter( T existingItem, T itemToInsert ) {
            assert contains( existingItem );
            add( indexOf( existingItem ) + 1, itemToInsert );
        }

        public void insertBefore( T existingItem, T itemToInsert ) {
            assert contains( existingItem );
            add( indexOf( existingItem ), itemToInsert );
        }
    }

    /**
     * Test harness.
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
                                                      new ProteinA( new StubGeneExpressionModel() ),
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
