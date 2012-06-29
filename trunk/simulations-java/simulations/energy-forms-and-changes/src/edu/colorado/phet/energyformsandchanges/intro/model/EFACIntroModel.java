// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.Color;
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
import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;
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

    // Threshold of temperature difference between the bodies in a multi-body
    // system below which energy can be exchanged with air.
    private static final double MIN_TEMPERATURE_DIFF_FOR_MULTI_BODY_AIR_ENERGY_EXCHANGE = 0.5; // In degrees C, empirically determined.

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Main model clock.
    private final ConstantDtClock clock = new ConstantDtClock( EFACConstants.FRAMES_PER_SECOND );

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

    // Air.
    private final Air air;

    // Boolean for tracking whether the energy chunks are visible to the user.
    public final BooleanProperty energyChunksVisible = new BooleanProperty( false );

    // Lists of thermal model elements for easy iteration.
    private final List<ThermalEnergyContainer> thermalEnergyContainers = new ArrayList<ThermalEnergyContainer>();
    private final List<RectangularThermalMovableModelElement> movableThermalEnergyContainers = new ArrayList<RectangularThermalMovableModelElement>();

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public EFACIntroModel() {

        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                stepInTime( clockEvent.getSimulationTimeChange() );
            }
        } );

        // Add the air.
        air = new Air( clock, energyChunksVisible );

        // Add the burners.
        rightBurner = new Burner( clock, new ImmutableVector2D( 0.18, 0 ), energyChunksVisible );
        leftBurner = new Burner( clock, new ImmutableVector2D( 0.08, 0 ), energyChunksVisible );

        // Add and position the blocks
        brick = new Brick( clock, new ImmutableVector2D( -0.1, 0 ), energyChunksVisible );
        ironBlock = new IronBlock( clock, new ImmutableVector2D( -0.175, 0 ), energyChunksVisible );

        // Add and position the beaker.
        {
            List<RectangularThermalMovableModelElement> listOfThingsThatCanGoInBeaker = new ArrayList<RectangularThermalMovableModelElement>() {{
                add( brick );
                add( ironBlock );
            }};
            beaker = new Beaker( clock, new ImmutableVector2D( -0.015, 0 ), listOfThingsThatCanGoInBeaker, energyChunksVisible );
        }

        // Put all the thermal containers on a list for easy iteration.
        thermalEnergyContainers.add( brick );
        thermalEnergyContainers.add( ironBlock );
        thermalEnergyContainers.add( beaker );
        thermalEnergyContainers.add( air );
        movableThermalEnergyContainers.add( brick );
        movableThermalEnergyContainers.add( ironBlock );
        movableThermalEnergyContainers.add( beaker );

        // Add the thermometers.  Set initial position to be off screen.
        // Subsequent initialization will put them into the thermometer box.
        thermometer1 = new Thermometer( this, new ImmutableVector2D( 100, 100 ) );
        thermometer2 = new Thermometer( this, new ImmutableVector2D( 100, 100 ) );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    /**
     * Restore the initial conditions of the model.
     */
    public void reset() {
        energyChunksVisible.reset();
        air.reset();
        leftBurner.reset();
        rightBurner.reset();
        ironBlock.reset();
        brick.reset();
        beaker.reset();
        thermometer1.reset();
        thermometer2.reset();
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

        // Update heat/cool limits on the burners.
        leftBurner.updateHeatCoolLimits( dt, ironBlock, brick, beaker );
        rightBurner.updateHeatCoolLimits( dt, ironBlock, brick, beaker );

        //---------------------------------------------------------------------
        // Energy and Energy Chunk Exchange
        //---------------------------------------------------------------------

        // Loop through all the movable thermal energy containers and have them
        // exchange energy with one another.
        for ( ThermalEnergyContainer ec1 : movableThermalEnergyContainers ) {
            for ( ThermalEnergyContainer ec2 : movableThermalEnergyContainers.subList( movableThermalEnergyContainers.indexOf( ec1 ) + 1, movableThermalEnergyContainers.size() ) ) {
                ec1.exchangeEnergyWith( ec2, dt );
            }
        }

        // Exchange thermal energy between the burners and the various movable
        // thermal energy containers.
        for ( Burner burner : Arrays.asList( leftBurner, rightBurner ) ) {
            if ( burner.areAnyOnTop( ironBlock, brick, beaker ) ) {
                for ( ThermalEnergyContainer movableThermalEnergyContainer : movableThermalEnergyContainers ) {
                    burner.addOrRemoveEnergy( movableThermalEnergyContainer, dt );
                }
            }
            else {
                // Nothing on burner, so heat/cool the air.
                burner.addOrRemoveEnergyToFromAir( air, dt );
            }
        }

        // Exchange energy chunks between burners and non-air energy containers.
        for ( RectangularThermalMovableModelElement thermalModelElement : Arrays.asList( ironBlock, brick, beaker ) ) {
            for ( Burner burner : Arrays.asList( leftBurner, rightBurner ) ) {
                if ( thermalModelElement.getSystemEnergyChunkBalance() < 0 && burner.inContactWith( thermalModelElement ) && burner.canSupplyEnergyChunk() ) {
                    thermalModelElement.addEnergyChunk( burner.extractClosestEnergyChunk( thermalModelElement.getCenterPoint() ) );
                }
                else if ( thermalModelElement.getSystemEnergyChunkBalance() > 0 && burner.inContactWith( thermalModelElement ) && burner.canAcceptEnergyChunk() ) {
                    burner.addEnergyChunk( thermalModelElement.extractClosestEnergyChunk( burner.getFlameIceRect() ) );
                }
            }
        }

        // Exchange energy chunks between movable thermal energy containers.
        for ( RectangularThermalMovableModelElement ec1 : movableThermalEnergyContainers ) {
            for ( RectangularThermalMovableModelElement ec2 : movableThermalEnergyContainers.subList( movableThermalEnergyContainers.indexOf( ec1 ) + 1, movableThermalEnergyContainers.size() ) ) {
                if ( ec1.getThermalContactArea().getThermalContactLength( ec2.getThermalContactArea() ) > 0 ) {
                    // Exchange chunks if appropriate.
                    if ( ec1.getEnergyChunkBalance() > 0 && ec2.getEnergyChunkBalance() < 0 ) {
                        ec2.addEnergyChunk( ec1.extractClosestEnergyChunk( ec2.getThermalContactArea().getBounds() ) );
                    }
                    else if ( ec1.getEnergyChunkBalance() < 0 && ec2.getEnergyChunkBalance() > 0 ) {
                        ec1.addEnergyChunk( ec2.extractClosestEnergyChunk( ec1.getThermalContactArea().getBounds() ) );
                    }
                }
            }
        }

        // Exchange energy and energy chunks between the movable thermal energy
        // containers and the air.
        for ( RectangularThermalMovableModelElement movableEnergyContainer : movableThermalEnergyContainers ) {

            // Set up some variables that are used to decide whether or not
            // energy should be exchanged with air.
            boolean contactWithOtherMovableElement = false;
            boolean immersedInBeaker = false;
            double maxTemperatureDifference = 0;

            // Figure out the max temperature difference between touching
            // energy containers.
            for ( ThermalEnergyContainer otherMovableEnergyContainer : movableThermalEnergyContainers ) {
                if ( movableEnergyContainer == otherMovableEnergyContainer ) {
                    continue;
                }
                if ( movableEnergyContainer.getThermalContactArea().getThermalContactLength( otherMovableEnergyContainer.getThermalContactArea() ) > 0 ) {
                    contactWithOtherMovableElement = true;
                    maxTemperatureDifference = Math.max( Math.abs( movableEnergyContainer.getTemperature() - otherMovableEnergyContainer.getTemperature() ), maxTemperatureDifference );
                }
            }

            if ( beaker.getThermalContactArea().getBounds().contains( movableEnergyContainer.getRect() ) ) {
                // This model element is immersed in the beaker.
                immersedInBeaker = true;
            }

            // Exchange energy chunks with the air if appropriate conditions met.
            if ( !contactWithOtherMovableElement || ( contactWithOtherMovableElement && !immersedInBeaker && maxTemperatureDifference < MIN_TEMPERATURE_DIFF_FOR_MULTI_BODY_AIR_ENERGY_EXCHANGE ) ) {
                air.exchangeEnergyWith( movableEnergyContainer, dt );
                if ( movableEnergyContainer.getEnergyChunkBalance() > 0 && air.canAcceptEnergyChunk() ) {
                    ImmutableVector2D pointAbove = new ImmutableVector2D( movableEnergyContainer.getCenterPoint().getX(), movableEnergyContainer.getRect().getMaxY() );
                    air.addEnergyChunk( movableEnergyContainer.extractClosestEnergyChunk( pointAbove ) );
                }
                else if ( movableEnergyContainer.getEnergyChunkBalance() < 0 && movableEnergyContainer.getTemperature() < air.getTemperature() && air.canSupplyEnergyChunk() ) {
                    movableEnergyContainer.addEnergyChunk( air.requestEnergyChunk( movableEnergyContainer.getCenterPoint() ) );
                }
            }
        }

        // Exchange energy chunks between the air and the burners.
        for ( Burner burner : Arrays.asList( leftBurner, rightBurner ) ) {
            if ( burner.getEnergyChunkCountForAir() > 0 ) {
                air.addEnergyChunk( burner.extractClosestEnergyChunk( burner.getCenterPoint() ) );
            }
            else if ( burner.getEnergyChunkCountForAir() < 0 ) {
                burner.addEnergyChunk( air.requestEnergyChunk( burner.getCenterPoint() ) );
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
        for ( ThermalEnergyContainer thermalEnergyContainer : Arrays.asList( ironBlock, brick, beaker, air ) ) {
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

    public Air getAir() {
        return air;
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

    public double getTemperatureAtLocation( ImmutableVector2D location ) {
        Point2D locationAsPoint = location.toPoint2D();
        for ( Block block : getBlockList() ) {
            if ( block.getProjectedShape().contains( locationAsPoint ) ) {
                return block.getTemperature();
            }
        }
        if ( beaker.getThermalContactArea().getBounds().contains( locationAsPoint ) ) {
            return beaker.getTemperature();
        }
        for ( Burner burner : Arrays.asList( leftBurner, rightBurner ) ) {
            if ( burner.getFlameIceRect().contains( locationAsPoint ) ) {
                return burner.getTemperature();
            }
        }
        return air.getTemperature();
    }

    public Color getElementColorAtLocation( ImmutableVector2D location ) {
        Point2D locationAsPoint = location.toPoint2D();
        for ( Block block : getBlockList() ) {
            if ( block.getProjectedShape().contains( locationAsPoint ) ) {
                return block.getColor();
            }
        }
        if ( beaker.getThermalContactArea().getBounds().contains( locationAsPoint ) ) {
            return EFACConstants.WATER_COLOR;
        }
        for ( Burner burner : Arrays.asList( leftBurner, rightBurner ) ) {
            if ( burner.getFlameIceRect().contains( locationAsPoint ) ) {
                return PhetColorScheme.RED_COLORBLIND;
            }
        }
        // Return the color for air, which is the background color.
        return EFACConstants.FIRST_TAB_BACKGROUND_COLOR;
    }
}
