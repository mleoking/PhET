// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.FunctionalUtils;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0.Constant;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.platetectonics.PlateTectonicsConstants;
import edu.colorado.phet.platetectonics.model.regions.Region.Type;
import edu.colorado.phet.platetectonics.model.regions.SimpleConstantRegion;
import edu.colorado.phet.platetectonics.util.Bounds3D;

public class PlateMotionModel extends PlateModel {

    private SimpleConstantRegion leftCrustRegion;
    private SimpleConstantRegion rightCrustRegion;

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

    private final Terrain leftTerrain;
    private final Terrain rightTerrain;
    private final TerrainConnector centerTerrain;

    private static float SIMPLE_MANTLE_TOP_Y = -10000; // 10km depth
    private static float SIMPLE_MANTLE_BOTTOM_Y = -500000; // 200km depth

    private final int sideCount = 128;
    private final int totalCount = 2 * sideCount;
    private final int depthCount = 32;
    private final int bottomCount = 64;

    private List<Sample> leftCrustTopSamples = new ArrayList<Sample>();
    private List<Sample> rightCrustTopSamples = new ArrayList<Sample>();
    private List<Sample> leftCrustBottomSamples = new ArrayList<Sample>();
    private List<Sample> rightCrustBottomSamples = new ArrayList<Sample>();
    private List<Sample> bottomSamples = new ArrayList<Sample>();

    private final ImmutableVector2F[] leftCrustTop;
    private final ImmutableVector2F[] rightCrustTop;
    private final ImmutableVector2F[] leftCrustBottom;
    private final ImmutableVector2F[] rightCrustBottom;
    private final ImmutableVector2F[] mantleTop;
    private final ImmutableVector2F[] mantleBottom;

    // TODO: better handling for this. ugly
    public final Property<PlateType> leftPlateType = new Property<PlateType>( null );
    public final Property<PlateType> rightPlateType = new Property<PlateType>( null );
    public final Property<MotionType> motionType = new Property<MotionType>( null );

    public final Property<Boolean> animationStarted = new Property<Boolean>( false );

    public final Property<Boolean> hasBothPlates = new Property<Boolean>( false ) {{
        SimpleObserver observer = new SimpleObserver() {
            @Override public void update() {
                set( hasLeftPlate() && hasRightPlate() );
            }
        };
        leftPlateType.addObserver( observer );
        rightPlateType.addObserver( observer );
    }};

    public final Property<Boolean> canRun = new Property<Boolean>( false ) {{
        SimpleObserver observer = new SimpleObserver() {
            @Override public void update() {
                set( hasLeftPlate() && hasRightPlate() && motionType.get() != null );
            }
        };
        leftPlateType.addObserver( observer );
        rightPlateType.addObserver( observer );
        motionType.addObserver( observer );
    }};

    public PlateMotionModel( final Bounds3D bounds ) {
        super( bounds );

        addDebugPropertyListener( "canRun", canRun );
        addDebugPropertyListener( "hasBothPlates", hasBothPlates );
        addDebugPropertyListener( "animationStarted", animationStarted );
        addDebugPropertyListener( "leftPlateType", leftPlateType );
        addDebugPropertyListener( "rightPlateType", rightPlateType );
        addDebugPropertyListener( "motionType", motionType );

        final float minX = bounds.getMinX();
        final float maxX = bounds.getMaxX();
        final float centerX = bounds.getCenterX();

        final float minZ = bounds.getMinZ();
        final float maxZ = bounds.getMaxZ();

        /*---------------------------------------------------------------------------*
         * samples (intial values)
         *----------------------------------------------------------------------------*/
        for ( int i = 0; i < sideCount; i++ ) {
            float leftX = getLeftX( i );
            float rightX = getRightX( i );
            leftCrustTopSamples.add( new Sample( new ImmutableVector2F( leftX, 0 ) ) );
            rightCrustTopSamples.add( new Sample( new ImmutableVector2F( rightX, 0 ) ) );
            leftCrustBottomSamples.add( new Sample( new ImmutableVector2F( leftX, SIMPLE_MANTLE_TOP_Y ) ) );
            rightCrustBottomSamples.add( new Sample( new ImmutableVector2F( rightX, SIMPLE_MANTLE_TOP_Y ) ) );
        }
        for ( int i = 0; i < bottomCount; i++ ) {
            float x = minX + ( maxX - minX ) * ( (float) i ) / ( (float) ( bottomCount - 1 ) );
            bottomSamples.add( new Sample( new ImmutableVector2F( x, SIMPLE_MANTLE_BOTTOM_Y ) ) );
        }

        /*---------------------------------------------------------------------------*
         * terrains
         *----------------------------------------------------------------------------*/
        leftTerrain = new Terrain( sideCount, depthCount ) {{
            setXBounds( minX, centerX );
            setZBounds( minZ, maxZ );
        }};
        addTerrain( leftTerrain );
        rightTerrain = new Terrain( sideCount, depthCount ) {{
            setXBounds( centerX, maxX );
            setZBounds( minZ, maxZ );
        }};
        addTerrain( rightTerrain );
        centerTerrain = new TerrainConnector( leftTerrain, rightTerrain, 2 );
        addTerrain( centerTerrain );

        leftCrustTop = new ImmutableVector2F[sideCount];
        rightCrustTop = new ImmutableVector2F[sideCount];
        leftCrustBottom = new ImmutableVector2F[sideCount];
        rightCrustBottom = new ImmutableVector2F[sideCount];
        mantleTop = new ImmutableVector2F[totalCount];
        mantleBottom = new ImmutableVector2F[bottomCount];

        updateTerrain();
        updateRegions();

        for ( int i = 0; i < bottomCount; i++ ) {
            mantleBottom[i] = bottomSamples.get( i ).position;
        }

        addRegion( new SimpleConstantRegion( Type.UPPER_MANTLE, mantleTop, mantleBottom, new Constant<Double>( 3700.0 ),
                                             new Constant<Double>( 0.0 ) ) );
    }

