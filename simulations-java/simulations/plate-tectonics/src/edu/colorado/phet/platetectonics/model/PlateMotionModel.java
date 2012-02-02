// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.FunctionalUtils;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.platetectonics.PlateTectonicsConstants;
import edu.colorado.phet.platetectonics.model.regions.CrossSectionStrip;
import edu.colorado.phet.platetectonics.util.Bounds3D;

public class PlateMotionModel extends PlateModel {

    private CrossSectionStrip leftCrustStrip;
    private CrossSectionStrip rightCrustStrip;
    private CrossSectionStrip leftMantleStrip;
    private CrossSectionStrip rightMantleStrip;

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

    private List<SamplePoint> leftCrustTopSamples = new ArrayList<SamplePoint>();
    private List<SamplePoint> rightCrustTopSamples = new ArrayList<SamplePoint>();
    private List<SamplePoint> leftCrustBottomSamples = new ArrayList<SamplePoint>();
    private List<SamplePoint> rightCrustBottomSamples = new ArrayList<SamplePoint>();
    private List<SamplePoint> leftMantleTopSamples = new ArrayList<SamplePoint>();
    private List<SamplePoint> rightMantleTopSamples = new ArrayList<SamplePoint>();
    private List<SamplePoint> leftMantleBottomSamples = new ArrayList<SamplePoint>();
    private List<SamplePoint> rightMantleBottomSamples = new ArrayList<SamplePoint>();


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

    public PlateMotionModel( final Bounds3D bounds ) {
        super( bounds );

        addDebugPropertyListener( "canRun", canRun );
        addDebugPropertyListener( "hasBothPlates", hasBothPlates );
        addDebugPropertyListener( "animationStarted", animationStarted );
        addDebugPropertyListener( "leftPlateType", leftPlateType );
        addDebugPropertyListener( "rightPlateType", rightPlateType );
        addDebugPropertyListener( "motionType", motionType );
        addDebugPropertyListener( "motionTypeIfStarted", motionTypeIfStarted );

        final float minX = bounds.getMinX();
        final float maxX = bounds.getMaxX();
        final float centerX = bounds.getCenterX();

        final float minZ = bounds.getMinZ();
        final float maxZ = bounds.getMaxZ();

        resetSamples();

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

        updateTerrain();
        updateStrips();

        leftMantleStrip = new CrossSectionStrip( leftMantleTopSamples, leftMantleBottomSamples );
        addStrip( leftMantleStrip );
        rightMantleStrip = new CrossSectionStrip( rightMantleTopSamples, rightMantleBottomSamples );
        addStrip( rightMantleStrip );
    }

    private void resetMantleStrips() {
        if ( leftMantleStrip != null ) {
            removeStrip( leftMantleStrip );
        }
        leftMantleStrip = new CrossSectionStrip( leftMantleTopSamples, leftMantleBottomSamples );
        addStrip( leftMantleStrip );

        if ( rightMantleStrip != null ) {
            removeStrip( rightMantleStrip );
        }
        rightMantleStrip = new CrossSectionStrip( rightMantleTopSamples, rightMantleBottomSamples );
        addStrip( rightMantleStrip );
    }

