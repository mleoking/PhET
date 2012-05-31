// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.platetectonics.model.regions.CrossSectionStrip;
import edu.colorado.phet.platetectonics.util.Bounds3D;
import edu.colorado.phet.platetectonics.util.Grid3D;
import edu.colorado.phet.platetectonics.util.PiecewiseLinearFunction;

import static edu.colorado.phet.platetectonics.util.Side.LEFT;
import static edu.colorado.phet.platetectonics.util.Side.RIGHT;

/**
 * Displays a simplified block model of crusts resting on the mantle. Their elevation is dependent on
 * their density (temperature and composition), and the center crust is user controlled.
 */
public class CrustModel extends PlateModel {

    public static final float INNER_OUTER_CORE_BOUNDARY_Y = -5180000;
    public static final float MANTLE_CORE_BOUNDARY_Y = -2921000;
    public static final float UPPER_LOWER_MANTLE_BOUNDARY_Y = -750000;

    // X positions of the plate boundaries
    private static final int LEFT_BOUNDARY = -75000;
    private static final int RIGHT_BOUNDARY = 75000;

    private static final float MANTLE_DENSITY = 3300;
    private static final float CORE_BOUNDARY_DENSITY = 10000;
    private static final float INNER_OUTER_CORE_BOUNDARY_DENSITY = 12800;
    public static final float CENTER_DENSITY = 13100;

    // oceanic crust properties
    private static final double LEFT_OCEANIC_DENSITY = 3000;
    private static final double LEFT_OCEANIC_THICKNESS = 7000;
    private static final double LEFT_OCEANIC_ELEVATION = computeCrustElevation( LEFT_OCEANIC_THICKNESS, LEFT_OCEANIC_DENSITY );

    // continental crust properties
    private static final double RIGHT_CONTINENTAL_DENSITY = 2700;
    private static final double RIGHT_CONTINENTAL_THICKNESS = 45000;
    private static final double RIGHT_CONTINENTAL_ELEVATION = computeCrustElevation( RIGHT_CONTINENTAL_THICKNESS, RIGHT_CONTINENTAL_DENSITY );

    // "approximate" center crust temperature ratio (0 means lowest temperature, 1 means highest temperature)
    public final Property<Double> temperatureRatio = new Property<Double>( 0.5 );

    // "approximate" center crust composition ratio (0 means more iron, 1 means more silica)
    public final Property<Double> compositionRatio = new Property<Double>( 0.5 );

    // thickness of the center crust, in meters
    public final Property<Double> thickness = new Property<Double>( 20000.0 );
    private final Terrain oceanicTerrain;
    private final Terrain middleTerrain;
    private final Terrain continentalTerrain;
    private final List<TerrainConnectorStrip> terrainConnectors = new ArrayList<TerrainConnectorStrip>();

    private static final int CENTER_X_SAMPLES = 20;
    private static final int SIDE_X_SAMPLES = 50;
    private static final int TERRAIN_Z_SAMPLES = 20;

    private double crustElevation = computeIdealCrustElevation();
    private double crustVelocity = 0;

    // km, kg/m^3
    private static final int[] mantleDepthDensity = new int[] {
            0, 1020,
            3, 2600,
            15, 2900,
            25, 3381,
            71, 3376,
            171, 3364,
            220, 3436,
            271, 3466,
            371, 3526,
            400, 3723,
            471, 3813,
            571, 3939,
            670, 4381,
            771, 4443,
            871, 4503,
            971, 4563,
            1071, 4621,
            1171, 4678,
            1271, 4734,
            1371, 4789,
            1471, 4844,
            1571, 4897,
            1671, 4950,
            1771, 5003,
            1871, 5054,
            1971, 5106,
            2071, 5157,
            2171, 5207,
            2271, 5257,
            2371, 5307,
            2471, 5357,
            2571, 5407,
            2671, 5457,
            2771, 5506,
            2871, 5556,
            2891, 5566
    };
    private CrossSectionStrip middleCrustStrip;
    private SimpleObserver middleUpdateObserver;
    private CrossSectionStrip middleMantleStrip;

