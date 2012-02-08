// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.FunctionalUtils;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.platetectonics.PlateTectonicsConstants;
import edu.colorado.phet.platetectonics.model.regions.CrossSectionStrip;
import edu.colorado.phet.platetectonics.model.regions.Region;
import edu.colorado.phet.platetectonics.util.Bounds3D;

public class PlateMotionModel extends PlateModel {

    private Plate leftPlate;
    private Plate rightPlate;

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

    private TerrainStrip leftTerrain;
    private TerrainStrip rightTerrain;
    private TerrainConnectorStrip centerTerrain;

    private static final float SIMPLE_MANTLE_TOP_Y = -10000; // 10km depth
    private static final float SIMPLE_MANTLE_BOTTOM_Y = -500000; // 200km depth
    private static final float SIMPLE_MANTLE_TOP_TEMP = ZERO_CELSIUS + 700;
    private static final float SIMPLE_MANTLE_BOTTOM_TEMP = ZERO_CELSIUS + 1300;
    private static final float SIMPLE_MANTLE_DENSITY = 3300f;

    private static final float SIMPLE_CRUST_TOP_TEMP = ZERO_CELSIUS;
    private static final float SIMPLE_CRUST_BOTTOM_TEMP = ZERO_CELSIUS + 450;

    private static final int MANTLE_VERTICAL_SAMPLES = 2;
    private static final int CRUST_VERTICAL_SAMPLES = 2;
    private static final int HORIZONTAL_SAMPLES = 128;

    private final int TERRAIN_DEPTH_SAMPLES = 32;

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
    public PlateMotionModel( final Bounds3D bounds ) {
        super( bounds );

        resetPlates();
        resetTerrain();

        updateTerrain();
        updateStrips();
    }

    private ImmutableVector2F topTextureMap( ImmutableVector2F position ) {
        return position.times( 0.00000125f );
    }

    private ImmutableVector2F textureMap( ImmutableVector2F position ) {
        return position.times( 0.000005f );
    }

    private <T> void addDebugPropertyListener( final String name, final Property<T> property ) {
        property.addObserver( new SimpleObserver() {
                                  public void update() {
                                      if ( PlateTectonicsConstants.DEBUG.get() ) {
                                          System.out.println( name + " changed to " + property.get() );
                                      }
                                  }
                              }, false );
    }

    private void resetPlates() {
        if ( leftPlate != null ) {
            removePlate( leftPlate );
        }

        leftPlate = new Plate() {{
            addMantle( new Region( MANTLE_VERTICAL_SAMPLES, HORIZONTAL_SAMPLES, new Function2<Integer, Integer, SamplePoint>() {
                public SamplePoint apply( Integer yIndex, Integer xIndex ) {
                    // start with top first
                    float x = getLeftX( xIndex );
                    final float yRatio = ( (float) yIndex ) / ( (float) MANTLE_VERTICAL_SAMPLES - 1 );
                    float y = SIMPLE_MANTLE_TOP_Y + ( SIMPLE_MANTLE_BOTTOM_Y - SIMPLE_MANTLE_TOP_Y ) * yRatio;
                    float temp = SIMPLE_MANTLE_TOP_TEMP + ( SIMPLE_MANTLE_BOTTOM_TEMP - SIMPLE_MANTLE_TOP_TEMP ) * yRatio;
                    return new SamplePoint( new ImmutableVector3F( x, y, 0 ), temp, SIMPLE_MANTLE_DENSITY,
                                            textureMap( new ImmutableVector2F( x, y ) ) );
                }
            } ) );
        }};
        if ( rightPlate != null ) {
            removePlate( rightPlate );
        }
        rightPlate = new Plate() {{
            addMantle( new Region( MANTLE_VERTICAL_SAMPLES, HORIZONTAL_SAMPLES, new Function2<Integer, Integer, SamplePoint>() {
                public SamplePoint apply( Integer yIndex, Integer xIndex ) {
                    // start with top first
                    float x = getRightX( xIndex );
                    final float yRatio = ( (float) yIndex ) / ( (float) MANTLE_VERTICAL_SAMPLES - 1 );
                    float y = SIMPLE_MANTLE_TOP_Y + ( SIMPLE_MANTLE_BOTTOM_Y - SIMPLE_MANTLE_TOP_Y ) * yRatio;
                    float temp = SIMPLE_MANTLE_TOP_TEMP + ( SIMPLE_MANTLE_BOTTOM_TEMP - SIMPLE_MANTLE_TOP_TEMP ) * yRatio;
                    return new SamplePoint( new ImmutableVector3F( x, y, 0 ), temp, SIMPLE_MANTLE_DENSITY,
                                            textureMap( new ImmutableVector2F( x, y ) ) );
                }
            } ) );
        }};

        addPlate( leftPlate );
        addPlate( rightPlate );
    }

