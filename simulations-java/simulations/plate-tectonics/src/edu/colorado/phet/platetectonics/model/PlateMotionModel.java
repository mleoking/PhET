// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.Bounds3F;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;
import edu.colorado.phet.common.phetcommon.model.event.Notifier;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ModelComponentTypes;
import edu.colorado.phet.common.phetcommon.simsharing.messages.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.util.FunctionalUtils;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.platetectonics.PlateTectonicsSimSharing.ModelActions;
import edu.colorado.phet.platetectonics.PlateTectonicsSimSharing.ModelComponents;
import edu.colorado.phet.platetectonics.PlateTectonicsSimSharing.ParameterKeys;
import edu.colorado.phet.platetectonics.model.behaviors.CollidingBehavior;
import edu.colorado.phet.platetectonics.model.behaviors.OverridingBehavior;
import edu.colorado.phet.platetectonics.model.behaviors.RiftingBehavior;
import edu.colorado.phet.platetectonics.model.behaviors.SubductingBehavior;
import edu.colorado.phet.platetectonics.model.behaviors.TransformBehavior;
import edu.colorado.phet.platetectonics.model.labels.BoundaryLabel;
import edu.colorado.phet.platetectonics.model.labels.RangeLabel;
import edu.colorado.phet.platetectonics.model.labels.TextLabel;
import edu.colorado.phet.platetectonics.model.regions.Boundary;
import edu.colorado.phet.platetectonics.model.regions.CrossSectionStrip;
import edu.colorado.phet.platetectonics.util.Side;

import static edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;

/**
 * Main model for the Plate Motion tab. Starts with just the mantle, then 2 plates are added. The UI and user decide what type of plate
 * behaviors (plate types and motion direction decide this) at the start of the animation, and the PlateBehavior for each plate takes care
 * of the specific type of animation.
 * <p/>
 * The state of the model (and how it needs to react to the user) is very helpful to understand. Here's a simplified (linear) state machine view of
 * the model (note that the user can switch between automatic and manual mode at any time):
 * <p/>
 * A) No crusts dropped (i.e. like the start of the simulation)
 * |    * user drops two crusts into place -->
 * B) Crusts (and plates) initialized, but no motion type or behaviors set or chosen. model.hasBothPlates flag is true
 * |    * user can change model.motionTypeIfStarted in automatic mode with the motion type radio buttons (with green / red / blue icons),
 * |      but it isn't set in stone
 * |    * user either drags the handles in a specific direction (manual mode) or presses play for 1st time (automatic mode) -->
 * C) Motion type fixed (won't change), plate-specific behaviors are created depending on crust types. model.animationStarted flag is true
 * |        (model.initializeBehaviors() is called after model.motionType is set)
 * |    * user can animate as they want (automatic or manual mode)
 * |    * user presses "Rewind" --> rewinds animation, goes to (C) with same motion type / behaviors
 * |    * user presses "Reset" --> goes to (A)
 */
public class PlateMotionModel extends PlateTectonicsModel {

    // the two main plates
    private PlateMotionPlate leftPlate;
    private PlateMotionPlate rightPlate;

    private final TectonicsClock clock;

    // labels that need to be shown
    public final ObservableList<RangeLabel> rangeLabels = new ObservableList<RangeLabel>();
    public final ObservableList<BoundaryLabel> boundaryLabels = new ObservableList<BoundaryLabel>();
    public final ObservableList<TextLabel> textLabels = new ObservableList<TextLabel>();
    public final Notifier<Side> frontBoundarySideNotifier = new Notifier<Side>();

    public static enum MotionType {
        CONVERGENT,
        DIVERGENT,
        TRANSFORM
    }

    // the strip of terrain connecting the left and right terran (perfectly vertical at the start), it's basically just glue
    private TerrainConnectorStrip terrainConnector;

    // model constants
    public static final float SIMPLE_MANTLE_TOP_Y = -10000;
    public static final float SIMPLE_MANTLE_BOTTOM_Y = -600000;
    public static final float SIMPLE_MANTLE_TOP_TEMP = ZERO_CELSIUS + 700;
    public static final float SIMPLE_LITHOSPHERE_BOUNDARY_TEMP = ZERO_CELSIUS + 1100;
    public static final float SIMPLE_MANTLE_BOTTOM_TEMP = ZERO_CELSIUS + 1300;
    public static final float SIMPLE_MANTLE_DENSITY = 3300f;

    public static final float SIMPLE_CRUST_TOP_TEMP = ZERO_CELSIUS;
    public static final float SIMPLE_CRUST_BOTTOM_TEMP = ZERO_CELSIUS + 450;

