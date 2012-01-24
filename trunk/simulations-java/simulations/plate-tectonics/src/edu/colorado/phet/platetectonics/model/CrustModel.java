// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.platetectonics.model.regions.Region.Type;
import edu.colorado.phet.platetectonics.model.regions.SimpleConstantRegion;
import edu.colorado.phet.platetectonics.model.regions.SimpleLinearRegion;
import edu.colorado.phet.platetectonics.model.regions.SimpleRegion;
import edu.colorado.phet.platetectonics.util.Bounds3D;
import edu.colorado.phet.platetectonics.util.Grid3D;
import edu.colorado.phet.platetectonics.util.PiecewiseLinearFunction;

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
    private final FlatTerrain oceanicTerrain;
    private final FlatTerrain middleTerrain;
    private final FlatTerrain continentalTerrain;
    private final List<TerrainConnector> terrainConnectors = new ArrayList<TerrainConnector>();

    private static final int CENTER_X_SAMPLES = 20;
    private static final int SIDE_X_SAMPLES = 50;
    private static final int TERRAIN_Z_SAMPLES = 20;
    private Runnable updateMiddleRegions;

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

    public CrustModel( final Grid3D grid ) {
        super( grid.getBounds() );
        final Bounds3D bounds = grid.getBounds();

        // update when anything is modified
//        SimpleObserver updateObserver = new SimpleObserver() {
//            public void update() {
//                updateView();
//            }
//        };
//        temperatureRatio.addObserver( updateObserver, false );
//        compositionRatio.addObserver( updateObserver, false );
//        thickness.addObserver( updateObserver, false );

        /*---------------------------------------------------------------------------*
        * terrains
        *----------------------------------------------------------------------------*/

        oceanicTerrain = new FlatTerrain( SIDE_X_SAMPLES, TERRAIN_Z_SAMPLES ) {{
            // x-coordinate interpolation favoring the top more
            for ( int i = 0; i < numXSamples; i++ ) {
                float ratio = ( (float) i ) / ( (float) ( numXSamples - 1 ) );
                ratio = (float) Math.pow( ratio, 0.3 );
                xData[i] = -PlateModel.MAX_FLAT_X + ratio * ( (float) LEFT_BOUNDARY - -PlateModel.MAX_FLAT_X );
            }
            setZBounds( bounds.getMinZ(), bounds.getMaxZ() );
            setElevation( (float) LEFT_OCEANIC_ELEVATION );
        }};
        middleTerrain = new FlatTerrain( CENTER_X_SAMPLES, TERRAIN_Z_SAMPLES ) {{
            setXBounds( LEFT_BOUNDARY, RIGHT_BOUNDARY );
            setZBounds( bounds.getMinZ(), bounds.getMaxZ() );
        }};
        continentalTerrain = new FlatTerrain( SIDE_X_SAMPLES, TERRAIN_Z_SAMPLES ) {{
            // x-coordinate interpolation favoring the top more
            for ( int i = 0; i < numXSamples; i++ ) {
                float ratio = ( (float) i ) / ( (float) ( numXSamples - 1 ) );
                ratio = (float) ( 1 - Math.pow( 1 - ratio, 0.3 ) );
                xData[i] = (float) RIGHT_BOUNDARY + ratio * ( PlateModel.MAX_FLAT_X - (float) RIGHT_BOUNDARY );
            }
            setZBounds( bounds.getMinZ(), bounds.getMaxZ() );
            setElevation( (float) RIGHT_CONTINENTAL_ELEVATION );
        }};
        addTerrain( oceanicTerrain );
        addTerrain( middleTerrain );
        addTerrain( continentalTerrain );

        TerrainConnector leftConnector = new TerrainConnector( oceanicTerrain, middleTerrain, 10 );
        TerrainConnector rightConnector = new TerrainConnector( middleTerrain, continentalTerrain, 10 );
        terrainConnectors.add( leftConnector );
        terrainConnectors.add( rightConnector );
        addTerrain( leftConnector );
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
        ImmutableVector2F[] oceanCrustBottom = map( oceanTop, new Function1<ImmutableVector2F, ImmutableVector2F>() {
                                                        public ImmutableVector2F apply( ImmutableVector2F vector ) {
                                                            return new ImmutableVector2F( vector.x, (float) ( vector.y - LEFT_OCEANIC_THICKNESS ) );
                                                        }
                                                    }, new ImmutableVector2F[oceanTop.length] );
        ImmutableVector2F[] oceanSideMinY = map( oceanTop, lowerBoundaryFunct, new ImmutableVector2F[oceanTop.length] );

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

        updateMiddleRegions = new Runnable() {
            public void run() {
                System.arraycopy( middleTerrain.getFrontVertices(), 0, middleTop, 0, middleTop.length );
                for ( int i = 0; i < middleTop.length; i++ ) {
                    middleCrustBottom[i] = new ImmutableVector2F( middleTop[i].getX(), (float) ( middleTop[i].getY() - thickness.get() ) );
                }
            }
        };
        updateMiddleRegions.run();

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
        ImmutableVector2F[] centerOfTheEarth = new ImmutableVector2F[] { new ImmutableVector2F( 0, CENTER_OF_EARTH_Y ) };

        addRegion( new SimpleRegion( Type.CRUST,
                                     oceanTop,
                                     oceanCrustBottom ) {
            @Override public float getDensity( ImmutableVector2F position ) {
                return (float) LEFT_OCEANIC_DENSITY;
            }

            @Override public float getTemperature( ImmutableVector2F position ) {
                float ratio = (float) -( ( position.y - LEFT_OCEANIC_ELEVATION ) / LEFT_OCEANIC_THICKNESS );
                return ZERO_CELSIUS + ratio * 450;
            }

            @Override public boolean isStatic() {
                return false;
            }
        } );
        addRegion( new SimpleRegion( Type.CRUST,
                                     continentTop,
                                     continentCrustBottom ) {
            @Override public float getDensity( ImmutableVector2F position ) {
                return (float) RIGHT_CONTINENTAL_DENSITY;
            }

            @Override public float getTemperature( ImmutableVector2F position ) {
                float ratio = (float) -( ( position.y - RIGHT_CONTINENTAL_ELEVATION ) / RIGHT_CONTINENTAL_THICKNESS );
                return ZERO_CELSIUS + ratio * 450;
            }

            @Override public boolean isStatic() {
                return false;
            }
        } );
        addRegion( new SimpleRegion( Type.CRUST,
                                     middleTop,
                                     middleCrustBottom ) {
            @Override public float getDensity( ImmutableVector2F position ) {
                return (float) getCenterCrustDensity();
            }

            @Override public float getTemperature( ImmutableVector2F position ) {
                // surface 10 C
                // 30km ~ 300 C
                float ratio = (float) -( ( position.y - getCenterCrustElevation() ) / thickness.get() );
                return (float) ( ZERO_CELSIUS + ratio * 700 * temperatureRatio.get() );
            }

            @Override public boolean isStatic() {
                return false;
            }
        } );

        addRegion( new SimpleConstantRegion( Type.UPPER_MANTLE,
                                             oceanCrustBottom,
                                             oceanSideMinY,
                                             constantFunction( (double) MANTLE_DENSITY ),
                                             constantFunction( (double) ( ZERO_CELSIUS + 700 ) ) ) );
        addRegion( new SimpleConstantRegion( Type.UPPER_MANTLE,
                                             middleCrustBottom,
                                             middleSideMinY,
                                             constantFunction( (double) MANTLE_DENSITY ),
                                             constantFunction( (double) ( ZERO_CELSIUS + 700 ) ) ) );
        addRegion( new SimpleConstantRegion( Type.UPPER_MANTLE,
                                             continentCrustBottom,
                                             continentSideMinY,
                                             constantFunction( (double) MANTLE_DENSITY ),
                                             constantFunction( (double) ( ZERO_CELSIUS + 700 ) ) ) );

        addRegion( new SimpleLinearRegion( Type.UPPER_MANTLE,
                                           mantleTop,
                                           mantleMiddle,
                                           lowerBoundary,
                                           UPPER_LOWER_MANTLE_BOUNDARY_Y,
                                           getMantleDensity( lowerBoundary ),
                                           getMantleDensity( UPPER_LOWER_MANTLE_BOUNDARY_Y ),
                                           ZERO_CELSIUS + 700, ZERO_CELSIUS + 1100 ) );

        addRegion( new SimpleLinearRegion( Type.LOWER_MANTLE,
                                           mantleMiddle,
                                           mantleBottom,
                                           UPPER_LOWER_MANTLE_BOUNDARY_Y,
                                           MANTLE_CORE_BOUNDARY_Y,
                                           getMantleDensity( UPPER_LOWER_MANTLE_BOUNDARY_Y ),
                                           getMantleDensity( MANTLE_CORE_BOUNDARY_Y ),
                                           ZERO_CELSIUS + 1100, ZERO_CELSIUS + 4000 ) );

        addRegion( new SimpleLinearRegion( Type.OUTER_CORE,
                                           mantleBottom,
                                           innerOuterCoreBoundary,
                                           MANTLE_CORE_BOUNDARY_Y,
                                           INNER_OUTER_CORE_BOUNDARY_Y,
                                           CORE_BOUNDARY_DENSITY,
                                           INNER_OUTER_CORE_BOUNDARY_DENSITY,
                                           ZERO_CELSIUS + 4400f, ZERO_CELSIUS + 6100 ) );

        addRegion( new SimpleLinearRegion( Type.INNER_CORE,
                                           innerOuterCoreBoundary,
                                           centerOfTheEarth,
                                           INNER_OUTER_CORE_BOUNDARY_Y,
                                           CENTER_OF_EARTH_Y,
                                           INNER_OUTER_CORE_BOUNDARY_DENSITY,
                                           CENTER_DENSITY,
                                           5778, 5778 ) );

        updateView();
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
        middleTerrain.setElevation( (float) getCenterCrustElevation() );

        // update the terrain connectors
        for ( TerrainConnector connector : terrainConnectors ) {
            connector.update();
        }

        updateMiddleRegions.run();

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
            new ImmutableVector2D( 150000, 1250 )
    );

    private static PiecewiseLinearFunction simplifiedOceanicDifference = new PiecewiseLinearFunction(
            new ImmutableVector2D( 0, 0 ),
            new ImmutableVector2D( 50000, 1000 ),
            new ImmutableVector2D( 100000, 1500 ),
            new ImmutableVector2D( 150000, 1600 )
    );

    public static double getSimplifiedContinentalTemperature( double depth, double surfaceTemperature ) {
        return surfaceTemperature + simplifiedContinentalDifference.apply( depth );
    }

    public static double getSimplifiedOceanicTemperature( double depth, double surfaceTemperature ) {
        return surfaceTemperature + simplifiedOceanicDifference.apply( depth );
    }

}
