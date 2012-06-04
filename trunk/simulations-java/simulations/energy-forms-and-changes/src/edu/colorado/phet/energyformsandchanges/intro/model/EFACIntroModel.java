// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.intro.view.BlockNode;

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

    // Minimum distance allowed between two objects.  The basically prevents
    // floating point issues.
    private static final double MIN_INTER_ELEMENT_DISTANCE = 1E-9; // In meters.

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Main model clock.
    protected final ConstantDtClock clock = new ConstantDtClock( EFACConstants.FRAMES_PER_SECOND );

    // Burners.
    private final Burner leftBurner;
    private final Burner rightBurner;

    // Movable thermal model objects.
    private final Brick brick;
    private final IronBlock ironBlock;
    private final Beaker beaker;

    // Thermometers.
    private final Thermometer thermometer1;
    private final Thermometer thermometer2;

    // List of energy chucks.
    public final ObservableList<EnergyChunk> energyChunkList = new ObservableList<EnergyChunk>();

    // Boolean for tracking whether the energy chunks are visible to the user.
    public final BooleanProperty energyChunksVisible = new BooleanProperty( false );

    // List of model element that can contain and exchange energy.
    private List<ThermalEnergyContainer> energyContainerList = new ArrayList<ThermalEnergyContainer>();

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public EFACIntroModel() {

        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                stepInTime( clockEvent.getSimulationTimeChange() );
            }
        } );

        // Add the burners.
        rightBurner = new Burner( clock, new ImmutableVector2D( 0.18, 0 ), energyChunksVisible );
        leftBurner = new Burner( clock, new ImmutableVector2D( 0.08, 0 ), energyChunksVisible );

        // Add and position the beaker.
        beaker = new Beaker( clock, new ImmutableVector2D( -0.015, 0 ), energyChunksVisible );

        // Add and position the blocks
        brick = new Brick( clock, new ImmutableVector2D( -0.1, 0 ), energyChunksVisible );
        ironBlock = new IronBlock( clock, new ImmutableVector2D( -0.175, 0 ), energyChunksVisible );

        // Put all the thermal containers on a list for easy iteration.
        energyContainerList.add( leftBurner );
        energyContainerList.add( rightBurner );
        energyContainerList.add( brick );
        energyContainerList.add( ironBlock );
        energyContainerList.add( beaker );

        // Add the thermometers.  They should reside in the tool box when
        // the sim starts up or is reset, so their position here doesn't much
        // matter, since it will be changed by the view.
        thermometer1 = new Thermometer( new ImmutableVector2D( 0, 0 ) );
        thermometer2 = new Thermometer( new ImmutableVector2D( 0, 0 ) );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    /**
     * Restore the initial conditions of the model.
     */
    public void reset() {
        leftBurner.reset();
        rightBurner.reset();
        ironBlock.reset();
        brick.reset();
        beaker.reset();
        thermometer1.reset();
        thermometer2.reset();
        energyChunksVisible.reset();
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
        for ( UserMovableModelElement movableModelElement : Arrays.asList( ironBlock, brick, beaker ) ) {
            if ( !movableModelElement.userControlled.get() && movableModelElement.getSupportingSurface() == null && movableModelElement.position.get().getY() != 0 ) {
                double minYPos = 0;

                // Determine whether there is something below this element that
                // it can land upon.
                Property<HorizontalSurface> potentialSupportingSurface = findBestSupportSurface( movableModelElement );
                if ( potentialSupportingSurface != null ) {
                    minYPos = potentialSupportingSurface.get().yPos;

                    // Center the movableModelElement above its new parent
                    double targetX = potentialSupportingSurface.get().getCenterX();
                    movableModelElement.setX( targetX );
                }

                // Calculate a proposed Y position based on gravitational falling.
                double acceleration = -9.8; // meters/s*s
                double velocity = movableModelElement.verticalVelocity.get() + acceleration * dt;
                double proposedYPos = movableModelElement.position.get().getY() + velocity * dt;
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
        beaker.updateFluidLevel( Arrays.asList( brick.getRect(), ironBlock.getRect() ) );

        // Update the temperature seen by the thermometers.
        for ( Thermometer thermometer : Arrays.asList( thermometer1, thermometer2 ) ) {
            boolean touchingSomething = false;
            for ( ThermalEnergyContainer element : Arrays.asList( beaker, brick, ironBlock, leftBurner, rightBurner ) ) {
                if ( element.getThermalContactArea().getBounds().contains( thermometer.position.get().toPoint2D() ) ) {
                    thermometer.sensedTemperature.set( element.getTemperature() );
                    touchingSomething = true;
                    break;
                }
            }

            if ( !touchingSomething ) {
                // Set to the air temperature.  TODO: Uses room temperature for now, will need air temp eventually.
                thermometer.sensedTemperature.set( EFACConstants.ROOM_TEMPERATURE );
            }
        }

        // Update heat/cool limits on the burners.
        leftBurner.updateHeatCoolLimits( ironBlock, brick, beaker );
        rightBurner.updateHeatCoolLimits( ironBlock, brick, beaker );

        // Loop through all the energy containers and have them exchange energy
        // with one another.
        for ( ThermalEnergyContainer ec1 : energyContainerList ) {
            for ( ThermalEnergyContainer ec2 : energyContainerList.subList( energyContainerList.indexOf( ec1 ) + 1, energyContainerList.size() ) ) {
                ec1.exchangeEnergyWith( ec2, dt );
            }
        }
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
    public Point2D validatePosition( RectangularThermalMovableModelElement modelElement, Point2D proposedPosition ) {

        ImmutableVector2D translation = new ImmutableVector2D( proposedPosition ).getSubtractedInstance( modelElement.position.get() );

        // Validate against the sides of the beaker.
        if ( modelElement != beaker ) {

            // Calculate an extension amount to apply to the rectangles that
            // define the beaker collision areas.  This is done to avoid issues
            // in the view with Z-order.  For now, this assumes only beakers
            // and blocks in the model.
            double extensionAmount = Block.SURFACE_WIDTH * BlockNode.PERSPECTIVE_EDGE_PROPORTION * Math.cos( BlockNode.PERSPECTIVE_ANGLE ) / 2;

            // Create three rectangles to represent the two sides and the top
            // of the beaker.
            double testRectThickness = 1E-3; // 1 mm thick walls.
            Rectangle2D beakerRect = beaker.getRect();
            Rectangle2D beakerLeftSide = new Rectangle2D.Double( beakerRect.getMinX() - extensionAmount,
                                                                 beaker.getRect().getMinY(),
                                                                 testRectThickness + extensionAmount * 2,
                                                                 beaker.getRect().getHeight() + extensionAmount );
            Rectangle2D beakerRightSide = new Rectangle2D.Double( beaker.getRect().getMaxX() - testRectThickness - extensionAmount,
                                                                  beaker.getRect().getMinY(),
                                                                  testRectThickness + extensionAmount * 2,
                                                                  beaker.getRect().getHeight() + extensionAmount );
            Rectangle2D beakerBottom = new Rectangle2D.Double( beaker.getRect().getMinX(), beaker.getRect().getMinY(), beaker.getRect().getWidth(), testRectThickness );

            // Do not restrict the model element's motion in positive Y
            // direction if the beaker is sitting on top of the model element -
            // the beaker will simply be lifted up.
            boolean restrictPositiveY = !beaker.isStackedUpon( modelElement );

            // Clamp the translation based on the beaker position.
            translation = determineAllowedTranslation( modelElement.getRect(), beakerLeftSide, translation, restrictPositiveY );
            translation = determineAllowedTranslation( modelElement.getRect(), beakerRightSide, translation, restrictPositiveY );
            translation = determineAllowedTranslation( modelElement.getRect(), beakerBottom, translation, restrictPositiveY );
        }

        // Now check the model element's motion against each of the blocks.
        for ( Block block : Arrays.asList( ironBlock, brick ) ) {
            if ( modelElement == block ) {
                // Don't test against self.
                continue;
            }

            // Do not restrict the model element's motion in positive Y
            // direction if the tested block is sitting on top of the model
            // element - the block will simply be lifted up.
            boolean restrictPositiveY = !block.isStackedUpon( modelElement );

            // Clamp the translation based on the test block's position.
            translation = determineAllowedTranslation( modelElement.getRect(), block.getRect(), translation, restrictPositiveY );
        }

        // Determine the new position based on the allowed translation.
        Vector2D newPosition = new Vector2D( modelElement.position.get().getAddedInstance( translation ) );

        // Clamp Y position to be positive.
        newPosition.setY( Math.max( newPosition.getY(), 0 ) );

        return newPosition.toPoint2D();
    }

    public void dumpEnergies() {
        for ( ThermalEnergyContainer thermalEnergyContainer : Arrays.asList( ironBlock, brick, beaker ) ) {
            System.out.println( thermalEnergyContainer.getClass().getName() + " - energy = " + thermalEnergyContainer.getEnergy() );
        }
    }

    /**
     * Determine the portion of a proposed translation that may occur given
     * a moving rectangle and a stationary rectangle that can block the moving
     * one.
     *
     * @param movingRect
     * @param stationaryRect
     * @param proposedTranslation
     * @param restrictPosY        Boolean that controls whether the positive Y
     *                            direction is restricted.  This is often set
     *                            false if there is another model element on
     *                            top of the one being tested.
     * @return
     */
    private ImmutableVector2D determineAllowedTranslation( Rectangle2D movingRect, Rectangle2D stationaryRect, ImmutableVector2D proposedTranslation, boolean restrictPosY ) {

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
        if ( proposedTranslation.getY() > 0 && restrictPosY ) {

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

    public Brick getBrick() {
        return brick;
    }

    public IronBlock getIronBlock() {
        return ironBlock;
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

    private Property<HorizontalSurface> findBestSupportSurface( UserMovableModelElement element ) {
        Property<HorizontalSurface> bestOverlappingSurface = null;

        // Check each of the possible supporting elements in the model to see
        // if this element can go on top of it.
        for ( ModelElement potentialSupportingElement : Arrays.asList( leftBurner, rightBurner, brick, ironBlock, beaker ) ) {
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
        return Arrays.asList( brick, ironBlock );
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

    public List<Thermometer> getThermometers() {
        return Arrays.asList( thermometer1, thermometer2 );
    }
}