    public void resetTerrain() {
        final float minZ = bounds.getMinZ();
        final float maxZ = bounds.getMaxZ();

        if ( leftTerrain != null ) {
            removeStrip( leftTerrain );
        }
        leftTerrain = new TerrainStrip( TERRAIN_DEPTH_SAMPLES, minZ, maxZ ) {{
            for ( int xIndex = 0; xIndex < HORIZONTAL_SAMPLES; xIndex++ ) {
                final float x = leftPlate.getMantle().getTopBoundary().samples.get( xIndex ).getPosition().x;
                addToRight( x, new ArrayList<TerrainSamplePoint>() {{
                    for ( int zIndex = 0; zIndex < TERRAIN_DEPTH_SAMPLES; zIndex++ ) {
                        final float z = zPositions.get( zIndex );
                        // elevation to be fixed later
                        add( new TerrainSamplePoint( 0, topTextureMap( new ImmutableVector2F( x, z ) ) ) );
                    }
                }} );
            }
        }};
        if ( rightTerrain != null ) {
            removeStrip( rightTerrain );
        }
        rightTerrain = new TerrainStrip( TERRAIN_DEPTH_SAMPLES, minZ, maxZ ) {{
            for ( int xIndex = 0; xIndex < HORIZONTAL_SAMPLES; xIndex++ ) {
                final float x = rightPlate.getMantle().getTopBoundary().samples.get( xIndex ).getPosition().x;
                addToRight( x, new ArrayList<TerrainSamplePoint>() {{
                    for ( int zIndex = 0; zIndex < TERRAIN_DEPTH_SAMPLES; zIndex++ ) {
                        final float z = zPositions.get( zIndex );
                        // elevation to be fixed later
                        add( new TerrainSamplePoint( 0, topTextureMap( new ImmutableVector2F( x, z ) ) ) );
                    }
                }} );
            }
        }};
        if ( centerTerrain != null ) {
            removeStrip( centerTerrain );
        }
        centerTerrain = new TerrainConnectorStrip( leftTerrain, rightTerrain, 3, minZ, maxZ );
        addStrip( leftTerrain );
        addStrip( rightTerrain );
        addStrip( centerTerrain );
    }