    public static final float SIMPLE_MAGMA_TEMP = ZERO_CELSIUS + 1300; // TODO: should this be warmer the farther down we are?
    public static final float SIMPLE_MAGMA_DENSITY = 2000f;

    // model "resolution" constants
    public static final int MANTLE_VERTICAL_STRIPS = 6;
    public static final int CRUST_VERTICAL_STRIPS = 2;
    public static final int LITHOSPHERE_VERTICAL_STRIPS = 2;
    public static final int HORIZONTAL_SAMPLES = 96;

    public static final int TERRAIN_DEPTH_SAMPLES = 32;

    private boolean transformMotionCCW = true;

    // boundary label (dotted line) that joines the left and right side
    public BoundaryLabel joiningBoundaryLabel;

    // tracks the stacking order of cross-section strips
    private final StripTracker stripTracker = new StripTracker();

    public ObservableList<SmokePuff> smokePuffs = new ObservableList<SmokePuff>();

    // plate and motion types (subject to change)
    public final Property<PlateType> leftPlateType = new Property<PlateType>( null );
    public final Property<PlateType> rightPlateType = new Property<PlateType>( null );
    public final Property<MotionType> motionType = new Property<MotionType>( null );
    public final Property<MotionType> motionTypeIfStarted = new Property<MotionType>( MotionType.CONVERGENT );

    // whether animation has started (if so, our behaviors are initialized)
    public final Property<Boolean> animationStarted = new Property<Boolean>( false );

    // whether both plates have been chosen from the "crust chooser". this does NOT mean a motion direction / behavior set has been chosen
    public final Property<Boolean> hasBothPlates = new Property<Boolean>( false ) {{
        SimpleObserver observer = new SimpleObserver() {
            public void update() {
                set( hasLeftPlate() && hasRightPlate() );
            }
        };
        leftPlateType.addObserver( observer );
        rightPlateType.addObserver( observer );
    }};

    // TODO: change bounds to possibly a Z range, or just bake it in
    public PlateMotionModel( final TectonicsClock clock, final Bounds3F bounds ) {
        super( bounds, new TextureStrategy( 0.000006f ) );
        this.clock = clock;

        resetPlates();
        resetTerrain();

        updateTerrain();
        updateStrips();

        // once plates have been chosen and animation has started, set up our plate behaviors so we can animate correctly
        animationStarted.addObserver( new SimpleObserver() {
            public void update() {
                if ( animationStarted.get() ) {
                    initializeBehaviors();
                }
            }
        } );

        // after choosing both plates, we need to determine the default motion type if the user presses play or step in automatic mode
        hasBothPlates.addObserver( new SimpleObserver() {
            public void update() {
                if ( allowsConvergentMotion() ) {
                    motionTypeIfStarted.set( MotionType.CONVERGENT );
                }
                else if ( allowsDivergentMotion() ) {
                    motionTypeIfStarted.set( MotionType.DIVERGENT );
                }
                else {
                    motionTypeIfStarted.set( MotionType.TRANSFORM );
                }

                if ( hasBothPlates.get() ) {
                    addMantleLabel();
                    addJoinedBoundary();
                }
            }
        } );
    }

    // adds a mantle label that stays at an appropriate height (in the middle)
    private void addMantleLabel() {
        if ( hasBothPlates.get() ) {
            final float mantleLabelHeight = -180000;
            textLabels.add( new TextLabel( new Property<Vector3F>( new Vector3F( 0, mantleLabelHeight, 0 ) ) {{

                // if we have a colliding behavior, we must push the mantle label down as the lithosphere sinks
                if ( leftPlate.getBehavior() != null && leftPlate.getBehavior() instanceof CollidingBehavior ) {
                    final Sample trackedSample = leftPlate.getLithosphere().getBottomBoundary().getEdgeSample( Side.RIGHT );
                    float initialContinentalBottom = trackedSample.getPosition().y;
                    final float depthFromBottom = mantleLabelHeight - initialContinentalBottom;
                    modelChanged.addUpdateListener( new UpdateListener() {
                        public void update() {
                            set( new Vector3F( 0, depthFromBottom + trackedSample.getPosition().y, 0 ) );
                        }
                    }, false );
                }
            }}, Strings.MANTLE ) );
        }
    }