    public CrustModel( final Grid3D grid ) {
        super( grid.getBounds(), new TextureStrategy( 0.00002f ) );
        final Bounds3D bounds = grid.getBounds();

        /*---------------------------------------------------------------------------*
        * terrains
        *----------------------------------------------------------------------------*/

        oceanicTerrain = new Terrain( TERRAIN_Z_SAMPLES, bounds.getMinZ(), bounds.getMaxZ() ) {{
            for ( int col = SIDE_X_SAMPLES - 1; col >= 0; col-- ) {
                float ratio = ( (float) col ) / ( (float) ( SIDE_X_SAMPLES - 1 ) );
                ratio = (float) Math.pow( ratio, 0.3 );
                final float x = -PlateModel.MAX_FLAT_X + ratio * ( (float) LEFT_BOUNDARY - -PlateModel.MAX_FLAT_X );
                addColumn( LEFT, x, new ArrayList<TerrainSample>() {{
                    for ( int row = 0; row < TERRAIN_Z_SAMPLES; row++ ) {
                        final float rowRatio = ( (float) row ) / ( (float) ( TERRAIN_Z_SAMPLES - 1 ) );
                        float z = bounds.getMinZ() + rowRatio * ( bounds.getMaxZ() - bounds.getMinZ() );
                        add( new TerrainSample( (float) LEFT_OCEANIC_ELEVATION, getTextureStrategy().mapTop( new ImmutableVector2F( x, z ) ) ) );
                    }
                }} );
            }
        }};
        middleTerrain = new Terrain( TERRAIN_Z_SAMPLES, bounds.getMinZ(), bounds.getMaxZ() ) {{
            for ( int col = 0; col < SIDE_X_SAMPLES; col++ ) {
                float ratio = ( (float) col ) / ( (float) ( SIDE_X_SAMPLES - 1 ) );
                final float x = LEFT_BOUNDARY + ratio * ( RIGHT_BOUNDARY - LEFT_BOUNDARY );
                addColumn( RIGHT, x, new ArrayList<TerrainSample>() {{
                    for ( int row = 0; row < TERRAIN_Z_SAMPLES; row++ ) {
                        final float rowRatio = ( (float) row ) / ( (float) ( TERRAIN_Z_SAMPLES - 1 ) );
                        float z = bounds.getMinZ() + rowRatio * ( bounds.getMaxZ() - bounds.getMinZ() );
                        add( new TerrainSample( (float) getCenterCrustElevation(), getTextureStrategy().mapTop( new ImmutableVector2F( x, z ) ) ) );
                    }
                }} );
            }
        }};
        continentalTerrain = new Terrain( TERRAIN_Z_SAMPLES, bounds.getMinZ(), bounds.getMaxZ() ) {{
            for ( int col = 0; col < SIDE_X_SAMPLES; col++ ) {
                float ratio = ( (float) col ) / ( (float) ( SIDE_X_SAMPLES - 1 ) );
                ratio = (float) ( 1 - Math.pow( 1 - ratio, 0.3 ) );
                final float x = (float) RIGHT_BOUNDARY + ratio * ( PlateModel.MAX_FLAT_X - (float) RIGHT_BOUNDARY );
                addColumn( RIGHT, x, new ArrayList<TerrainSample>() {{
                    for ( int row = 0; row < TERRAIN_Z_SAMPLES; row++ ) {
                        final float rowRatio = ( (float) row ) / ( (float) ( TERRAIN_Z_SAMPLES - 1 ) );
                        float z = bounds.getMinZ() + rowRatio * ( bounds.getMaxZ() - bounds.getMinZ() );
                        add( new TerrainSample( (float) RIGHT_CONTINENTAL_ELEVATION, getTextureStrategy().mapTop( new ImmutableVector2F( x, z ) ) ) );
                    }
                }} );
            }
        }};
        addTerrain( oceanicTerrain );
        addTerrain( middleTerrain );
        addTerrain( continentalTerrain );

        // TODO: terrain connectors!
        TerrainConnectorStrip leftConnector = new TerrainConnectorStrip( oceanicTerrain, middleTerrain, 10, bounds.getMinZ(), bounds.getMaxZ() );
        terrainConnectors.add( leftConnector );
        addTerrain( leftConnector );

        TerrainConnectorStrip rightConnector = new TerrainConnectorStrip( middleTerrain, continentalTerrain, 10, bounds.getMinZ(), bounds.getMaxZ() );
        terrainConnectors.add( rightConnector );
        addTerrain( rightConnector );

        /*---------------------------------------------------------------------------*
        * regions
        *----------------------------------------------------------------------------*/

        final float lowerBoundary = bounds.getMinY();
        Function1<ImmutableVector2F, ImmutableVector2F> lowerBoundaryFunct = new Function1<ImmutableVector2F, ImmutableVector2F>() {
            public ImmutableVector2F apply( ImmutableVector2F vector ) {
                return new ImmutableVector2F( vector.x, lowerBoundary );
            }
        };

        ImmutableVector2F[] oceanTop = oceanicTerrain.getFrontVertices();
        final ImmutableVector2F[] oceanCrustBottom = map( oceanTop, new Function1<ImmutableVector2F, ImmutableVector2F>() {
            public ImmutableVector2F apply( ImmutableVector2F vector ) {
                return new ImmutableVector2F( vector.x, (float) ( vector.y - LEFT_OCEANIC_THICKNESS ) );
            }
        }, new ImmutableVector2F[oceanTop.length] );
        final ImmutableVector2F[] oceanSideMinY = map( oceanTop, lowerBoundaryFunct, new ImmutableVector2F[oceanTop.length] );

        ImmutableVector2F[] continentTop = continentalTerrain.getFrontVertices();
        ImmutableVector2F[] continentCrustBottom = map( continentTop, new Function1<ImmutableVector2F, ImmutableVector2F>() {
            public ImmutableVector2F apply( ImmutableVector2F vector ) {
                return new ImmutableVector2F( vector.x, (float) ( vector.y - RIGHT_CONTINENTAL_THICKNESS ) );
            }
        }, new ImmutableVector2F[continentTop.length] );
        ImmutableVector2F[] continentSideMinY = map( continentTop, lowerBoundaryFunct, new ImmutableVector2F[continentTop.length] );

        final ImmutableVector2F[] middleTop = middleTerrain.getFrontVertices();
        final ImmutableVector2F[] middleCrustBottom = new ImmutableVector2F[middleTop.length];
        final ImmutableVector2F[] middleSideMinY = map( middleTop, lowerBoundaryFunct, new ImmutableVector2F[middleTop.length] );

        final Sample[] middleTopSamples = new Sample[middleTop.length];
        final Sample[] middleCrustBottomSamples = new Sample[middleCrustBottom.length];
        final Sample[] middleMantleTopSamples = new Sample[middleCrustBottom.length];
        final Sample[] middleSideMinYSamples = new Sample[middleSideMinY.length];

        final Function1<Float, Float> middleCrustTemperatureFunction = new Function1<Float, Float>() {
            public Float apply( Float y ) {
                float ratio = (float) -( ( y - getCenterCrustElevation() ) / thickness.get() );
                return (float) ( ZERO_CELSIUS + ratio * 700 * temperatureRatio.get() );
            }
        };

        // initialize with dummy values (TODO fix)
        for ( int i = 0; i < middleTop.length; i++ ) {
            middleTopSamples[i] = new Sample( new ImmutableVector3F(),
                                              0, (float) getCenterCrustDensity(),
                                              new ImmutableVector2F() );
            middleCrustBottomSamples[i] = new Sample( new ImmutableVector3F(),
                                                      0, (float) getCenterCrustDensity(),
                                                      new ImmutableVector2F() );
            middleMantleTopSamples[i] = new Sample( new ImmutableVector3F(),
                                                    ZERO_CELSIUS + 700, MANTLE_DENSITY,
                                                    new ImmutableVector2F() );
            middleSideMinYSamples[i] = new Sample( new ImmutableVector3F( middleSideMinY[i].x, middleSideMinY[i].y, 0 ),
                                                   ZERO_CELSIUS + 700, MANTLE_DENSITY, getTextureStrategy().mapFront( middleSideMinY[i] ) );
        }

        middleUpdateObserver = new SimpleObserver() {
            public void update() {
                System.arraycopy( middleTerrain.getFrontVertices(), 0, middleTop, 0, middleTop.length );
                final float centerCrustDensity = (float) getCenterCrustDensity();
                for ( int i = 0; i < middleTop.length; i++ ) {
                    middleCrustBottom[i] = new ImmutableVector2F( middleTop[i].getX(), (float) ( middleTop[i].getY() - thickness.get() ) );
                }
                for ( int i = 0; i < middleTop.length; i++ ) {
                    final ImmutableVector3F crustTopPosition = new ImmutableVector3F( middleTop[i].x, middleTop[i].y, 0 );
                    final ImmutableVector3F crustBottomPosition = new ImmutableVector3F( middleCrustBottom[i].x, middleCrustBottom[i].y, 0 );
                    middleTopSamples[i].setPosition( crustTopPosition );
                    middleCrustBottomSamples[i].setPosition( crustBottomPosition );
                    middleTopSamples[i].setTextureCoordinates( getTextureStrategy().mapFront( new ImmutableVector2F( middleTop[i].x, 0 ) ) );
                    ImmutableVector2F position = new ImmutableVector2F( middleTop[i].x, -thickness.get() );
                    middleCrustBottomSamples[i].setTextureCoordinates( getTextureStrategy().mapFront( position ) );
                    middleMantleTopSamples[i].setPosition( crustBottomPosition );
                    middleMantleTopSamples[i].setTextureCoordinates( getTextureStrategy().mapFront( middleCrustBottom[i] ) );
                    middleTopSamples[i].setTemperature( middleCrustTemperatureFunction.apply( middleTop[i].y ) );
                    middleCrustBottomSamples[i].setTemperature( middleCrustTemperatureFunction.apply( middleCrustBottom[i].y ) );
                    middleTopSamples[i].setDensity( centerCrustDensity );
                    middleCrustBottomSamples[i].setDensity( centerCrustDensity );
                }
            }
        };

        temperatureRatio.addObserver( middleUpdateObserver, false );
        compositionRatio.addObserver( middleUpdateObserver, false );
        middleUpdateObserver.update();


        ImmutableVector2F[] mantleTop = new ImmutableVector2F[oceanSideMinY.length + middleSideMinY.length + continentSideMinY.length - 2];
        System.arraycopy( oceanSideMinY, 0, mantleTop, 0, oceanSideMinY.length );
        System.arraycopy( middleSideMinY, 0, mantleTop, oceanSideMinY.length - 1, middleSideMinY.length );
        System.arraycopy( continentSideMinY, 0, mantleTop, oceanSideMinY.length + middleSideMinY.length - 2, continentSideMinY.length );
        ImmutableVector2F[] mantleMiddle = map( mantleTop, new Function1<ImmutableVector2F, ImmutableVector2F>() {
            public ImmutableVector2F apply( ImmutableVector2F vector2f ) {
                return new ImmutableVector2F( vector2f.x, UPPER_LOWER_MANTLE_BOUNDARY_Y ); // to 750km depth
            }
        }, new ImmutableVector2F[mantleTop.length] );
        ImmutableVector2F[] mantleBottom = map( mantleTop, new Function1<ImmutableVector2F, ImmutableVector2F>() {
            public ImmutableVector2F apply( ImmutableVector2F vector2f ) {
                return new ImmutableVector2F( vector2f.x, MANTLE_CORE_BOUNDARY_Y ); // to 2921km depth
            }
        }, new ImmutableVector2F[mantleTop.length] );
        ImmutableVector2F[] innerOuterCoreBoundary = map( mantleTop, new Function1<ImmutableVector2F, ImmutableVector2F>() {
            public ImmutableVector2F apply( ImmutableVector2F vector2f ) {
                return new ImmutableVector2F( vector2f.x, INNER_OUTER_CORE_BOUNDARY_Y ); // to 5180km depth
            }
        }, new ImmutableVector2F[mantleTop.length] );
        ImmutableVector2F[] centerOfTheEarth = map( innerOuterCoreBoundary, new Function1<ImmutableVector2F, ImmutableVector2F>() {
            public ImmutableVector2F apply( ImmutableVector2F immutableVector2F ) {
                return new ImmutableVector2F( 0, CENTER_OF_EARTH_Y );
            }
        }, new ImmutableVector2F[innerOuterCoreBoundary.length] );

        /*---------------------------------------------------------------------------*
        * right oceanic crust
        *----------------------------------------------------------------------------*/
        {
            Sample[] tops = new Sample[oceanTop.length];
            Sample[] bottoms = new Sample[oceanTop.length];
            for ( int i = 0; i < oceanTop.length; i++ ) {
                tops[i] = new Sample( new ImmutableVector3F( oceanTop[i].x, oceanTop[i].y, 0 ),
                                      middleCrustTemperatureFunction.apply( oceanTop[i].y ), (float) LEFT_OCEANIC_DENSITY,
                                      getTextureStrategy().mapFront( oceanTop[i] ) );
                bottoms[i] = new Sample( new ImmutableVector3F( oceanCrustBottom[i].x, oceanCrustBottom[i].y, 0 ),
                                         middleCrustTemperatureFunction.apply( oceanCrustBottom[i].y ), (float) LEFT_OCEANIC_DENSITY,
                                         getTextureStrategy().mapFront( oceanCrustBottom[i] ) );
            }

            CrossSectionStrip strip = new CrossSectionStrip();
            for ( int i = 0; i < oceanTop.length; i++ ) {
                strip.addPatch( RIGHT, tops[i], bottoms[i] );
            }
            addStrip( strip );
        }

        /*---------------------------------------------------------------------------*
        * left continental crust
        *----------------------------------------------------------------------------*/
        {
            Sample[] tops = new Sample[continentTop.length];
            Sample[] bottoms = new Sample[continentTop.length];
            Function1<Float, Float> temperatureFunction = new Function1<Float, Float>() {
                public Float apply( Float y ) {
                    float ratio = (float) -( ( y - RIGHT_CONTINENTAL_ELEVATION ) / RIGHT_CONTINENTAL_THICKNESS );
                    return ZERO_CELSIUS + ratio * 450;
                }
            };
            for ( int i = 0; i < continentTop.length; i++ ) {
                tops[i] = new Sample( new ImmutableVector3F( continentTop[i].x, continentTop[i].y, 0 ),
                                      temperatureFunction.apply( continentTop[i].y ), (float) RIGHT_CONTINENTAL_DENSITY,
                                      getTextureStrategy().mapFront( continentTop[i] ) );
                bottoms[i] = new Sample( new ImmutableVector3F( continentCrustBottom[i].x, continentCrustBottom[i].y, 0 ),
                                         temperatureFunction.apply( continentCrustBottom[i].y ), (float) RIGHT_CONTINENTAL_DENSITY,
                                         getTextureStrategy().mapFront( continentCrustBottom[i] ) );
            }

            CrossSectionStrip strip = new CrossSectionStrip();
            for ( int i = 0; i < continentTop.length; i++ ) {
                strip.addPatch( RIGHT, tops[i], bottoms[i] );
            }
            addStrip( strip );
        }

        middleCrustStrip = new CrossSectionStrip() {{
            for ( int i = 0; i < middleTop.length; i++ ) {
                addPatch( RIGHT, middleTopSamples[i], middleCrustBottomSamples[i] );
            }
        }};
        addStrip( middleCrustStrip );
        middleMantleStrip = new CrossSectionStrip() {{
            for ( int i = 0; i < middleMantleTopSamples.length; i++ ) {
                addPatch( RIGHT, middleMantleTopSamples[i], middleSideMinYSamples[i] );
            }
        }};
        addStrip( middleMantleStrip );

        // other parts of upper mantles (not the middle part)
        addStrip( rectangularConstantStrip( oceanCrustBottom, oceanSideMinY,
                                            MANTLE_DENSITY, MANTLE_DENSITY,
                                            ZERO_CELSIUS + 700, ZERO_CELSIUS + 700 ) );
        addStrip( rectangularConstantStrip( continentCrustBottom, continentSideMinY,
                                            MANTLE_DENSITY, MANTLE_DENSITY,
                                            ZERO_CELSIUS + 700, ZERO_CELSIUS + 700 ) );

        // rest of upper mantle
        addStrip( rectangularConstantStrip( mantleTop, mantleMiddle,
                                            getMantleDensity( lowerBoundary ), getMantleDensity( UPPER_LOWER_MANTLE_BOUNDARY_Y ),
                                            ZERO_CELSIUS + 700, ZERO_CELSIUS + 1100 ) );

        // lower mantle
        addStrip( rectangularConstantStrip( mantleMiddle, mantleBottom,
                                            getMantleDensity( UPPER_LOWER_MANTLE_BOUNDARY_Y ), getMantleDensity( MANTLE_CORE_BOUNDARY_Y ),
                                            ZERO_CELSIUS + 1100, ZERO_CELSIUS + 4000 ) );

        // outer core
        addStrip( rectangularConstantStrip( mantleBottom, innerOuterCoreBoundary,
                                            CORE_BOUNDARY_DENSITY, INNER_OUTER_CORE_BOUNDARY_DENSITY,
                                            ZERO_CELSIUS + 4400f, ZERO_CELSIUS + 6100 ) );

        // inner core
        addStrip( rectangularConstantStrip( innerOuterCoreBoundary, centerOfTheEarth,
                                            INNER_OUTER_CORE_BOUNDARY_DENSITY, CENTER_DENSITY,
                                            5778, 5778 ) );

        updateView();
    }

