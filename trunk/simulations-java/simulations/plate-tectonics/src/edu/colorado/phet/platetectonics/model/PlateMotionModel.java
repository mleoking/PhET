// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
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
import edu.colorado.phet.platetectonics.model.regions.CrossSectionStrip;
import edu.colorado.phet.platetectonics.util.Bounds3D;
import edu.colorado.phet.platetectonics.util.Side;

import static edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;

public class PlateMotionModel extends PlateModel {

    private PlateMotionPlate leftPlate;
    private PlateMotionPlate rightPlate;
    private final TectonicsClock clock;

    public final ObservableList<RangeLabel> rangeLabels = new ObservableList<RangeLabel>();
    public final ObservableList<BoundaryLabel> boundaryLabels = new ObservableList<BoundaryLabel>();
    public final ObservableList<TextLabel> textLabels = new ObservableList<TextLabel>();

    public static enum MotionType {
        CONVERGENT,
        DIVERGENT,
        TRANSFORM
    }

    private TerrainConnectorStrip terrainConnector;

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

    public static final int MANTLE_VERTICAL_STRIPS = 6;
    public static final int CRUST_VERTICAL_STRIPS = 2;
    public static final int LITHOSPHERE_VERTICAL_STRIPS = 2;
    public static final int HORIZONTAL_SAMPLES = 96;

    public static final int TERRAIN_DEPTH_SAMPLES = 32;

    private boolean transformMotionCCW = true;

    private final StripTracker stripTracker = new StripTracker();

    public ObservableList<SmokePuff> smokePuffs = new ObservableList<SmokePuff>();

    // TODO: better handling for this. ugly
    public final Property<PlateType> leftPlateType = new Property<PlateType>( null );
    public final Property<PlateType> rightPlateType = new Property<PlateType>( null );
    public final Property<MotionType> motionType = new Property<MotionType>( null );
    public final Property<MotionType> motionTypeIfStarted = new Property<MotionType>( MotionType.CONVERGENT );

    public final Property<Boolean> animationStarted = new Property<Boolean>( false );

    public final Property<Boolean> hasBothPlates = new Property<Boolean>( false ) {{
        SimpleObserver observer = new SimpleObserver() {
            public void update() {
                set( hasLeftPlate() && hasRightPlate() );
            }
        };
        leftPlateType.addObserver( observer );
        rightPlateType.addObserver( observer );
    }};

    public final Property<Boolean> canRun = new Property<Boolean>( false ) {{
        SimpleObserver observer = new SimpleObserver() {
            public void update() {
                set( hasLeftPlate() && hasRightPlate() && motionType.get() != null );
            }
        };
        leftPlateType.addObserver( observer );
        rightPlateType.addObserver( observer );
        motionType.addObserver( observer );
    }};

