// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.geneexpressionbasics.common.model.BioShapeUtils;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines.AttachmentStateMachine;
import edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines.MessengerRnaFragmentAttachmentStateMachine;

/**
 * Class that represents a fragment of messenger ribonucleic acid, or mRNA, in
 * the model.  The fragments exist for a short time as mRNA is being destroyed,
 * but can't be translated.
 *
 * @author John Blanco
 */
public class MessengerRnaFragment extends MobileBiomolecule {

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

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private PointMass firstShapeDefiningPoint = null;
    private PointMass lastShapeDefiningPoint = null;

    // List of the shape segments that define the outline shape.
    public final ShapeSegment.SquareSegment shapeSegment;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    /**
     * Constructor.  This creates the mRNA as a single point, with the intention
     * of growing it.
     *
     * @param position
     */
    public MessengerRnaFragment( final GeneExpressionModel model, Point2D position ) {
        super( model, new DoubleGeneralPath( position ).getGeneralPath(), NOMINAL_COLOR );
        shapeSegment = new ShapeSegment.SquareSegment( position );

        // Add first shape defining point to the point list.
        firstShapeDefiningPoint = new PointMass( position, 0 );
        lastShapeDefiningPoint = firstShapeDefiningPoint;
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

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
        shapeSegment.add( length );

        // Now that the points and shape segment are updated, run the algorithm
        // that winds the points through the shapes to produce the overall
        // shape of the strand.
        windPointsThroughSegments();
    }

    /**
     * Set the position of the lower right end of the mRNA fragment.
     *
     * @param p
     */
    public void setLowerRightPosition( Point2D p ) {
        shapeSegment.setLowerRightCornerPos( p );
    }

    public void setLowerRightPosition( double x, double y ) {
        setLowerRightPosition( new Point2D.Double( x, y ) );
    }

    /**
     * This is the "winding algorithm" that positions the points that define
     * the shape of the mRNA within the shape segments.  The combination of
     * this algorithm and the shape segments allow the mRNA to look reasonable
     * when it is being synthesized and when it is being transcribed.
     */
    private void windPointsThroughSegments() {
        randomizePointPositionsInRectangle( firstShapeDefiningPoint, lastShapeDefiningPoint, shapeSegment.getBounds() );
        runSpringAlgorithm( firstShapeDefiningPoint, lastShapeDefiningPoint, shapeSegment.getBounds() );

        // Update the shape property based on the newly positioned points.
        shapeProperty.set( BioShapeUtils.createCurvyLineFromPoints( getPointList() ) );
    }

    /**
     * Randomly position a set of points inside a rectangle.  The first point
     * is positioned at the upper left, the last at the lower right, and all
     * points in between are randomly placed.
     * <p/>
     * TODO: This code is duplicated in MessengerRna, consolidate into utils class or something.
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
     * <p/>
     * TODO: This code is duplicated in MessengerRna, consolidate into utils class or something.
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
     * Release this mRNA fragment from the destroyer molecule.
     */
    public void releaseFromDestroyer() {
        attachmentStateMachine.detach();
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

    @Override protected AttachmentStateMachine createAttachmentStateMachine() {
        return new MessengerRnaFragmentAttachmentStateMachine( this );
    }
}