    private CrossSectionStrip rectangularConstantStrip( final ImmutableVector2F[] top, final ImmutableVector2F[] bottom,
                                                        final float topDensity, final float bottomDensity,
                                                        final float topTemperature, final float bottomTemperature ) {
        return new CrossSectionStrip() {{
            for ( int i = 0; i < top.length; i++ ) {
                addPatch( RIGHT, new Sample( new ImmutableVector3F( top[i].x, top[i].y, 0 ), topTemperature, topDensity, getTextureStrategy().mapFront( top[i] ) ), new Sample( new ImmutableVector3F( bottom[i].x, bottom[i].y, 0 ), bottomTemperature, bottomDensity, getTextureStrategy().mapFront( bottom[i] ) ) );
            }
        }};
    }

    public static float getMantleDensity( float y ) {
        float depth = -y;
        assert depth >= 0;
        // TODO: could be faster
        for ( int i = 0; i < mantleDepthDensity.length; i += 2 ) {
            if ( depth < mantleDepthDensity[i] * 1000 ) {
                if ( i > 0 ) {
                    float minDepth = mantleDepthDensity[i - 2] * 1000;
                    float maxDepth = mantleDepthDensity[i] * 1000;
                    float minDensity = mantleDepthDensity[i - 1];
                    float maxDensity = mantleDepthDensity[i + 1];

                    float ratio = ( depth - minDepth ) / ( maxDepth - minDepth );
                    return minDensity + ratio * ( maxDensity - minDensity );
                }
                else {
                    throw new RuntimeException( "should not be reachable with first depth == 0" );
                }
            }
        }

        // below our max recorded depth, return the max density
        return mantleDepthDensity[mantleDepthDensity.length - 1];
    }