    private <T> void addDebugPropertyListener( final String name, final Property<T> property ) {
        property.addObserver( new SimpleObserver() {
                                  @Override public void update() {
                                      if ( PlateTectonicsConstants.DEBUG.get() ) {
                                          System.out.println( name + " changed to " + property.get() );
                                      }
                                  }
                              }, false );
    }

    @Override public void resetAll() {
        super.resetAll();

        if ( leftCrustRegion != null ) {
            removeRegion( leftCrustRegion );
            leftCrustRegion = null;
        }
        leftPlateType.reset();

        if ( rightCrustRegion != null ) {
            removeRegion( rightCrustRegion );
            rightCrustRegion = null;
        }
        rightPlateType.reset();

        // TODO: refactor this, since it is used in init
        leftCrustBottomSamples.clear();
        rightCrustBottomSamples.clear();
        for ( int i = 0; i < sideCount; i++ ) {
            float leftX = getLeftX( i );
            float rightX = getRightX( i );
            leftCrustBottomSamples.add( new Sample( new ImmutableVector2F( leftX, SIMPLE_MANTLE_TOP_Y ) ) );
            rightCrustBottomSamples.add( new Sample( new ImmutableVector2F( rightX, SIMPLE_MANTLE_TOP_Y ) ) );
        }

        canRun.reset();
        motionType.reset();
        animationStarted.reset();

        updateRegions();
        updateTerrain();

        modelChanged.updateListeners();
    }

    // i from 0 to sideCount-1
    private float getLeftX( int i ) {
        return bounds.getMinX() + ( bounds.getCenterX() - bounds.getMinX() ) * ( (float) i ) / (float) ( sideCount - 1 );
    }

    // i from 0 to sideCount-1
    private float getRightX( int i ) {
        return bounds.getCenterX() + ( bounds.getMaxX() - bounds.getCenterX() ) * ( (float) i ) / (float) ( sideCount - 1 );
    }

    public void dropLeftCrust( PlateType type ) {
        // TODO: update the middle crust sample sometime
        leftPlateType.set( type );
        for ( int i = 0; i < sideCount; i++ ) {
            float x = getLeftX( i );
            leftCrustTopSamples.get( i ).position = new ImmutableVector2F( x, getFreshCrustTop( type ) );
            leftCrustBottomSamples.get( i ).position = new ImmutableVector2F( x, getFreshCrustBottom( type ) );
        }
        updateRegions();
        updateTerrain();
        leftCrustRegion = new SimpleConstantRegion( Type.CRUST, leftCrustTop, leftCrustBottom,
                                                    new Constant<Double>( (double) getFreshDensity( type ) ),
                                                    new Constant<Double>( 0.0 ) );
        addRegion( leftCrustRegion );
        modelChanged.updateListeners();
    }