    // given a particular choice of motion types (transform/convergent/divergent) and plate types, this initializes the necessary plate behaviors
    private void initializeBehaviors() {

        ParameterSet parameters = new ParameterSet( new Parameter[]{
                new Parameter( ParameterKeys.leftPlateType, leftPlateType.get().toString() ),
                new Parameter( ParameterKeys.rightPlateType, rightPlateType.get().toString() )
        } );

        switch( motionType.get() ) {
            case TRANSFORM:
                // limit to 25 million years
                clock.setTimeLimit( 25 );
                leftPlate.setBehavior( new TransformBehavior( leftPlate, rightPlate, isTransformMotionCCW() ) );
                rightPlate.setBehavior( new TransformBehavior( rightPlate, leftPlate, !isTransformMotionCCW() ) );
                leftPlate.addMiddleSide( rightPlate );
                rightPlate.addMiddleSide( leftPlate );
                SimSharingManager.sendModelMessage( ModelComponents.motion, ModelComponentTypes.feature, ModelActions.transformMotion, parameters );
                break;
            case CONVERGENT:
                // if both continental, we collide
                if ( leftPlateType.get() == PlateType.CONTINENTAL && rightPlateType.get() == PlateType.CONTINENTAL ) {
                    // limit to 35 million years
                    clock.setTimeLimit( 35 );
                    leftPlate.setBehavior( new CollidingBehavior( leftPlate, rightPlate ) );
                    rightPlate.setBehavior( new CollidingBehavior( rightPlate, leftPlate ) );
                    SimSharingManager.sendModelMessage( ModelComponents.motion, ModelComponentTypes.feature, ModelActions.continentalCollisionMotion, parameters );
                }
                // otherwise test for the heavier plate, which will subduct
                else if ( leftPlateType.get().isContinental() || rightPlateType.get() == PlateType.OLD_OCEANIC ) {
                    clock.setTimeLimit( 50 );
                    // right plate subducts
                    leftPlate.setBehavior( new OverridingBehavior( leftPlate, rightPlate ) );
                    rightPlate.setBehavior( new SubductingBehavior( rightPlate, leftPlate ) );
                    rightPlate.getCrust().moveToFront();
                    frontBoundarySideNotifier.updateListeners( Side.RIGHT );
                    SimSharingManager.sendModelMessage( ModelComponents.motion, ModelComponentTypes.feature, ModelActions.rightPlateSubductingMotion, parameters );
                }
                else if ( rightPlateType.get().isContinental() || leftPlateType.get() == PlateType.OLD_OCEANIC ) {
                    clock.setTimeLimit( 50 );
                    // left plate subducts
                    leftPlate.setBehavior( new SubductingBehavior( leftPlate, rightPlate ) );
                    rightPlate.setBehavior( new OverridingBehavior( rightPlate, leftPlate ) );
                    leftPlate.getCrust().moveToFront();
                    frontBoundarySideNotifier.updateListeners( Side.LEFT );
                    // TODO: refactor so we don't have to send out side notifications after these modifications
                    SimSharingManager.sendModelMessage( ModelComponents.motion, ModelComponentTypes.feature, ModelActions.leftPlateSubductingMotion, parameters );
                }
                else {
                    // plates must be the same
                    assert leftPlateType.get() == rightPlateType.get();

                    // which isn't allowed here for any oceanic combinations
                    throw new RuntimeException( "behavior type not supported: " + leftPlateType.get() + ", " + rightPlateType.get() );
                }
                break;
            case DIVERGENT:
                clock.setTimeLimit( 35 );
                leftPlate.setBehavior( new RiftingBehavior( leftPlate, rightPlate ) );
                rightPlate.setBehavior( new RiftingBehavior( rightPlate, leftPlate ) );
                SimSharingManager.sendModelMessage( ModelComponents.motion, ModelComponentTypes.feature, ModelActions.divergentMotion, parameters );
                break;
        }

        leftPlate.getBehavior().afterConstructionInit();
        rightPlate.getBehavior().afterConstructionInit();

        // reset the labels
        if ( motionType.get() == MotionType.TRANSFORM ) {
            // no labels for the transform motion type
            textLabels.clear();
        }
        else if ( textLabels.isEmpty() && hasBothPlates.get() ) {
            addMantleLabel();
        }
        else {
            textLabels.clear();
            addMantleLabel();
        }

    }

    private void addJoinedBoundary() {
        // add a joining boundary between the two plates (behaviors may change or destroy this)
        joiningBoundaryLabel = new BoundaryLabel( new Boundary() {{
            addSample( Side.LEFT, getPlate( Side.LEFT ).getLithosphere().getBottomBoundary().getEdgeSample( Side.RIGHT ) );
            addSample( Side.RIGHT, getPlate( Side.RIGHT ).getLithosphere().getBottomBoundary().getEdgeSample( Side.LEFT ) );
        }}, Side.LEFT ); // side of the boundary is arbitrary
        boundaryLabels.add( joiningBoundaryLabel );
    }

