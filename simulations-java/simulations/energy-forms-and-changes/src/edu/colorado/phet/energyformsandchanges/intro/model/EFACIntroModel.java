// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.common.model.Beaker;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;
import edu.colorado.phet.energyformsandchanges.common.model.ITemperatureModel;
import edu.colorado.phet.energyformsandchanges.common.view.BurnerNode;
import edu.colorado.phet.energyformsandchanges.common.view.BurnerStandNode;
import edu.colorado.phet.energyformsandchanges.intro.view.BlockNode;

/**
 * Primary model class for the "Intro" tab of the Energy Forms and Changes
 * simulation.
 *
 * @author John Blanco
 */
public class EFACIntroModel implements ITemperatureModel {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    // Minimum distance allowed between two objects.  The basically prevents
    // floating point issues.
    private static final double MIN_INTER_ELEMENT_DISTANCE = 1E-9; // In meters.

    // Threshold of temperature difference between the bodies in a multi-body
    // system below which energy can be exchanged with air.
    private static final double MIN_TEMPERATURE_DIFF_FOR_MULTI_BODY_AIR_ENERGY_EXCHANGE = 0.5; // In degrees C, empirically determined.

    // Initial thermometer location, intended to be away from any model objects.
    private static final Vector2D INITIAL_THERMOMETER_LOCATION = new Vector2D( 100, 100 );

    public static final int NUM_THERMOMETERS = 3;

    private static final double BEAKER_WIDTH = 0.085; // In meters.
    private static final double BEAKER_HEIGHT = BEAKER_WIDTH * 1.1;

    // TODO: Remove this and all related code when performance issues are resolved.
    private static final boolean ENABLE_INTERNAL_PROFILING = false;

    // For operations that require random behavior.
    private static final Random RAND = new Random();

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
    private final BeakerContainer beaker;

    // Thermometers.
    public final List<ElementFollowingThermometer> thermometers = new ArrayList<ElementFollowingThermometer>();

    // Air.
    private final Air air;

    // Boolean property that controls whether the energy chunks are visible to the user.
    public final BooleanProperty energyChunksVisible = new BooleanProperty( false );

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
        rightBurner = new Burner( clock, new Vector2D( 0.18, 0 ), energyChunksVisible );
        leftBurner = new Burner( clock, new Vector2D( 0.08, 0 ), energyChunksVisible );

        // Add and position the blocks
        brick = new Brick( clock, new Vector2D( -0.1, 0 ), energyChunksVisible );
        ironBlock = new IronBlock( clock, new Vector2D( -0.175, 0 ), energyChunksVisible );

        // Add and position the beaker.
        {
            List<RectangularThermalMovableModelElement> listOfThingsThatCanGoInBeaker = new ArrayList<RectangularThermalMovableModelElement>() {{
                add( brick );
                add( ironBlock );
            }};
            beaker = new BeakerContainer( clock, new Vector2D( -0.015, 0 ), BEAKER_WIDTH, BEAKER_HEIGHT, listOfThingsThatCanGoInBeaker, energyChunksVisible );
        }

        // Put all the thermal containers on a list for easy iteration.
        List<ThermalEnergyContainer> thermalEnergyContainers = new ArrayList<ThermalEnergyContainer>();
        thermalEnergyContainers.add( brick );
        thermalEnergyContainers.add( ironBlock );
        thermalEnergyContainers.add( beaker );
        thermalEnergyContainers.add( air );
        movableThermalEnergyContainers.add( brick );
        movableThermalEnergyContainers.add( ironBlock );
        movableThermalEnergyContainers.add( beaker );

