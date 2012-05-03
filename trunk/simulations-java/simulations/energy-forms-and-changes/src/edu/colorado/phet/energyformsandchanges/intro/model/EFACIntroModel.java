// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;

/**
 * Primary model class for the "Intro" tab of the Energy Forms and Changes
 * simulation.
 *
 * @author John Blanco
 */
public class EFACIntroModel {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    // Size of the lab table top.
    private static final double LAB_TABLE_WIDTH = 0.5; // In meters.

    // Minimum distance allowed between two objects.  The basically prevents
    // floating point issues.
    private static final double MIN_INTER_ELEMENT_DISTANCE = 1E-9; // In meters.

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Main model clock.
    protected final ConstantDtClock clock = new ConstantDtClock( 30.0 );

    // Surface of the lab bench.
    private final Shelf labBenchSurface;

    // Burners.
    private final Burner leftBurner;
    private final Burner rightBurner;

    // Movable thermal model objects.
    private final Brick brick;
    private final LeadBlock leadBlock;
    private final Beaker beaker;

    // Themometer tool box.
    private final Rectangle2D thermometerToolBox;

    // Thermometers.
    private final Thermometer thermometer1;
    private final Thermometer thermometer2;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public EFACIntroModel() {

        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                stepInTime( clockEvent.getSimulationTimeChange() );
            }
        } );

        // Add the main lab bench shelf.  The center of the shelf is the point
        // (0, 0) in model space.
        labBenchSurface = new Shelf( new Point2D.Double( -LAB_TABLE_WIDTH / 2, 0 ),
                                     LAB_TABLE_WIDTH,
                                     EnergyFormsAndChangesResources.Images.SHELF_LONG,
                                     LAB_TABLE_WIDTH * 0.015,
                                     LAB_TABLE_WIDTH * 0.05,
                                     Math.PI / 2 );

        // Add the burners.
        rightBurner = new Burner( new Point2D.Double( 0.18, 0 ) );
        leftBurner = new Burner( new Point2D.Double( 0.08, 0 ) );

        // Add and position the beaker.
        beaker = new Beaker( new ImmutableVector2D( -0.01, 0 ) );

        // Add and position the blocks
        brick = new Brick( new ImmutableVector2D( -0.1, 0 ) );
        leadBlock = new LeadBlock( new ImmutableVector2D( -0.175, 0 ) );

        // Add the thermometer tool box.  Tweak Alert: This is sized and
        // positioned to be in a reasonable location and to contain the
        // thermometer representation used in the view.
        thermometerToolBox = new Rectangle2D.Double( -0.22, 0.17, 0.1, 0.08 );

        // Add and position the thermometers.  They are initially positioned
        // inside the tool box.  Some coordination is necessary with the view
        // in order to make their initial positions look good.
        {
            double thermometerYPos = thermometerToolBox.getMinY() + 0.015;
            thermometer1 = new Thermometer( new ImmutableVector2D( thermometerToolBox.getMinX() + thermometerToolBox.getWidth() * 0.1, thermometerYPos ) );
            thermometer2 = new Thermometer( new ImmutableVector2D( thermometerToolBox.getMinX() + thermometerToolBox.getWidth() * 0.6, thermometerYPos ) );
        }
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    /**
     * Restore the initial conditions of the model.
     */
    public void reset() {
        leadBlock.position.reset();
        brick.position.reset();
        beaker.position.reset();
        thermometer1.position.reset();
        thermometer2.position.reset();
    }

    /**
     * Update the state of the model.
     *
     * @param dt
     */
    private void stepInTime( double dt ) {

        // Cause any user-movable model elements that are not supported by a
        // surface to fall (or, in some cases, jump up) towards the nearest
        // supporting surface.
        for ( UserMovableModelElement movableModelElement : Arrays.asList( leadBlock, brick, beaker ) ) {
            if ( !movableModelElement.userControlled.get() && movableModelElement.getSupportingSurface() == null && movableModelElement.position.get().getY() != 0 ) {
                double acceleration = -9.8; // meters/s*s
                double velocity = movableModelElement.verticalVelocity.get() + acceleration * dt;
                double proposedYPos = movableModelElement.position.get().getY() + velocity * dt;
                double minYPos = 0;
                Property<HorizontalSurface> potentialSupportingSurface = findBestSupportSurface( movableModelElement );
                if ( potentialSupportingSurface != null ) {
                    minYPos = potentialSupportingSurface.get().yPos;

                    // Center the movableModelElement on its new parent
                    double targetX = potentialSupportingSurface.get().getCenterX();
                    movableModelElement.setX( targetX );
                }
                if ( proposedYPos < minYPos ) {
                    // The element has landed on the ground or some other surface.
                    proposedYPos = minYPos;
                    movableModelElement.verticalVelocity.set( 0.0 );
                    if ( potentialSupportingSurface != null ) {
                        movableModelElement.setSupportingSurface( potentialSupportingSurface );
                    }
                }
                else {
                    movableModelElement.verticalVelocity.set( velocity );
                }
                movableModelElement.position.set( new ImmutableVector2D( movableModelElement.position.get().getX(), proposedYPos ) );
            }
        }

        // Update the fluid level in the beaker, which could be displaced by
        // one or more of the blocks.
        beaker.updateFluidLevel( Arrays.asList( brick.getRect(), leadBlock.getRect() ) );
    }

    /**
     * Validate the position being proposed for the given model element.  This
     * evaluates whether the proposed position would cause the model element
     * to move through another solid element, or the side of the beaker, or
     * something that would look weird to the user and, if so, prevent the odd
     * behavior from happening by returning a location that works better.
     *
     * @param modelElement
     * @param proposedPosition
     * @return
     */
    public Point2D validatePosition( RectangularMovableModelElement modelElement, Point2D proposedPosition ) {

        ImmutableVector2D translation = new ImmutableVector2D( proposedPosition ).getSubtractedInstance( modelElement.position.get() );

        // Validate against the sides of the beaker.
        if ( modelElement != beaker ) {
            // Create three rectangle to represent the two sides and the top of
            // the beaker.
            double testRectThickness = 1E-3; // 1 mm thick walls.
            Rectangle2D beakerRect = beaker.getRect();
            Rectangle2D beakerLeftSide = new Rectangle2D.Double( beakerRect.getMinX(), beaker.getRect().getMinY(), testRectThickness, beaker.getRect().getHeight() );
            Rectangle2D beakerRightSide = new Rectangle2D.Double( beaker.getRect().getMaxX() - testRectThickness, beaker.getRect().getMinY(), testRectThickness, beaker.getRect().getHeight() );
            Rectangle2D beakerBottom = new Rectangle2D.Double( beaker.getRect().getMinX(), beaker.getRect().getMinY(), beaker.getRect().getWidth(), testRectThickness );

            translation = determineAllowedTranslation( modelElement.getRect(), beakerLeftSide, translation );
            translation = determineAllowedTranslation( modelElement.getRect(), beakerRightSide, translation );
            translation = determineAllowedTranslation( modelElement.getRect(), beakerBottom, translation );
        }

        // Now check the motion against each of the blocks.
        for ( Block block : Arrays.asList( leadBlock, brick ) ) {
            if ( modelElement == block ) {
                // Don't test against self.
                continue;
            }
            if ( translation.getX() > 0 ) {

            }
        }

        Vector2D newPosition = new Vector2D( modelElement.position.get().getAddedInstance( translation ) );

        // Clamp Y position to be positive.
        newPosition.setY( Math.max( newPosition.getY(), 0 ) );

        return newPosition.toPoint2D();
    }

    /**
     * Determine the portion of a proposed translation that may occur given
     * a moving rectangle and a stationary rectangle that can block the moving
     * one.
     *
     * @param movingRect
     * @param stationaryRect
     * @param proposedTranslation
     * @return
     */
    private ImmutableVector2D determineAllowedTranslation( Rectangle2D movingRect, Rectangle2D stationaryRect, ImmutableVector2D proposedTranslation ) {

        // Parameter checking.
        if ( movingRect.intersects( stationaryRect ) ) {
            System.out.println( getClass().getName() + " - Warning: Can't test blocking if rectangles already overlap, method is being misused." );
            return new ImmutableVector2D( proposedTranslation );
        }

        double xTranslation = proposedTranslation.getX();
        double yTranslation = proposedTranslation.getY();

        // X direction.
        if ( proposedTranslation.getX() > 0 ) {

            // Check for collisions moving right.
            Line2D rightEdge = new Line2D.Double( movingRect.getMaxX(), movingRect.getMinY(), movingRect.getMaxX(), movingRect.getMaxY() );
            Shape rightEdgeSmear = projectShapeFromLine( rightEdge, proposedTranslation );

            if ( rightEdge.getX1() <= stationaryRect.getMinX() && rightEdgeSmear.intersects( stationaryRect ) ) {
                // Collision detected, limit motion.
                xTranslation = stationaryRect.getMinX() - rightEdge.getX1() - MIN_INTER_ELEMENT_DISTANCE;
            }
        }
        else if ( proposedTranslation.getX() < 0 ) {

            // Check for collisions moving left.
            Line2D leftEdge = new Line2D.Double( movingRect.getMinX(), movingRect.getMinY(), movingRect.getMinX(), movingRect.getMaxY() );
            Shape leftEdgeSmear = projectShapeFromLine( leftEdge, proposedTranslation );

            if ( leftEdge.getX1() >= stationaryRect.getMaxX() && leftEdgeSmear.intersects( stationaryRect ) ) {
                // Collision detected, limit motion.
                xTranslation = stationaryRect.getMaxX() - leftEdge.getX1() + MIN_INTER_ELEMENT_DISTANCE;
            }
        }

        // Y direction.
        if ( proposedTranslation.getY() > 0 ) {

            // Check for collisions moving up.
            Line2D movingTopEdge = new Line2D.Double( movingRect.getMinX(), movingRect.getMaxY(), movingRect.getMaxX(), movingRect.getMaxY() );
            Shape topEdgeSmear = projectShapeFromLine( movingTopEdge, proposedTranslation );

            if ( movingTopEdge.getY1() <= stationaryRect.getMinY() && topEdgeSmear.intersects( stationaryRect ) ) {
                // Collision detected, limit motion.
                yTranslation = stationaryRect.getMinY() - movingTopEdge.getY1() - MIN_INTER_ELEMENT_DISTANCE;
            }
        }
        if ( proposedTranslation.getY() < 0 ) {

            // Check for collisions moving down.
            Line2D movingBottomEdge = new Line2D.Double( movingRect.getMinX(), movingRect.getMinY(), movingRect.getMaxX(), movingRect.getMinY() );
            Shape bottomEdgeSmear = projectShapeFromLine( movingBottomEdge, proposedTranslation );

            if ( movingBottomEdge.getY1() >= stationaryRect.getMaxY() && bottomEdgeSmear.intersects( stationaryRect ) ) {
                // Collision detected, limit motion.
                yTranslation = stationaryRect.getMaxY() - movingBottomEdge.getY1() + MIN_INTER_ELEMENT_DISTANCE;
            }
        }

        return new ImmutableVector2D( xTranslation, yTranslation );
    }

    /**
     * Project a line into a 2D shape based on the provided projection vector.
     * This is a convenience function used by the code that detects potential
     * collisions between the 2D objects in model space.
     */
    private Shape projectShapeFromLine( Line2D edge, ImmutableVector2D projection ) {
        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( edge.getP1() );
        path.lineTo( edge.getX1() + projection.getX(), edge.getY1() + projection.getY() );
        path.lineTo( edge.getX2() + projection.getX(), edge.getY2() + projection.getY() );
        path.lineTo( edge.getP2() );
        path.closePath();
        return path.getGeneralPath();
    }

    public IClock getClock() {
        return clock;
    }

    public Shelf getLabBenchSurface() {
        return labBenchSurface;
    }

    public Brick getBrick() {
        return brick;
    }

    public LeadBlock getLeadBlock() {
        return leadBlock;
    }

    public Burner getLeftBurner() {
        return leftBurner;
    }

    public Burner getRightBurner() {
        return rightBurner;
    }

    public Beaker getBeaker() {
        return beaker;
    }

    public Thermometer getThermometer1() {
        return thermometer1;
    }

    public Thermometer getThermometer2() {
        return thermometer2;
    }

    public Rectangle2D getThermometerToolBox() {
        return thermometerToolBox;
    }

    private Property<HorizontalSurface> findBestSupportSurface( UserMovableModelElement element ) {
        Property<HorizontalSurface> bestOverlappingSurface = null;

        // Check each of the possible supporting elements in the model to see
        // if this element can go on top of it.
        for ( ModelElement potentialSupportingElement : Arrays.asList( leftBurner, rightBurner, brick, leadBlock, beaker ) ) {
            if ( potentialSupportingElement == element || potentialSupportingElement.isStackedUpon( element ) ) {
                // The potential supporting element is either the same as the
                // test element or is sitting on top of the test element.  In
                // either case, it can't be used to support the test element,
                // so skip it.
                continue;
            }
            if ( potentialSupportingElement != element && element.getBottomSurfaceProperty().get().overlapsWith( potentialSupportingElement.getTopSurfaceProperty().get() ) ) {

                // There is at least some overlap.  Determine if this surface
                // is the best one so far.
                double surfaceOverlap = getHorizontalOverlap( potentialSupportingElement.getTopSurfaceProperty().get(), element.getBottomSurfaceProperty().get() );

                // The following nasty 'if' clause determines if the potential
                // supporting surface is a better one than we currently have
                // based on whether we have one at all, or has more overlap
                // than the previous best choice, or is directly above the
                // current one.
                if ( bestOverlappingSurface == null ||
                     ( surfaceOverlap > getHorizontalOverlap( bestOverlappingSurface.get(), element.getBottomSurfaceProperty().get() ) &&
                       !isDirectlyAbove( bestOverlappingSurface.get(), potentialSupportingElement.getTopSurfaceProperty().get() ) ) ||
                     ( isDirectlyAbove( potentialSupportingElement.getTopSurfaceProperty().get(), bestOverlappingSurface.get() ) ) ) {
                    bestOverlappingSurface = potentialSupportingElement.getTopSurfaceProperty();
                }
            }
        }
        return bestOverlappingSurface;
    }

    public List<Block> getBlockList() {
        return Arrays.asList( brick, leadBlock );
    }

    // Get the amount of overlap in the x direction between two horizontal surfaces.
    private double getHorizontalOverlap( HorizontalSurface s1, HorizontalSurface s2 ) {
        double lowestMax = Math.min( s1.xRange.getMax(), s2.xRange.getMax() );
        double highestMin = Math.max( s1.xRange.getMin(), s2.xRange.getMin() );
        return Math.max( lowestMax - highestMin, 0 );
    }

    // Returns true if surface s1's center is above surface s2.
    private boolean isDirectlyAbove( HorizontalSurface s1, HorizontalSurface s2 ) {
        return s2.xRange.contains( s1.getCenterX() ) && s1.yPos > s2.yPos;
    }
}