    private void resetPlates() {
        if ( leftPlate != null ) {
            removePlate( leftPlate );
        }

        leftPlate = new PlateMotionPlate( this, getTextureStrategy(), Side.LEFT );
        if ( rightPlate != null ) {
            removePlate( rightPlate );
        }
        rightPlate = new PlateMotionPlate( this, getTextureStrategy(), Side.RIGHT );

        addPlate( leftPlate );
        addPlate( rightPlate );
    }

    public void resetTerrain() {
        final float minZ = bounds.getMinZ();
        final float maxZ = bounds.getMaxZ();

        if ( terrainConnector != null ) {
            removeTerrain( terrainConnector );
        }
        terrainConnector = new TerrainConnectorStrip( leftPlate.getTerrain(), rightPlate.getTerrain(), 3, minZ, maxZ );
        addTerrain( terrainConnector );
    }


    public void rewind() {
        joiningBoundaryLabel = null;

        resetPlates();
        resetTerrain();

        rangeLabels.clear();
        boundaryLabels.clear();
        textLabels.clear();

        dropCrust( Side.LEFT, leftPlateType.get() );
        dropCrust( Side.RIGHT, rightPlateType.get() );

        // joined boundary before behaviors, since the overriding behavior needs to modify the joined boundary
        addJoinedBoundary();
        initializeBehaviors();

        smokePuffs.clear();
    }

    @Override
    public void resetAll() {
        super.resetAll();

        joiningBoundaryLabel = null;

        clock.resetTimeLimit();
        clock.setTimeMultiplier( 1 ); // TODO: refactor so this is easier to reset (maybe property-based?)

        leftPlateType.reset();
        rightPlateType.reset();
        resetPlates();
        resetTerrain();

        motionType.reset();
        motionTypeIfStarted.reset();
        animationStarted.reset();

        updateStrips();
        updateTerrain();

        modelChanged.updateListeners();

        smokePuffs.clear();

        rangeLabels.clear();
        boundaryLabels.clear();
        textLabels.clear();
    }

    // xIndex can be from 0 to HORIZONTAL_SAMPLES-1
    public float getStartingX( Side side, int xIndex ) {
        assert xIndex >= 0;
        assert xIndex < HORIZONTAL_SAMPLES;
        switch( side ) {
            case LEFT:
                return bounds.getMinX() + ( bounds.getCenterX() - bounds.getMinX() ) * ( (float) xIndex ) / (float) ( HORIZONTAL_SAMPLES - 1 );
            case RIGHT:
                return bounds.getCenterX() + ( bounds.getMaxX() - bounds.getCenterX() ) * ( (float) xIndex ) / (float) ( HORIZONTAL_SAMPLES - 1 );
            default:
                throw new RuntimeException( "Side not found: " + side );
        }
    }

    public void dropCrust( Side side, PlateType type ) {
        PlateMotionPlate plate = getPlate( side );

        plate.droppedCrust( type );
        getPlateTypeProperty( side ).set( type );

        updateStrips();
        plate.fullSyncTerrain();
        plate.randomizeTerrain();
        updateTerrain();
        updateStrips();
        modelChanged.updateListeners();
    }

    private void updateStrips() {
        for ( CrossSectionStrip strip : getCrossSectionStrips() ) {
            strip.update();
        }
    }

    private void updateTerrain() {
        terrainConnector.update();
    }

    public Property<PlateType> getPlateTypeProperty( Side side ) {
        return side == Side.LEFT ? leftPlateType : rightPlateType;
    }

    public PlateType getPlateType( Side side ) {
        return getPlateTypeProperty( side ).get();
    }

    public PlateMotionPlate getPlate( Side side ) {
        return side == Side.LEFT ? leftPlate : rightPlate;
    }

    public boolean hasPlate( Side side ) {
        return getPlateType( side ) != null;
    }

    public boolean hasLeftPlate() {
        return hasPlate( Side.LEFT );
    }

    public boolean hasRightPlate() {
        return hasPlate( Side.RIGHT );
    }

    public List<Sample> getAllSamples() {
        return FunctionalUtils.concat( leftPlate.getSamples(), rightPlate.getSamples() );
    }

    // TODO: conversion from double to float
    @Override
    public void update( double timeElapsed ) {
        assert !Double.isNaN( timeElapsed );
        super.update( timeElapsed );

        if ( hasLeftPlate() && hasRightPlate() ) {
            if ( motionType.get() == null ) {
                // default to a certain direction when we pick a motion-type this way
                setTransformMotionCCW( false );
                motionType.set( motionTypeIfStarted.get() );
            }

            animationStarted.set( true );
            leftPlate.getBehavior().stepInTime( (float) timeElapsed );
            rightPlate.getBehavior().stepInTime( (float) timeElapsed );
            updateStrips();
            updateTerrain();
        }

        modelChanged.updateListeners();
    }