        // Add the thermometers.
        for ( int i = 0; i < NUM_THERMOMETERS; i++ ) {
            final ElementFollowingThermometer thermometer = new ElementFollowingThermometer( this, INITIAL_THERMOMETER_LOCATION, false );
            thermometers.add( thermometer );

            // Add handling for a special case where the user drops something
            // (generally a block) in the beaker behind this thermometer.
            // The action is to automatically move the thermometer to a
            // location where it continues to sense the beaker temperature.
            // This was requested after interviews.
            thermometer.sensedElementColor.addObserver( new ChangeObserver<Color>() {
                double blockWidthIncludingPerspective = ironBlock.getProjectedShape().getBounds2D().getWidth();

                public void update( Color newColor, Color oldColor ) {
                    DoubleRange xRange = new DoubleRange( beaker.getRect().getCenterX() - blockWidthIncludingPerspective / 2,
                                                          beaker.getRect().getCenterX() + blockWidthIncludingPerspective / 2 );
                    if ( oldColor == EFACConstants.WATER_COLOR_IN_BEAKER && !thermometer.userControlled.get() && xRange.contains( thermometer.position.get().getX() )) {
                        thermometer.userControlled.set( true ); // Must toggle userControlled to enable element following.
                        thermometer.position.set( new Vector2D( beaker.getRect().getMaxX() - 0.01, beaker.getRect().getMinY() + beaker.getRect().getHeight() * 0.33 ) );
                        thermometer.userControlled.set( false ); // Must toggle userControlled to enable element following.
                    }
                }
            } );
        }
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    /**
     * Restore the initial conditions of the model.
     */
    public void reset() {
        clock.resetSimulationTime();
        energyChunksVisible.reset();
        air.reset();
        leftBurner.reset();
        rightBurner.reset();
        ironBlock.reset();
        brick.reset();
        beaker.reset();
        for ( ElementFollowingThermometer thermometer : thermometers ) {
            thermometer.reset();
        }
    }

    private long previousTime = 0;
    private int TIME_ARRAY_LENGTH = 100;
    private long[] times = new long[TIME_ARRAY_LENGTH];
    private int countUnderMin = 0;