    @Override public void resetAll() {
        super.resetAll();

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

    // i from 0 to HORIZONTAL_SAMPLES-1
    private float getLeftX( int i ) {
        return bounds.getMinX() + ( bounds.getCenterX() - bounds.getMinX() ) * ( (float) i ) / (float) ( HORIZONTAL_SAMPLES - 1 );
    }

    // i from 0 to HORIZONTAL_SAMPLES-1
    private float getRightX( int i ) {
        return bounds.getCenterX() + ( bounds.getMaxX() - bounds.getCenterX() ) * ( (float) i ) / (float) ( HORIZONTAL_SAMPLES - 1 );
    }

    public void dropLeftCrust( final PlateType type ) {
        leftPlate.addCrust( new Region( CRUST_VERTICAL_SAMPLES, HORIZONTAL_SAMPLES, new Function2<Integer, Integer, SamplePoint>() {
            public SamplePoint apply( Integer yIndex, Integer xIndex ) {
                // start with top first
                float x = getLeftX( xIndex );

                final float topY = getFreshCrustTop( type );
                final float bottomY = getFreshCrustBottom( type );

                final float yRatio = ( (float) yIndex ) / ( (float) CRUST_VERTICAL_SAMPLES - 1 );
                float y = topY + ( bottomY - topY ) * yRatio;

                // TODO: young/old oceanic crust differences!
                float temp = SIMPLE_CRUST_TOP_TEMP + ( SIMPLE_CRUST_BOTTOM_TEMP - SIMPLE_CRUST_TOP_TEMP ) * yRatio;
                return new SamplePoint( new ImmutableVector3F( x, y, 0 ), temp, getFreshDensity( type ),
                                        textureMap( new ImmutableVector2F( x, y ) ) );
            }
        } ) );

        // set the position/texture coordinates to be the same for the mantle top boundary
        leftPlate.getMantle().getTopBoundary().borrowPositionAndTexture( leftPlate.getCrust().getBottomBoundary() );

        leftPlateType.set( type );

        updateStrips();
        updateTerrain();
        modelChanged.updateListeners();
    }

    public void dropRightCrust( final PlateType type ) {
        rightPlate.addCrust( new Region( CRUST_VERTICAL_SAMPLES, HORIZONTAL_SAMPLES, new Function2<Integer, Integer, SamplePoint>() {
            public SamplePoint apply( Integer yIndex, Integer xIndex ) {
                // start with top first
                float x = getRightX( xIndex );

                final float topY = getFreshCrustTop( type );
                final float bottomY = getFreshCrustBottom( type );

                final float yRatio = ( (float) yIndex ) / ( (float) CRUST_VERTICAL_SAMPLES - 1 );
                float y = topY + ( bottomY - topY ) * yRatio;

                // TODO: young/old oceanic crust differences!
                float temp = SIMPLE_CRUST_TOP_TEMP + ( SIMPLE_CRUST_BOTTOM_TEMP - SIMPLE_CRUST_TOP_TEMP ) * yRatio;
                return new SamplePoint( new ImmutableVector3F( x, y, 0 ), temp, getFreshDensity( type ),
                                        textureMap( new ImmutableVector2F( x, y ) ) );
            }
        } ) );

        // set the position/texture coordinates to be the same for the mantle top boundary
        rightPlate.getMantle().getTopBoundary().borrowPositionAndTexture( rightPlate.getCrust().getBottomBoundary() );

        rightPlateType.set( type );

        updateStrips();
        updateTerrain();
        modelChanged.updateListeners();
    }

    private void updateStrips() {
        for ( CrossSectionStrip strip : getCrossSectionStrips() ) {
            strip.update();
        }
    }

    private void updateTerrain() {
        for ( int column = 0; column < HORIZONTAL_SAMPLES; column++ ) {
            // left side
            ImmutableVector3F leftPosition = ( hasLeftPlate() ? leftPlate.getCrust().getTopBoundary() : leftPlate.getMantle().getTopBoundary() ).samples.get( column ).getPosition();
            for ( int row = 0; row < TERRAIN_DEPTH_SAMPLES; row++ ) {
                // set the elevation for the whole column
                leftTerrain.getSample( column, row ).setElevation( leftPosition.y );
            }
            leftTerrain.xPositions.set( column, leftPosition.x );

            // right side
            ImmutableVector3F rightPosition = ( hasRightPlate() ? rightPlate.getCrust().getTopBoundary() : rightPlate.getMantle().getTopBoundary() ).samples.get( column ).getPosition();
            for ( int row = 0; row < TERRAIN_DEPTH_SAMPLES; row++ ) {
                rightTerrain.getSample( column, row ).setElevation( rightPosition.y );
            }
            rightTerrain.xPositions.set( column, rightPosition.x );
        }
        leftTerrain.elevationChanged.updateListeners();
        rightTerrain.elevationChanged.updateListeners();
        centerTerrain.update();
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

    public boolean hasLeftPlate() {
        return leftPlateType.get() != null;
    }

    public boolean hasRightPlate() {
        return rightPlateType.get() != null;
    }

    public List<SamplePoint> getAllSamples() {
        return FunctionalUtils.concat( leftPlate.getSamples(), rightPlate.getSamples() );
    }

    // TODO: conversion from double to float
    @Override public void update( double timeElapsed ) {
        super.update( timeElapsed );

        if ( hasLeftPlate() && hasRightPlate() ) {
            if ( motionType.get() == null ) {
                // default to a certain direction when we pick a motion-type this way
                setTransformMotionCCW( false );
                motionType.set( motionTypeIfStarted.get() );
            }

            animationStarted.set( true );
            if ( motionType.get() == MotionType.CONVERGENT ) {
                for ( SamplePoint sample : getAllSamples() ) {
                    transformSample( sample, (float) timeElapsed );
                }
            }
            else if ( motionType.get() == MotionType.DIVERGENT ) {
                for ( SamplePoint sample : getAllSamples() ) {
                    transformSample( sample, (float) -timeElapsed );
                }
            }
            else if ( motionType.get() == MotionType.TRANSFORM ) {
                // since time elapsed is in millions of years, here we have 3cm/year movement
                // halved, since we only want the slip to be at that speed
                float rightOffset = 30000f / 2 * (float) ( isTransformMotionCCW() ? -timeElapsed : timeElapsed );
                for ( SamplePoint sample : rightPlate.getSamples() ) {
                    sample.setPosition( sample.getPosition().plus( new ImmutableVector3F( 0, 0, rightOffset ) ) );
                }
                rightTerrain.shiftZ( rightOffset );
                for ( SamplePoint sample : leftPlate.getSamples() ) {
                    sample.setPosition( sample.getPosition().plus( new ImmutableVector3F( 0, 0, -rightOffset ) ) );
                }
                leftTerrain.shiftZ( -rightOffset );
            }
            updateStrips();
            updateTerrain();
        }

        modelChanged.updateListeners();
    }

    public void transformSample( SamplePoint sample, float timeElapsed ) {
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
        return hasLeftPlate() && hasRightPlate() && leftPlateType.get().isContinental() && rightPlateType.get().isContinental();
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