    // TODO: change bounds to possibly a Z range, or just bake it in
    public PlateMotionModel( final TectonicsClock clock, final Bounds3D bounds ) {
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

                addMantleLabel();
            }
        } );
    }

    private void addMantleLabel() {
        if ( hasBothPlates.get() ) {
            final float mantleLabelHeight = -180000;
            textLabels.add( new TextLabel( new Property<ImmutableVector3F>( new ImmutableVector3F( 0, mantleLabelHeight, 0 ) ) {{

                // if we have a colliding behavior, we must push the mantle label down as the lithosphere sinks
                if ( leftPlate.getBehavior() != null && leftPlate.getBehavior() instanceof CollidingBehavior ) {
                    final Sample trackedSample = leftPlate.getLithosphere().getBottomBoundary().getEdgeSample( Side.RIGHT );
                    float initialContinentalBottom = trackedSample.getPosition().y;
                    final float depthFromBottom = mantleLabelHeight - initialContinentalBottom;
                    modelChanged.addUpdateListener( new UpdateListener() {
                        public void update() {
                            set( new ImmutableVector3F( 0, depthFromBottom + trackedSample.getPosition().y, 0 ) );
                        }
                    }, false );
                }
            }}, Strings.MANTLE ) );
        }
    }

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
                    SimSharingManager.sendModelMessage( ModelComponents.motion, ModelComponentTypes.feature, ModelActions.rightPlateSubductingMotion, parameters );
                }
                else if ( rightPlateType.get().isContinental() || leftPlateType.get() == PlateType.OLD_OCEANIC ) {
                    clock.setTimeLimit( 50 );
                    // left plate subducts
                    leftPlate.setBehavior( new SubductingBehavior( leftPlate, rightPlate ) );
                    rightPlate.setBehavior( new OverridingBehavior( rightPlate, leftPlate ) );
                    leftPlate.getCrust().moveToFront();
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
        resetPlates();
        resetTerrain();

        rangeLabels.clear();
        boundaryLabels.clear();
        textLabels.clear();

        dropCrust( Side.LEFT, leftPlateType.get() );
        dropCrust( Side.RIGHT, rightPlateType.get() );

        initializeBehaviors();

        smokePuffs.clear();
    }

    @Override
    public void resetAll() {
        super.resetAll();

        clock.resetTimeLimit();
        clock.setTimeMultiplier( 1 ); // TODO: refactor so this is easier to reset (maybe property-based?)

        leftPlateType.reset();
        rightPlateType.reset();
        resetPlates();
        resetTerrain();

        canRun.reset();
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
        // behaviors will take care of this terrain update on their own once the animation has started
//        if ( !animationStarted.get() ) {
//            leftPlate.fullSyncTerrain();
//            rightPlate.fullSyncTerrain();
//        }
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
        ImmutableVector3F point = new ImmutableVector3F( (float) x, (float) y, 0 );
        HitResult hitResult = firstStripIntersection( point );
        if ( hitResult != null ) {
            return hitResult.density;
        }
        else if ( y < 0 ) {
            return PlateModel.getWaterDensity( y );
        }
        else {
            return PlateModel.getAirDensity( y );
        }
    }

    @Override
    public double getTemperature( double x, double y ) {
        ImmutableVector3F point = new ImmutableVector3F( (float) x, (float) y, 0 );
        HitResult hitResult = firstStripIntersection( point );
        if ( hitResult != null ) {
            return hitResult.temperature;
        }
        else if ( y < 0 ) {
            return PlateModel.getWaterTemperature( y );
        }
        else {
            return PlateModel.getAirTemperature( y );
        }
    }

    public static double getSimplifiedMantleTemperature( double y ) {
        double depth = -y;
        return 273.15 + ( 0.0175 - 3.04425e-9 * depth ) * depth; // based on T0 + (q00/k)*y - (rho*H/(2*k))*y^2 from model doc
    }

    public Bounds3D getLeftDropAreaBounds() {
        return Bounds3D.fromMinMax( bounds.getMinX(), bounds.getCenterX(),
                                    SIMPLE_MANTLE_TOP_Y, 15000,
                                    bounds.getMinZ(), bounds.getMaxZ() );
    }

    public Bounds3D getRightDropAreaBounds() {
        return Bounds3D.fromMinMax( bounds.getCenterX(), bounds.getMaxX(),
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

    public List<CrossSectionStrip> getStripsInOrder() {
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

    private static class HitResult {
        public final float density;
        public final float temperature;

        private HitResult( float density, float temperature ) {
            this.density = density;
            this.temperature = temperature;
        }
    }

    private HitResult firstStripIntersection( ImmutableVector3F point ) {
        final List<CrossSectionStrip> strips = getStripsInOrder();
        for ( CrossSectionStrip strip : strips ) {
            for ( int i = 0; i < strip.getLength() - 1; i++ ) {
                HitResult hitTopLeft = triangleXYIntersection(
                        strip.topPoints.get( i ),
                        strip.bottomPoints.get( i ),
                        strip.topPoints.get( i + 1 ),
                        point
                );
                if ( hitTopLeft != null ) {
                    return hitTopLeft;
                }
                HitResult hitBottomRight = triangleXYIntersection(
                        strip.bottomPoints.get( i ),
                        strip.topPoints.get( i + 1 ),
                        strip.bottomPoints.get( i + 1 ),
                        point
                );
                if ( hitBottomRight != null ) {
                    return hitBottomRight;
                }
            }
        }
        return null;
    }

    // not the most numerically accurate way, but that doesn't matter in this scenario
    private static HitResult triangleXYIntersection( Sample a, Sample b, Sample c, ImmutableVector3F point ) {
        float areaA = triangleXYArea( point, b.getPosition(), c.getPosition() );
        float areaB = triangleXYArea( point, c.getPosition(), a.getPosition() );
        float areaC = triangleXYArea( point, a.getPosition(), b.getPosition() );
        float insideArea = triangleXYArea( a.getPosition(), b.getPosition(), c.getPosition() );

        // some area must be "outside" the main triangle (just needs to be close)
        if ( areaA + areaB + areaC > insideArea * 1.02 ) {
            return null;
        }
        else {
            // results based on relative triangle areas
            return new HitResult(
                    ( areaA / insideArea ) * a.getDensity() + ( areaB / insideArea ) * b.getDensity() + ( areaC / insideArea ) * c.getDensity(),
                    ( areaA / insideArea ) * a.getTemperature() + ( areaB / insideArea ) * b.getTemperature() + ( areaC / insideArea ) * c.getTemperature()
            );
        }
    }

    private static float triangleXYArea( ImmutableVector3F a, ImmutableVector3F b, ImmutableVector3F c ) {
        return Math.abs( ( ( a.x - c.x ) * ( b.y - c.y ) - ( b.x - c.x ) * ( a.y - c.y ) ) / 2.0f );
    }

}