    /**
     * Update the state of the model.
     *
     * @param dt Time step.
     */
    private void stepInTime( double dt ) {

        if ( ENABLE_INTERNAL_PROFILING ) {
            long time = System.currentTimeMillis();
            times[countUnderMin++] = time - previousTime;
            if ( previousTime != 0 && time - previousTime > 48 || countUnderMin + 1 >= TIME_ARRAY_LENGTH ) {
                System.out.println( "----------------------" );
                for ( int i = 0; i < countUnderMin; i++ ) {
                    System.out.print( times[i] + " " );
                }
                System.out.println();
                countUnderMin = 0;
            }
            previousTime = time;
        }

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
                        potentialSupportingSurface.get().addElementToSurface( movableModelElement );
                    }
                }
                else {
                    movableModelElement.verticalVelocity.set( velocity );
                }
                movableModelElement.position.set( new Vector2D( movableModelElement.position.get().getX(), proposedYPos ) );
            }
        }

        // Update the fluid level in the beaker, which could be displaced by
        // one or more of the blocks.
        beaker.updateFluidLevel( Arrays.asList( brick.getRect(), ironBlock.getRect() ) );

        //=====================================================================
        // Energy and Energy Chunk Exchange
        //=====================================================================

        // Note: The original intent was to design all the energy containers
        // such that the order of the exchange didn't matter, nor who was
        // exchanging with whom.  This turned out to be a lot of extra work to
        // maintain, and was eventually abandoned.  So, the order and nature of
        // the exchanged below should be maintained unless there is a good
        // reason not to, and any changes should be well tested.

        // Loop through all the movable thermal energy containers and have them
        // exchange energy with one another.
        for ( ThermalEnergyContainer ec1 : movableThermalEnergyContainers ) {
            for ( ThermalEnergyContainer ec2 : movableThermalEnergyContainers.subList( movableThermalEnergyContainers.indexOf( ec1 ) + 1, movableThermalEnergyContainers.size() ) ) {
                ec1.exchangeEnergyWith( ec2, dt );
            }
        }

        // Exchange thermal energy between the burners and the other thermal
        // model elements, including air.
        for ( Burner burner : Arrays.asList( leftBurner, rightBurner ) ) {
            if ( burner.areAnyOnTop( ironBlock, brick, beaker ) ) {
                for ( ThermalEnergyContainer movableThermalEnergyContainer : movableThermalEnergyContainers ) {
                    burner.addOrRemoveEnergyToFromObject( movableThermalEnergyContainer, dt );
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
                if ( burner.inContactWith( thermalModelElement ) ) {
                    if ( burner.canSupplyEnergyChunk() && ( burner.getEnergyChunkBalanceWithObjects() > 0 || thermalModelElement.getEnergyChunkBalance() < 0 ) ) {
                        // Push an energy chunk into the item on the burner.
                        thermalModelElement.addEnergyChunk( burner.extractClosestEnergyChunk( thermalModelElement.getCenterPoint() ) );
                    }
                    else if ( burner.canAcceptEnergyChunk() && ( burner.getEnergyChunkBalanceWithObjects() < 0 || thermalModelElement.getEnergyChunkBalance() > 0 ) ) {
                        // Extract an energy chunk from the model element.
                        EnergyChunk ec = thermalModelElement.extractClosestEnergyChunk( burner.getFlameIceRect() );
                        if ( ec != null ) {
                            burner.addEnergyChunk( ec );
                        }
                    }
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

        // Exchange energy and energy chunks between the movable thermal
        // energy containers and the air.
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

            // Exchange energy and energy chunks with the air if appropriate
            // conditions met.
            if ( !contactWithOtherMovableElement || ( !immersedInBeaker && ( maxTemperatureDifference < MIN_TEMPERATURE_DIFF_FOR_MULTI_BODY_AIR_ENERGY_EXCHANGE || movableEnergyContainer.getEnergyBeyondMaxTemperature() > 0 ) ) ) {
                air.exchangeEnergyWith( movableEnergyContainer, dt );
                if ( movableEnergyContainer.getEnergyChunkBalance() > 0 ) {
                    Vector2D pointAbove = new Vector2D( RAND.nextDouble() * movableEnergyContainer.getRect().getWidth() + movableEnergyContainer.getRect().getMinX(),
                                                        movableEnergyContainer.getRect().getMaxY() );
                    EnergyChunk ec = movableEnergyContainer.extractClosestEnergyChunk( pointAbove );
                    if ( ec != null ) {
                        Rectangle2D ecInitialMotionConstraints = null;
                        if ( movableEnergyContainer instanceof Beaker ) {

                            // Constrain the energy chunk's motion so that it
                            // doesn't go through the edges of the beaker.
                            // There is a bit of a fudge factor in here to
                            // make sure that the sides of the energy chunk,
                            // and not just the center, stay in bounds.
                            double energyChunkWidth = 0.01;
                            ecInitialMotionConstraints = new Rectangle2D.Double( movableEnergyContainer.getRect().getX() + energyChunkWidth / 2,
                                                                                 movableEnergyContainer.getRect().getY(),
                                                                                 movableEnergyContainer.getRect().getWidth() - energyChunkWidth,
                                                                                 movableEnergyContainer.getRect().getHeight() );
                        }
                        air.addEnergyChunk( ec, ecInitialMotionConstraints );
                    }
                }
                else if ( movableEnergyContainer.getEnergyChunkBalance() < 0 && movableEnergyContainer.getTemperature() < air.getTemperature() ) {
                    movableEnergyContainer.addEnergyChunk( air.requestEnergyChunk( movableEnergyContainer.getCenterPoint() ) );
                }
            }
        }

        // Exchange energy chunks between the air and the burners.
        for ( Burner burner : Arrays.asList( leftBurner, rightBurner ) ) {
            if ( burner.getEnergyChunkCountForAir() > 0 ) {
                air.addEnergyChunk( burner.extractClosestEnergyChunk( burner.getCenterPoint() ), null );
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
     * @param modelElement     Element whose position is being validated.
     * @param proposedPosition Proposed new position for element
     * @return The original proposed position if valid, or alternative position
     *         if not.
     */
    public Point2D validatePosition( RectangularThermalMovableModelElement modelElement, Point2D proposedPosition ) {

        // Compensate for the model element's center X position.
        Vector2D translation = new Vector2D( proposedPosition ).minus( modelElement.position.get() );

        // Figure out how far the block's right edge appears to protrude to
        // the side due to perspective.
        double blockPerspectiveExtension = Block.SURFACE_WIDTH * BlockNode.PERSPECTIVE_EDGE_PROPORTION * Math.cos( BlockNode.PERSPECTIVE_ANGLE ) / 2;

        // Validate against burner boundaries.  Treat the burners as one big
        // blocking rectangle so that the user can't drag things between
        // them.  Also, compensate for perspective so that we can avoid
        // difficult z-order issues.
        double standPerspectiveExtension = leftBurner.getOutlineRect().getHeight() * BurnerNode.EDGE_TO_HEIGHT_RATIO * Math.cos( BurnerStandNode.PERSPECTIVE_ANGLE ) / 2;
        double burnerRectX = leftBurner.getOutlineRect().getX() - standPerspectiveExtension - ( modelElement != beaker ? blockPerspectiveExtension : 0 );
        Rectangle2D burnerBlockingRect = new Rectangle2D.Double( burnerRectX,
                                                                 leftBurner.getOutlineRect().getY(),
                                                                 rightBurner.getOutlineRect().getMaxX() - burnerRectX,
                                                                 leftBurner.getOutlineRect().getHeight() );
        translation = determineAllowedTranslation( modelElement.getRect(), burnerBlockingRect, translation, false );

        // Validate against the sides of the beaker.
        if ( modelElement != beaker ) {

            // Create three rectangles to represent the two sides and the top
            // of the beaker.
            double testRectThickness = 1E-3; // 1 mm thick walls.
            Rectangle2D beakerRect = beaker.getRect();
            Rectangle2D beakerLeftSide = new Rectangle2D.Double( beakerRect.getMinX() - blockPerspectiveExtension,
                                                                 beaker.getRect().getMinY(),
                                                                 testRectThickness + blockPerspectiveExtension * 2,
                                                                 beaker.getRect().getHeight() + blockPerspectiveExtension );
            Rectangle2D beakerRightSide = new Rectangle2D.Double( beaker.getRect().getMaxX() - testRectThickness - blockPerspectiveExtension,
                                                                  beaker.getRect().getMinY(),
                                                                  testRectThickness + blockPerspectiveExtension * 2,
                                                                  beaker.getRect().getHeight() + blockPerspectiveExtension );
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

            Rectangle2D testRect = modelElement.getRect();
            if ( modelElement == beaker ) {
                // Special handling for the beaker - block it at the outer
                // edge of the block instead of the center in order to
                // simplify z-order handling.
                testRect = new Rectangle2D.Double( testRect.getX() - blockPerspectiveExtension,
                                                   testRect.getY(),
                                                   testRect.getWidth() + blockPerspectiveExtension * 2,
                                                   testRect.getHeight() );
            }

            // Clamp the translation based on the test block's position, but
            // handle the case where the block is immersed in the beaker.
            if ( modelElement != beaker || !beaker.getRect().contains( block.getRect() ) ) {
                translation = determineAllowedTranslation( testRect, block.getRect(), translation, restrictPositiveY );
            }
        }

        // Determine the new position based on the resultant translation.
        MutableVector2D newPosition = new MutableVector2D( modelElement.position.get().plus( translation ) );

        // Clamp Y position to be positive to prevent dragging below table.
        newPosition.setY( Math.max( newPosition.getY(), 0 ) );

        return newPosition.toPoint2D();
    }

    public void dumpEnergies() {
        for ( ThermalEnergyContainer thermalEnergyContainer : Arrays.asList( ironBlock, brick, beaker, air ) ) {
            System.out.println( thermalEnergyContainer.getClass().getName() + " - energy = " + thermalEnergyContainer.getEnergy() );
        }
    }

    /*
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
    private Vector2D determineAllowedTranslation( Rectangle2D movingRect, Rectangle2D stationaryRect, Vector2D proposedTranslation, boolean restrictPosY ) {

        // Test for case where rectangles already overlap.
        if ( movingRect.intersects( stationaryRect ) ) {

            // The rectangles already overlap.  Are they right on top of one another?
            if ( movingRect.getCenterX() == stationaryRect.getCenterX() && movingRect.getCenterY() == stationaryRect.getCenterY() ) {
                System.out.println( getClass().getName() + " - Warning: Rectangle centers in same location, returning zero vector." );
                return new Vector2D( 0, 0 );
            }

            // Determine the motion in the X & Y directions that will "cure"
            // the overlap.
            double xOverlapCure = 0;
            if ( movingRect.getMaxX() > stationaryRect.getMinX() && movingRect.getMinX() < stationaryRect.getMinX() ) {
                xOverlapCure = stationaryRect.getMinX() - movingRect.getMaxX();
            }
            else if ( stationaryRect.getMaxX() > movingRect.getMinX() && stationaryRect.getMinX() < movingRect.getMinX() ) {
                xOverlapCure = stationaryRect.getMaxX() - movingRect.getMinX();
            }
            double yOverlapCure = 0;
            if ( movingRect.getMaxY() > stationaryRect.getMinY() && movingRect.getMinY() < stationaryRect.getMinY() ) {
                yOverlapCure = stationaryRect.getMinY() - movingRect.getMaxY();
            }
            else if ( stationaryRect.getMaxY() > movingRect.getMinY() && stationaryRect.getMinY() < movingRect.getMinY() ) {
                yOverlapCure = stationaryRect.getMaxY() - movingRect.getMinY();
            }

            // Something is wrong with algorithm if both values are zero,
            // since overlap was detected by the "intersects" method.
            assert !( xOverlapCure == 0 && yOverlapCure == 0 );

            // Return a vector with the smallest valid "cure" value, leaving
            // the other translation value unchanged.
            if ( xOverlapCure != 0 && Math.abs( xOverlapCure ) < Math.abs( yOverlapCure ) ) {
                return new Vector2D( xOverlapCure, proposedTranslation.getY() );
            }
            else {
                return new Vector2D( proposedTranslation.getX(), yOverlapCure );
            }
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

        return new Vector2D( xTranslation, yTranslation );
    }

    /* Project a line into a 2D shape based on the provided projection vector.
     * This is a convenience function used by the code that detects potential
     * collisions between the 2D objects in model space.
     */
    private Shape projectShapeFromLine( Line2D edge, Vector2D projection ) {
        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( edge.getP1() );
        path.lineTo( edge.getX1() + projection.getX(), edge.getY1() + projection.getY() );
        path.lineTo( edge.getX2() + projection.getX(), edge.getY2() + projection.getY() );
        path.lineTo( edge.getP2() );
        path.closePath();
        return path.getGeneralPath();
    }

    public ConstantDtClock getClock() {
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

    public BeakerContainer getBeaker() {
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
            if ( element.getBottomSurfaceProperty().get().overlapsWith( potentialSupportingElement.getTopSurfaceProperty().get() ) ) {

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

        // Make sure that the best supporting surface isn't at the bottom of
        // a stack, which can happen in cases where the model element being
        // tested isn't directly above the best surface's center.
        if ( bestOverlappingSurface != null ) {
            while ( bestOverlappingSurface.get().getElementOnSurface() != null ) {
                bestOverlappingSurface = bestOverlappingSurface.get().getElementOnSurface().getTopSurfaceProperty();
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

    /**
     * Get the temperature and color that would be sensed by a thermometer at
     * the provided location.
     *
     * @param location Location to be sensed.
     * @return Composite object with temperature and color at the provided
     *         location.
     */
    public TemperatureAndColor getTemperatureAndColorAtLocation( Vector2D location ) {
        Point2D locationAsPoint = location.toPoint2D();

        // Test blocks first.  This is a little complicated since the z-order
        // must be taken into account.
        List<Block> copyOfBlockList = new ArrayList<Block>( getBlockList() );
        Collections.sort( copyOfBlockList, new Comparator<Block>() {
            public int compare( Block b1, Block b2 ) {
                if ( b1.position.get().equals( b2.position.get() ) ) {
                    return 0;
                }
                if ( b2.position.get().getX() > b1.position.get().getX() || b2.position.get().getY() > b1.position.get().getY() ) {
                    return 1;
                }
                return -1;
            }
        } );
        for ( Block block : copyOfBlockList ) {
            if ( block.getProjectedShape().contains( locationAsPoint ) ) {
                return new TemperatureAndColor( block.getTemperature(), block.getColor() );
            }
        }

        // Test if this point is in the water or steam associated with the beaker.
        if ( beaker.getThermalContactArea().getBounds().contains( locationAsPoint ) ) {
            return new TemperatureAndColor( beaker.getTemperature(), EFACConstants.WATER_COLOR_IN_BEAKER );
        }
        else if ( beaker.getSteamArea().contains( locationAsPoint ) && beaker.steamingProportion > 0 ) {
            return new TemperatureAndColor( beaker.getSteamTemperature( locationAsPoint.getY() - beaker.getSteamArea().getMinY() ), Color.WHITE );
        }

        // Test if the point is a burner.
        for ( Burner burner : Arrays.asList( leftBurner, rightBurner ) ) {
            if ( burner.getFlameIceRect().contains( locationAsPoint ) ) {
                return new TemperatureAndColor( burner.getTemperature(), EFACConstants.FIRST_TAB_BACKGROUND_COLOR );

            }
        }

        // Point is in nothing else, so return the air temperature.
        return new TemperatureAndColor( air.getTemperature(), EFACConstants.FIRST_TAB_BACKGROUND_COLOR );
    }
}
