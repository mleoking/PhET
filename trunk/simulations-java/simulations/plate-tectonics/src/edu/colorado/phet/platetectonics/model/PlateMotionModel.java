// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import java.util.List;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.FunctionalUtils;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.platetectonics.model.behaviors.CollidingBehavior;
import edu.colorado.phet.platetectonics.model.behaviors.OverridingBehavior;
import edu.colorado.phet.platetectonics.model.behaviors.RiftingBehavior;
import edu.colorado.phet.platetectonics.model.behaviors.SubductingBehavior;
import edu.colorado.phet.platetectonics.model.behaviors.TransformBehavior;
import edu.colorado.phet.platetectonics.model.regions.CrossSectionStrip;
import edu.colorado.phet.platetectonics.util.Bounds3D;
import edu.colorado.phet.platetectonics.util.Side;

public class PlateMotionModel extends PlateModel {

    private PlateMotionPlate leftPlate;
    private PlateMotionPlate rightPlate;
    private final TectonicsClock clock;

    public static enum PlateType {
        CONTINENTAL( true, false ),
        YOUNG_OCEANIC( false, true ),
        OLD_OCEANIC( false, true );
        private boolean continental;
        private boolean oceanic;

        PlateType( boolean isContinental, boolean isOceanic ) {
            continental = isContinental;
            oceanic = isOceanic;
        }

        public boolean isContinental() {
            return continental;
        }

        public boolean isOceanic() {
            return oceanic;
        }
    }

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

    public static final float SIMPLE_MAGMA_TEMP = ZERO_CELSIUS + 1000; // TODO: should this be warmer the farther down we are?
    public static final float SIMPLE_MAGMA_DENSITY = 2000f;

    public static final int MANTLE_VERTICAL_STRIPS = 6;
    public static final int CRUST_VERTICAL_STRIPS = 2;
    public static final int LITHOSPHERE_VERTICAL_STRIPS = 2;
    public static final int HORIZONTAL_SAMPLES = 96;

    public static final int TERRAIN_DEPTH_SAMPLES = 32;