    private ImmutableVector2F textureMap( ImmutableVector2F position ) {
        return position.times( 0.00001f );
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

    private void resetSamples() {
        /*---------------------------------------------------------------------------*
         * samples (intial values)
         *----------------------------------------------------------------------------*/
        leftCrustTopSamples.clear();
        rightCrustTopSamples.clear();
        leftCrustBottomSamples.clear();
        rightCrustBottomSamples.clear();
        leftMantleTopSamples.clear();
        rightMantleTopSamples.clear();
        leftMantleBottomSamples.clear();
        rightMantleBottomSamples.clear();
        for ( int i = 0; i < sideCount; i++ ) {
            float leftX = getLeftX( i );
            float rightX = getRightX( i );

            // TODO: densities  (and change them when things are dropped in!)
            leftCrustTopSamples.add( new SamplePoint( new ImmutableVector3F( leftX, 0, 0 ), ZERO_CELSIUS,
                                                      2700.0f, textureMap( new ImmutableVector2F( leftX, 0 ) ) ) );
            rightCrustTopSamples.add( new SamplePoint( new ImmutableVector3F( rightX, 0, 0 ), ZERO_CELSIUS,
                                                       2700.0f, textureMap( new ImmutableVector2F( rightX, 0 ) ) ) );
            leftCrustBottomSamples.add( new SamplePoint( new ImmutableVector3F( leftX, SIMPLE_MANTLE_TOP_Y, 0 ), ZERO_CELSIUS + 450,
                                                         2700.0f, textureMap( new ImmutableVector2F( leftX, SIMPLE_MANTLE_TOP_Y ) ) ) );
            rightCrustBottomSamples.add( new SamplePoint( new ImmutableVector3F( rightX, SIMPLE_MANTLE_TOP_Y, 0 ), ZERO_CELSIUS + 450,
                                                          2700.0f, textureMap( new ImmutableVector2F( rightX, SIMPLE_MANTLE_TOP_Y ) ) ) );

            leftMantleTopSamples.add( new SamplePoint( new ImmutableVector3F( leftX, SIMPLE_MANTLE_TOP_Y, 0 ), ZERO_CELSIUS + 700,
                                                       3300f, textureMap( new ImmutableVector2F( leftX, SIMPLE_MANTLE_TOP_Y ) ) ) );
            rightMantleTopSamples.add( new SamplePoint( new ImmutableVector3F( rightX, SIMPLE_MANTLE_TOP_Y, 0 ), ZERO_CELSIUS + 700,
                                                        3300f, textureMap( new ImmutableVector2F( rightX, SIMPLE_MANTLE_TOP_Y ) ) ) );
            leftMantleBottomSamples.add( new SamplePoint( new ImmutableVector3F( leftX, SIMPLE_MANTLE_BOTTOM_Y, 0 ), ZERO_CELSIUS + 1300,
                                                          3300f, textureMap( new ImmutableVector2F( leftX, SIMPLE_MANTLE_BOTTOM_Y ) ) ) );
            rightMantleBottomSamples.add( new SamplePoint( new ImmutableVector3F( rightX, SIMPLE_MANTLE_BOTTOM_Y, 0 ), ZERO_CELSIUS + 1300,
                                                           3300f, textureMap( new ImmutableVector2F( rightX, SIMPLE_MANTLE_BOTTOM_Y ) ) ) );
        }
    }

    @Override public void resetAll() {
        super.resetAll();

        if ( leftCrustStrip != null ) {
            removeStrip( leftCrustStrip );
            leftCrustStrip = null;
        }
        leftPlateType.reset();

        if ( rightCrustStrip != null ) {
            removeStrip( rightCrustStrip );
            rightCrustStrip = null;
        }
        rightPlateType.reset();

        resetSamples();

        canRun.reset();
        motionType.reset();
        motionTypeIfStarted.reset();
        animationStarted.reset();

        resetMantleStrips(); // do this only after initializing the samples again
        updateStrips();
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
            final ImmutableVector3F top = new ImmutableVector3F( x, getFreshCrustTop( type ), 0 );
            final ImmutableVector3F bottom = new ImmutableVector3F( x, getFreshCrustBottom( type ), 0 );

            leftCrustTopSamples.get( i ).setPosition( top );
            leftCrustTopSamples.get( i ).setTextureCoordinates( textureMap( new ImmutableVector2F( top.x, top.y ) ) );
            leftCrustBottomSamples.get( i ).setPosition( bottom );
            leftCrustBottomSamples.get( i ).setTextureCoordinates( textureMap( new ImmutableVector2F( bottom.x, bottom.y ) ) );
            leftMantleTopSamples.get( i ).setPosition( bottom );
            leftMantleTopSamples.get( i ).setTextureCoordinates( textureMap( new ImmutableVector2F( bottom.x, bottom.y ) ) );
            leftCrustTopSamples.get( i ).setDensity( getFreshDensity( type ) );
            leftCrustBottomSamples.get( i ).setDensity( getFreshDensity( type ) );
        }
        updateStrips();
        updateTerrain();
        leftCrustStrip = new CrossSectionStrip( leftCrustTopSamples, leftCrustBottomSamples );
        addStrip( leftCrustStrip );
        modelChanged.updateListeners();
    }

    public void dropRightCrust( PlateType type ) {
        // TODO: update the middle crust sample sometime
        rightPlateType.set( type );
        for ( int i = 0; i < sideCount; i++ ) {
            float x = getRightX( i );
            final ImmutableVector3F top = new ImmutableVector3F( x, getFreshCrustTop( type ), 0 );
            final ImmutableVector3F bottom = new ImmutableVector3F( x, getFreshCrustBottom( type ), 0 );
            rightCrustTopSamples.get( i ).setPosition( top );
            rightCrustTopSamples.get( i ).setTextureCoordinates( textureMap( new ImmutableVector2F( top.x, top.y ) ) );
            rightCrustBottomSamples.get( i ).setPosition( bottom );
            rightCrustBottomSamples.get( i ).setTextureCoordinates( textureMap( new ImmutableVector2F( bottom.x, bottom.y ) ) );
            rightMantleTopSamples.get( i ).setPosition( bottom );
            rightMantleTopSamples.get( i ).setTextureCoordinates( textureMap( new ImmutableVector2F( bottom.x, bottom.y ) ) );
            rightCrustTopSamples.get( i ).setDensity( getFreshDensity( type ) );
            rightCrustBottomSamples.get( i ).setDensity( getFreshDensity( type ) );
        }
        updateStrips();
        updateTerrain();
        rightCrustStrip = new CrossSectionStrip( rightCrustTopSamples, rightCrustBottomSamples );
        addStrip( rightCrustStrip );
        modelChanged.updateListeners();
    }

    private void updateStrips() {
        for ( CrossSectionStrip strip : new CrossSectionStrip[] {
                leftCrustStrip, rightCrustStrip,
                leftMantleStrip, rightMantleStrip
        } ) {
            if ( strip != null ) {
                strip.update();
            }
        }
    }

    private void updateTerrain() {
        for ( int column = 0; column < sideCount; column++ ) {
            // left side
            ImmutableVector3F leftPosition = hasLeftPlate() ? leftCrustTopSamples.get( column ).getPosition() : leftCrustBottomSamples.get( column ).getPosition();
            for ( int row = 0; row < depthCount; row++ ) {
                // set the elevation for the whole column
                leftTerrain.setElevation( leftPosition.y, column, row );
            }
            leftTerrain.xData[column] = leftPosition.x;

            // right side
            ImmutableVector3F rightPosition = hasRightPlate() ? rightCrustTopSamples.get( column ).getPosition() : rightCrustBottomSamples.get( column ).getPosition();
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

    public List<SamplePoint> getAllSamples() {
        return FunctionalUtils.concat(
                leftCrustTopSamples,
                rightCrustTopSamples,
                leftCrustBottomSamples,
                rightCrustBottomSamples,
                leftMantleTopSamples,
                rightMantleTopSamples,
                leftMantleBottomSamples,
                rightMantleBottomSamples );
    }

    // TODO: conversion from double to float
    @Override public void update( double timeElapsed ) {
        super.update( timeElapsed );

        if ( hasLeftPlate() && hasRightPlate() ) {
            if ( motionType.get() == null ) {
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
}