    private static <T, U> U[] map( T[] array, Function1<? super T, ? extends U> mapper, U[] resultArray ) {
        assert resultArray.length >= array.length;

        for ( int i = 0; i < array.length; i++ ) {
            resultArray[i] = mapper.apply( array[i] );
        }
        return resultArray;
    }

    private static <T> Function0<T> constantFunction( final T t ) {
        return new Function0<T>() {
            public T apply() {
                return t;
            }
        };
    }

    private static <T> Function0<T> propertyFunction( final Property<T> property ) {
        return new Function0<T>() {
            public T apply() {
                return property.get();
            }
        };
    }

    @Override public void update( double timeElapsed ) {
        timeElapsed *= 100;
        double idealCrustElevation = computeIdealCrustElevation();
        double delta = idealCrustElevation - crustElevation;

        double k = 0.01;

        double pe = 0.5 * k * delta * delta;
        double ke = 0.5 * crustVelocity * crustVelocity;
        double energy = pe + ke;

        // movement due to velocity
        crustElevation += crustVelocity * timeElapsed;

        // velocity change due to acceleration
        crustVelocity += delta * k * timeElapsed;

        // elapsed time-independent damping (looks OK at different framerates)
        crustVelocity /= Math.exp( timeElapsed / 9 );

        updateView();
    }

