// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.GeneExpressionModel;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.PointMass;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.ShapeSegment;

/**
 * Biomolecule that is a represented as a wound up strand.  Generally, this
 * refers to some sort of RNA.
 *
 * @author John Blanco
 */
public abstract class WindingBiomolecule extends MobileBiomolecule {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    // Color used by this molecule.  Since mRNA is depicted as a line and not
    // as a closed shape, a transparent color is used.  This enables reuse of
    // generic biomolecule classes.
    protected static final Color NOMINAL_COLOR = new Color( 0, 0, 0, 0 );

    // Standard distance between points that define the shape.  This is done to
    // keep the number of points reasonable and make the shape-defining
    // algorithm consistent.
    protected static final double INTER_POINT_DISTANCE = 50; // In picometers, empirically determined.

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    protected PointMass firstShapeDefiningPoint = null;
    protected PointMass lastShapeDefiningPoint = null;

    // List of the shape segments that define the outline shape.
    public final WindingBiomolecule.EnhancedObservableList<ShapeSegment> shapeSegments = new WindingBiomolecule.EnhancedObservableList<ShapeSegment>();

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    /**
     * Constructor.
     *
     * @param initialShape
     */
    public WindingBiomolecule( GeneExpressionModel model, Shape initialShape, Point2D position ) {
        super( model, initialShape, NOMINAL_COLOR );

        // Add first shape defining point to the point list.
        firstShapeDefiningPoint = new PointMass( position, 0 );
        lastShapeDefiningPoint = firstShapeDefiningPoint;
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

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
     * This is the "winding algorithm" that positions the points that define
     * the shape of the mRNA within the shape segments.  The combination of
     * this algorithm and the shape segments allow the mRNA to look reasonable
     * when it is being synthesized and when it is being transcribed.
     */
    protected void windPointsThroughSegments() {
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

        Random rand = new Random( 8 ); // Seed chosen empirically for best looking mRNA.
        PointMass currentPoint = firstPoint;
        do {
            // Randomly position the points within the segment.
            currentPoint.setPosition( bounds.getMinX() + rand.nextDouble() * bounds.getWidth(),
                                      bounds.getMinY() + rand.nextDouble() * bounds.getHeight() );
            currentPoint = currentPoint.getNextPointMass();
        } while ( currentPoint != lastPoint && currentPoint != null );
    }

    /**
     * Realign all the segments, making sure that the end of one connects to
     * the beginning of another, using the last segment on the list as the
     * starting point.
     */
    protected void realignSegmentsFromEnd() {
        ArrayList<ShapeSegment> copyOfShapeSegments = new ArrayList<ShapeSegment>( shapeSegments );
        Collections.reverse( copyOfShapeSegments );
        for ( int i = 0; i < copyOfShapeSegments.size() - 1; i++ ) {
            // Assumes that the shape segments attach to one another in such
            // a way that they chain from the upper left to the lower right.
            copyOfShapeSegments.get( i + 1 ).setLowerRightCornerPos( copyOfShapeSegments.get( i ).getUpperLeftCornerPos() );
        }
    }

    protected ShapeSegment getLastShapeSegment() {
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
     * Realign the positions of all segments starting from the given segment
     * and working forward and backward through the segment list.
     *
     * @param segmentToAlignFrom
     */
    protected void realignSegmentsFrom( ShapeSegment segmentToAlignFrom ) {
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

    //-------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //-------------------------------------------------------------------------

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
}