    private boolean transformMotionCCW = true;

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
        super( bounds, new TextureStrategy( 0.000005f ) );
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
    }

    private void initializeBehaviors() {
        switch( motionType.get() ) {
            case TRANSFORM:
                // limit to 25 million years
                clock.setTimeLimit( 25 );
                leftPlate.setBehavior( new TransformBehavior( leftPlate, rightPlate, isTransformMotionCCW() ) );
                rightPlate.setBehavior( new TransformBehavior( rightPlate, leftPlate, !isTransformMotionCCW() ) );
                leftPlate.addMiddleSide( rightPlate );
                rightPlate.addMiddleSide( leftPlate );
                break;
            case CONVERGENT:
                // if both continental, we collide
                if ( leftPlateType.get() == PlateType.CONTINENTAL && rightPlateType.get() == PlateType.CONTINENTAL ) {
                    // limit to 35 million years
                    clock.setTimeLimit( 35 );
                    leftPlate.setBehavior( new CollidingBehavior( leftPlate, rightPlate ) );
                    rightPlate.setBehavior( new CollidingBehavior( rightPlate, leftPlate ) );
                }
                // otherwise test for the heavier plate, which will subduct
                else if ( leftPlateType.get().isContinental() || rightPlateType.get() == PlateType.OLD_OCEANIC ) {
                    // right plate subducts
                    leftPlate.setBehavior( new OverridingBehavior( leftPlate, rightPlate ) );
                    rightPlate.setBehavior( new SubductingBehavior( rightPlate, leftPlate ) );
                    rightPlate.getCrust().moveToFront();
                }
                else if ( rightPlateType.get().isContinental() || leftPlateType.get() == PlateType.OLD_OCEANIC ) {
                    // left plate subducts
                    leftPlate.setBehavior( new SubductingBehavior( leftPlate, rightPlate ) );
                    rightPlate.setBehavior( new OverridingBehavior( rightPlate, leftPlate ) );
                    leftPlate.getCrust().moveToFront();
                }
                else {
                    // plates must be the same
                    assert leftPlateType.get() == rightPlateType.get();

                    // which isn't allowed here for any oceanic combinations
                    throw new RuntimeException( "behavior type not supported: " + leftPlateType.get() + ", " + rightPlateType.get() );
                }
                break;
            case DIVERGENT:
                leftPlate.setBehavior( new RiftingBehavior( leftPlate, rightPlate ) );
                rightPlate.setBehavior( new RiftingBehavior( rightPlate, leftPlate ) );
                break;
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

    private boolean rewinding = false;

    public void rewind() {
        rewinding = true;
        resetPlates();
        resetTerrain();

        dropLeftCrust( leftPlateType.get() );
        dropRightCrust( rightPlateType.get() );
        rewinding = false;

        updateStrips();

        // needed since our animation is started
        leftPlate.fullSyncTerrain();
        rightPlate.fullSyncTerrain();
        updateTerrain();

        modelChanged.updateListeners();

        initializeBehaviors();
    }

    @Override public void resetAll() {
        super.resetAll();

        clock.resetTimeLimit();

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

    public void dropLeftCrust( final PlateType type ) {
        leftPlate.droppedCrust( type );
        leftPlateType.set( type );

        if ( !rewinding ) {
            updateStrips();
            updateTerrain();
            modelChanged.updateListeners();
        }
    }

    public void dropRightCrust( final PlateType type ) {
        rightPlate.droppedCrust( type );
        rightPlateType.set( type );

        if ( !rewinding ) {
            updateStrips();
            updateTerrain();
            modelChanged.updateListeners();
        }
    }

    private void updateStrips() {
        for ( CrossSectionStrip strip : getCrossSectionStrips() ) {
            strip.update();
        }
    }

    private void updateTerrain() {
        // behaviors will take care of this terrain update on their own once the animation has started
        if ( !animationStarted.get() ) {
            leftPlate.fullSyncTerrain();
            rightPlate.fullSyncTerrain();
        }
        terrainConnector.update();
    }

    public static float getFreshDensity( PlateType type ) {
        switch( type ) {
            case CONTINENTAL:
                return 2750;
            case YOUNG_OCEANIC:
                return 3000;
            case OLD_OCEANIC:
                return 3070;
            default:
                throw new RuntimeException( "unknown type: " + type );
        }
    }

    public static float getFreshCrustBottom( PlateType type ) {
        switch( type ) {
            case CONTINENTAL:
                return -40000;
            case YOUNG_OCEANIC:
                return -10000;
            case OLD_OCEANIC:
                return -10000;
            default:
                throw new RuntimeException( "unknown type: " + type );
        }
    }

    public static float getFreshCrustTop( PlateType type ) {
        switch( type ) {
            case CONTINENTAL:
                return 5000;
            case YOUNG_OCEANIC:
                return -3000;
            case OLD_OCEANIC:
                return -3000;
            default:
                throw new RuntimeException( "unknown type: " + type );
        }
    }

    public static float getFreshLithosphereBottom( PlateType type ) {
        switch( type ) {
            case CONTINENTAL:
                return getFreshCrustBottom( type ) - 100000;
            case YOUNG_OCEANIC:
                return getFreshCrustBottom( type ) - 30000;
            case OLD_OCEANIC:
                return getFreshCrustBottom( type ) - 40000; // old oceanic lithosphere is thicker
            default:
                throw new RuntimeException( "unknown type: " + type );
        }
    }

    public boolean hasLeftPlate() {
        return leftPlateType.get() != null;
    }

    public boolean hasRightPlate() {
        return rightPlateType.get() != null;
    }

    public List<Sample> getAllSamples() {
        return FunctionalUtils.concat( leftPlate.getSamples(), rightPlate.getSamples() );
    }

    // TODO: conversion from double to float
    @Override public void update( double timeElapsed ) {
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

    public static void transformSample( Sample sample, float timeElapsed ) {
        ImmutableVector2F origin = new ImmutableVector2F( 0, 5005 );
        ImmutableVector2F toDir = ImmutableVector2F.Y_UNIT.negate();
        ImmutableVector2F fromDir = ImmutableVector2F.X_UNIT;

        ImmutableVector2F pos = new ImmutableVector2F( sample.getPosition().x, sample.getPosition().y ).minus( origin );

        // flip the "from" direction if we are on the other side of the "to" direction
        if ( fromDir.dot( toDir ) > fromDir.dot( pos.normalized() ) ) {
            fromDir = ImmutableVector2F.X_UNIT.negate();
        }

        ImmutableVector2F medianDir = fromDir.plus( toDir ).normalized();
        ImmutableVector2F motionDir = new ImmutableVector2F( Math.signum( medianDir.x ) * medianDir.y, -Math.abs( medianDir.x ) );

        float value = toDir.dot( pos ) * fromDir.dot( pos );
        float currentProgress = motionDir.dot( pos );
        float newProgress = currentProgress + timeElapsed * 5000;

        ImmutableVector2F highSolution;
        ImmutableVector2F lowSolution;
        {
            // vector a == fromDir
            float ax = fromDir.x;
            float ay = fromDir.y;
            // vector b == toDir
            float bx = toDir.x;
            float by = toDir.y;
            // vector d = motionDir
            float dx = motionDir.x;
            float dy = motionDir.y;

            float c = value;
            float p = newProgress;

            // we want to solve where
            // p = dot( d, x ) and
            // c = dot( a, x ) * dot( b, x )
            // this was solved by hand into these quadratic coefficients, and we can solve for both "possible" solutions
            // polyA * x^2 + polyB * x + polyC = 0
            float polyA = ax * bx - ( ax * by + ay * bx ) * ( dx / dy ) + ay * by * ( dx / dy ) * ( dx / dy );
            float polyB = ( ax * by + ay * bx ) * ( p / dy ) - ay * by * 2 * p * ( dx / ( dy * dy ) );
            float polyC = ay * by * p * p / ( dy * dy ) - c;

            // solve for x using the quadratic equation.
            float discriminant = polyB * polyB - 4 * polyA * polyC;
            if ( discriminant < 0 ) {
                System.out.println( "toDir = " + toDir );
                System.out.println( "fromDir = " + fromDir );
                System.out.println( "motionDir = " + motionDir );
                System.out.println( "sample.position = " + sample.getPosition() );
                System.out.println( "pos = " + pos );
                System.out.println( "currentProgress = " + currentProgress );
                System.out.println( "newProgress = " + newProgress );
                System.out.println( "value = " + value );
                throw new RuntimeException( "discriminant < 0" );
            }
            float largeX = (float) ( ( -polyB + Math.sqrt( discriminant ) ) / ( 2 * polyA ) );
            float smallX = (float) ( ( -polyB - Math.sqrt( discriminant ) ) / ( 2 * polyA ) );

            // solve for y based on the "progress" formula y=(p-x*dx)/dy
            float largeY = ( p - largeX * dx ) / dy;
            float smallY = ( p - smallX * dx ) / dy;

            highSolution = new ImmutableVector2F( largeX, largeY );
            lowSolution = new ImmutableVector2F( smallX, smallY );
        }

        // pick the solution that has a smaller y for now TODO: make sure this is right
        final ImmutableVector2F p = ( lowSolution.y < highSolution.y ? lowSolution : highSolution ).plus( origin );
        sample.setPosition( new ImmutableVector3F( p.x, p.y, sample.getPosition().z ) );
    }

    @Override public double getElevation( double x, double z ) {
        return 0;
    }

    @Override public double getDensity( double x, double y ) {
        return 0; // TODO
    }

    @Override public double getTemperature( double x, double y ) {
        return 0; // TODO
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
        return hasLeftPlate() && hasRightPlate();
    }

    public boolean allowsConvergentMotion() {
        // allow convergent motion unless both are the same type of oceanic plate
        return hasLeftPlate() && hasRightPlate() && ( leftPlateType.get() != rightPlateType.get()
                                                      || ( leftPlateType.get().isContinental() && rightPlateType.get().isContinental() ) );
    }

    public static void main( String[] args ) {
        System.out.println( getSimplifiedMantleTemperature( 0 ) );
        System.out.println( getSimplifiedMantleTemperature( 0 ) - getSimplifiedMantleTemperature( -1000 ) );
        System.out.println( getSimplifiedMantleTemperature( -20000 ) );
        System.out.println( getSimplifiedMantleTemperature( -100000 ) );
    }

    public boolean isTransformMotionCCW() {
        return transformMotionCCW;
    }

    public void setTransformMotionCCW( boolean transformMotionCCW ) {
        System.out.println( "transformMotionCCW = " + transformMotionCCW );
        this.transformMotionCCW = transformMotionCCW;
    }


}