    private void updateView() {
        // update the middle elevation
        middleTerrain.setToFlatElevation( (float) getCenterCrustElevation() );
        middleTerrain.elevationChanged.updateListeners();

        // update the terrain connectors
        for ( TerrainConnectorStrip connector : terrainConnectors ) {
            connector.update();
        }

        middleUpdateObserver.update();

        // update the views for our strips that change
        middleCrustStrip.update();
        middleMantleStrip.update();

        // send out notifications
        modelChanged.updateListeners();
    }

    /**
     * Compute the elevation (above sea level) of a crustal piece, based on its thickness and density
     *
     * @param thickness Thickness (km)
     * @param density   Density (kg/m^3)
     * @return Elevation (m) above sea level (or below if negative)
     */
    private static double computeCrustElevation( double thickness, double density ) {
        // TODO (model): the "- 3500" part is a total hack. replace with correct version
        return thickness * ( 1 - density / MANTLE_DENSITY ) - 3500;
    }

    private double getCenterCrustDensity() {
        // TODO (model): replace this hack with the correct density function
        double ratio = 0.8 * ( 1 - compositionRatio.get() ) + 0.10 * ( 1 - temperatureRatio.get() );
        return 2600 + 700 * ratio;
    }

    public double getCenterCrustElevation() {
        return crustElevation;
    }