    public void dropRightCrust( PlateType type ) {
        // TODO: update the middle crust sample sometime
        rightPlateType.set( type );
        for ( int i = 0; i < sideCount; i++ ) {
            float x = getRightX( i );
            rightCrustTopSamples.get( i ).position = new ImmutableVector2F( x, getFreshCrustTop( type ) );
            rightCrustBottomSamples.get( i ).position = new ImmutableVector2F( x, getFreshCrustBottom( type ) );
        }
        updateRegions();
        updateTerrain();
        rightCrustRegion = new SimpleConstantRegion( Type.CRUST, rightCrustTop, rightCrustBottom,
                                                     new Constant<Double>( (double) getFreshDensity( type ) ),
                                                     new Constant<Double>( 0.0 ) );
        addRegion( rightCrustRegion );
        modelChanged.updateListeners();
    }

    private void updateRegions() {
        if ( PlateTectonicsConstants.DEBUG.get() ) {
            System.out.println( "updateRegions" );
        }
        for ( int i = 0; i < sideCount; i++ ) {
            // left side
            mantleTop[i] = leftCrustBottomSamples.get( i ).position;
            leftCrustBottom[i] = leftCrustBottomSamples.get( i ).position;
            leftCrustTop[i] = leftCrustTopSamples.get( i ).position;

            // right side
            mantleTop[i + sideCount] = rightCrustBottomSamples.get( i ).position;
            rightCrustBottom[i] = rightCrustBottomSamples.get( i ).position;
            rightCrustTop[i] = rightCrustTopSamples.get( i ).position;
        }

        for ( int i = 0; i < bottomCount; i++ ) {
            mantleBottom[i] = bottomSamples.get( i ).position;
        }
    }

    private void updateTerrain() {
        if ( PlateTectonicsConstants.DEBUG.get() ) {
            System.out.println( "updateTerrain" );
        }
        for ( int column = 0; column < sideCount; column++ ) {
            // left side
            ImmutableVector2F leftPosition = hasLeftPlate() ? leftCrustTopSamples.get( column ).position : leftCrustBottomSamples.get( column ).position;
            for ( int row = 0; row < depthCount; row++ ) {
                // set the elevation for the whole column
                leftTerrain.setElevation( leftPosition.y, column, row );
            }
            leftTerrain.xData[column] = leftPosition.x;

            // right side
            ImmutableVector2F rightPosition = hasRightPlate() ? rightCrustTopSamples.get( column ).position : rightCrustBottomSamples.get( column ).position;
            for ( int row = 0; row < depthCount; row++ ) {
                rightTerrain.setElevation( rightPosition.y, column, row );
            }
            rightTerrain.xData[column] = rightPosition.x;
        }
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

    // TODO: conversion from double to float
    @Override public void update( double timeElapsed ) {
        super.update( timeElapsed );

        if ( hasLeftPlate() && hasRightPlate() ) {
            animationStarted.set( true );
            if ( motionType.get() == MotionType.CONVERGENT ) {
                for ( Sample sample : FunctionalUtils.concat(
                        leftCrustTopSamples,
                        rightCrustTopSamples,
                        leftCrustBottomSamples,
                        rightCrustBottomSamples,
                        bottomSamples ) ) {
                    transformSample( sample, (float) timeElapsed );
                }
            }
            updateRegions();
            updateTerrain();
        }

        modelChanged.updateListeners();
    }

    public void transformSample( Sample sample, float timeElapsed ) {
        ImmutableVector2F origin = new ImmutableVector2F( 0, 5005 );
        ImmutableVector2F toDir = ImmutableVector2F.Y_UNIT.negate();
        ImmutableVector2F fromDir = ImmutableVector2F.X_UNIT;

        ImmutableVector2F pos = sample.position.minus( origin );

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
                System.out.println( "sample.position = " + sample.position );
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
        sample.position = ( lowSolution.y < highSolution.y ? lowSolution : highSolution ).plus( origin );
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

    public static class Sample {
        public ImmutableVector2F position;

        public Sample( ImmutableVector2F position ) {
            this.position = position;
        }
    }

    public static void main( String[] args ) {
        System.out.println( getSimplifiedMantleTemperature( 0 ) );
        System.out.println( getSimplifiedMantleTemperature( 0 ) - getSimplifiedMantleTemperature( -1000 ) );
        System.out.println( getSimplifiedMantleTemperature( -20000 ) );
        System.out.println( getSimplifiedMantleTemperature( -100000 ) );
    }
}