    @Override
    public double getElevation( double x, double z ) {
        // NOTE: OK to not fill in here, not ever used. TODO: redesign so we don't have this
        return 0;
    }

    @Override
    public double getDensity( double x, double y ) {
        Vector3F point = new Vector3F( (float) x, (float) y, 0 );
        HitResult hitResult = firstStripIntersection( point );
        if ( hitResult != null ) {
            return hitResult.density;
        }
        else if ( y < 0 ) {
            return PlateTectonicsModel.getWaterDensity( y );
        }
        else {
            return PlateTectonicsModel.getAirDensity( y );
        }
    }

    @Override
    public double getTemperature( double x, double y ) {
        Vector3F point = new Vector3F( (float) x, (float) y, 0 );
        HitResult hitResult = firstStripIntersection( point );
        if ( hitResult != null ) {
            return hitResult.temperature;
        }
        else if ( y < 0 ) {
            return PlateTectonicsModel.getWaterTemperature( y );
        }
        else {
            return PlateTectonicsModel.getAirTemperature( y );
        }
    }

    public static double getSimplifiedMantleTemperature( double y ) {
        double depth = -y;
        return 273.15 + ( 0.0175 - 3.04425e-9 * depth ) * depth; // based on T0 + (q00/k)*y - (rho*H/(2*k))*y^2 from model doc
    }

    public Bounds3F getLeftDropAreaBounds() {
        return Bounds3F.fromMinMax( bounds.getMinX(), bounds.getCenterX(),
                                    SIMPLE_MANTLE_TOP_Y, 15000,
                                    bounds.getMinZ(), bounds.getMaxZ() );
    }

    public Bounds3F getRightDropAreaBounds() {
        return Bounds3F.fromMinMax( bounds.getCenterX(), bounds.getMaxX(),
                                    SIMPLE_MANTLE_TOP_Y, 15000,
                                    bounds.getMinZ(), bounds.getMaxZ() );
    }

    public boolean allowsDivergentMotion() {
        // disallow oceanic-continental for divergent motion
        return hasLeftPlate() && hasRightPlate() && ( leftPlateType.get().isContinental() == rightPlateType.get().isContinental() );
    }

    public boolean allowsTransformMotion() {
        // disallow oceanic-continental for transform motion
        return hasLeftPlate() && hasRightPlate() && ( leftPlateType.get().isContinental() == rightPlateType.get().isContinental() );
    }

    public boolean allowsConvergentMotion() {
        // allow convergent motion unless both are the same type of oceanic plate
        return hasLeftPlate() && hasRightPlate() && ( leftPlateType.get() != rightPlateType.get()
                                                      || ( leftPlateType.get().isContinental() && rightPlateType.get().isContinental() ) );
    }

    public boolean isTransformMotionCCW() {
        return transformMotionCCW;
    }

    public void setTransformMotionCCW( boolean transformMotionCCW ) {
        this.transformMotionCCW = transformMotionCCW;
    }

    @Override public List<CrossSectionStrip> getStripsInOrder() {
        return stripTracker.getStripsInOrder();
    }

    // keeps track of the stacking order of cross-section strips so we can accurately get intersection information even with overlapping strips
    private class StripTracker {
        private final List<CrossSectionStrip> strips = new ArrayList<CrossSectionStrip>();

        private StripTracker() {
            final VoidFunction1<CrossSectionStrip> moveToFrontCallback = new VoidFunction1<CrossSectionStrip>() {
                public void apply( CrossSectionStrip strip ) {
                    strips.remove( strip );
                    strips.add( strip );
                }
            };

            crossSectionStripAdded.addListener( new VoidFunction1<CrossSectionStrip>() {
                public void apply( CrossSectionStrip strip ) {
                    strip.moveToFrontNotifier.addListener( moveToFrontCallback );
                    strips.add( strip );
                }
            } );

            crossSectionStripRemoved.addListener( new VoidFunction1<CrossSectionStrip>() {
                public void apply( CrossSectionStrip strip ) {
                    strip.moveToFrontNotifier.removeListener( moveToFrontCallback );
                    strips.remove( strip );
                }
            } );
        }

        // returns the front-most strips first (reversed from the actual display rendering order)
        public List<CrossSectionStrip> getStripsInOrder() {
            List<CrossSectionStrip> result = new ArrayList<CrossSectionStrip>( strips );
            Collections.reverse( result );
            return result;
        }
    }
}