    private double computeIdealCrustElevation() {
        return computeCrustElevation( thickness.get(), getCenterCrustDensity() );
    }

    public double getCenterCrustBottomY() {
        return getCenterCrustElevation() - thickness.get();
    }

    @Override public double getElevation( double x, double z ) {
        if ( x < LEFT_BOUNDARY ) {
            // left "oceanic" crust
            return LEFT_OCEANIC_ELEVATION;
        }
        if ( x > RIGHT_BOUNDARY ) {
            // right "continental" crust
            return RIGHT_CONTINENTAL_ELEVATION;
        }
        // our crust
        return getCenterCrustElevation();
    }

    @Override public double getDensity( double x, double y ) {
        if ( x < LEFT_BOUNDARY ) {
            if ( y > 0 ) {
                return getAirDensity( y );
            }
            else if ( y > LEFT_OCEANIC_ELEVATION ) {
                return getWaterDensity( y );
            }
            else if ( y > LEFT_OCEANIC_ELEVATION - LEFT_OCEANIC_THICKNESS ) {
                return LEFT_OCEANIC_DENSITY;
            }
            else {
                return MANTLE_DENSITY;
            }
        }
        else if ( x > RIGHT_BOUNDARY ) {
            if ( y > RIGHT_CONTINENTAL_ELEVATION ) {
                return getAirDensity( y );
            }
            else if ( y > RIGHT_CONTINENTAL_ELEVATION - RIGHT_CONTINENTAL_THICKNESS ) {
                return RIGHT_CONTINENTAL_DENSITY;
            }
            else {
                return MANTLE_DENSITY;
            }
        }
        else if ( y > getCenterCrustElevation() ) {
            if ( y > 0 ) {
                return getAirDensity( y );
            }
            else {
                return getWaterDensity( y );
            }
        }
        else if ( y > getCenterCrustBottomY() ) {
            return getCenterCrustDensity();
        }
        else {
            return MANTLE_DENSITY;
        }
    }

    @Override public double getTemperature( double x, double y ) {
        // TODO: complete redo on this part?
        double elevation = getElevation( x, 0 );
        double surfaceTemperature = getSurfaceTemperature( elevation );

        if ( elevation >= y ) {
            // our point is under surface level
            double depth = elevation - y;
            double continental = getSimplifiedContinentalTemperature( depth, surfaceTemperature );
            double oceanic = getSimplifiedOceanicTemperature( depth, surfaceTemperature );
            if ( x < LEFT_BOUNDARY ) {
                return oceanic;
            }
            else if ( x > RIGHT_BOUNDARY ) {
                return continental;
            }
            else {
                // blend the two based on our "ratio"
                return continental + temperatureRatio.get() * ( oceanic - continental );
            }
        }
        else {
            // our point is above surface level
            if ( y > 0 ) {
                // above water level, so return air temp
                return getAirTemperature( y );
            }
            else {
                // return water temperature (simplified model)
                return getWaterTemperature( y );
            }
        }
    }

    private static PiecewiseLinearFunction simplifiedContinentalDifference = new PiecewiseLinearFunction(
            new ImmutableVector2D( 0, 0 ),
            new ImmutableVector2D( 40000, 500 ),
            new ImmutableVector2D( 150000, 1250 ),
            new ImmutableVector2D( 250000, 2000 ),
            new ImmutableVector2D( 2700000, 3400 ),
            new ImmutableVector2D( 2890000, 4000 ),
            new ImmutableVector2D( PlateModel.EARTH_RADIUS, 5778 )
    );

    private static PiecewiseLinearFunction simplifiedOceanicDifference = new PiecewiseLinearFunction(
            new ImmutableVector2D( 0, 0 ),
            new ImmutableVector2D( 50000, 1000 ),
            new ImmutableVector2D( 100000, 1500 ),
            new ImmutableVector2D( 150000, 1600 ),
            new ImmutableVector2D( 250000, 2000 ),
            new ImmutableVector2D( 2700000, 3400 ),
            new ImmutableVector2D( 2890000, 4000 ),
            new ImmutableVector2D( PlateModel.EARTH_RADIUS, 5778 )
    );

    public static double getSimplifiedContinentalTemperature( double depth, double surfaceTemperature ) {
        return surfaceTemperature + simplifiedContinentalDifference.apply( depth );
    }

    public static double getSimplifiedOceanicTemperature( double depth, double surfaceTemperature ) {
        return surfaceTemperature + simplifiedOceanicDifference.apply( depth );
    }

}
