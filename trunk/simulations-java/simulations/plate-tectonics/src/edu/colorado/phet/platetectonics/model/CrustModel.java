// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.platetectonics.model.Region.Type;
import edu.colorado.phet.platetectonics.util.Bounds3D;
import edu.colorado.phet.platetectonics.util.Grid3D;
import edu.colorado.phet.platetectonics.util.PiecewiseLinearFunction;

import com.jme3.math.Vector2f;

/**
 * Displays a simplified block model of crusts resting on the mantle. Their elevation is dependent on
 * their density (temperature and composition), and the center crust is user controlled.
 */
public class CrustModel extends PlateModel {

    // X positions of the plate boundaries
    private static final int LEFT_BOUNDARY = -75000;
    private static final int RIGHT_BOUNDARY = 75000;

    private static final double MANTLE_DENSITY = 3300;

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

    private static final int TERRAIN_X_SAMPLES = 20;
    private static final int TERRAIN_Z_SAMPLES = 20;
    private Runnable updateMiddleRegions;

    public CrustModel( final Grid3D grid ) {
        super( grid );
        final Bounds3D bounds = grid.getBounds();

        // update when anything is modified
        SimpleObserver updateObserver = new SimpleObserver() {
            public void update() {
                updateView();
            }
        };
        temperatureRatio.addObserver( updateObserver, false );
        compositionRatio.addObserver( updateObserver, false );
        thickness.addObserver( updateObserver, false );

        /*---------------------------------------------------------------------------*
        * terrains
        *----------------------------------------------------------------------------*/

        oceanicTerrain = new FlatTerrain( TERRAIN_X_SAMPLES, TERRAIN_Z_SAMPLES ) {{
            setXBounds( bounds.getMinX(), LEFT_BOUNDARY );
            setZBounds( bounds.getMinZ(), bounds.getMaxZ() );
            setElevation( (float) LEFT_OCEANIC_ELEVATION );
        }};
        middleTerrain = new FlatTerrain( TERRAIN_X_SAMPLES, TERRAIN_Z_SAMPLES ) {{
            setXBounds( LEFT_BOUNDARY, RIGHT_BOUNDARY );
            setZBounds( bounds.getMinZ(), bounds.getMaxZ() );
        }};
        continentalTerrain = new FlatTerrain( TERRAIN_X_SAMPLES, TERRAIN_Z_SAMPLES ) {{
            setXBounds( RIGHT_BOUNDARY, bounds.getMaxX() );
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

        Function1<Vector2f, Vector2f> lowerBoundaryFunct = new Function1<Vector2f, Vector2f>() {
            public Vector2f apply( Vector2f vector ) {
                return new Vector2f( vector.x, bounds.getMinY() );
            }
        };

        Vector2f[] oceanTop = oceanicTerrain.getFrontVertices();
        Vector2f[] oceanCrustBottom = map( oceanTop, new Function1<Vector2f, Vector2f>() {
                                               public Vector2f apply( Vector2f vector ) {
                                                   return new Vector2f( vector.x, (float) ( vector.y - LEFT_OCEANIC_THICKNESS ) );
                                               }
                                           }, new Vector2f[oceanTop.length] );
        Vector2f[] oceanSideMinY = map( oceanTop, lowerBoundaryFunct, new Vector2f[oceanTop.length] );

        Vector2f[] continentTop = continentalTerrain.getFrontVertices();
        Vector2f[] continentCrustBottom = map( continentTop, new Function1<Vector2f, Vector2f>() {
                                                   public Vector2f apply( Vector2f vector ) {
                                                       return new Vector2f( vector.x, (float) ( vector.y - RIGHT_CONTINENTAL_THICKNESS ) );
                                                   }
                                               }, new Vector2f[continentTop.length] );
        Vector2f[] continentSideMinY = map( continentTop, lowerBoundaryFunct, new Vector2f[continentTop.length] );

        final Vector2f[] middleTop = middleTerrain.getFrontVertices();
        final Vector2f[] middleCrustBottom = new Vector2f[middleTop.length];
        final Vector2f[] middleSideMinY = map( middleTop, lowerBoundaryFunct, new Vector2f[middleTop.length] );

        updateMiddleRegions = new Runnable() {
            public void run() {
                System.arraycopy( middleTerrain.getFrontVertices(), 0, middleTop, 0, middleTop.length );
                for ( int i = 0; i < middleTop.length; i++ ) {
                    middleCrustBottom[i] = new Vector2f( middleTop[i].getX(), (float) ( middleTop[i].getY() - thickness.get() ) );
                }
            }
        };
        updateMiddleRegions.run();

        Vector2f[] mantleTop = new Vector2f[oceanSideMinY.length + middleSideMinY.length + continentSideMinY.length - 2];
        System.arraycopy( oceanSideMinY, 0, mantleTop, 0, oceanSideMinY.length );
        System.arraycopy( middleSideMinY, 0, mantleTop, oceanSideMinY.length - 1, middleSideMinY.length );
        System.arraycopy( continentSideMinY, 0, mantleTop, oceanSideMinY.length + middleSideMinY.length - 2, continentSideMinY.length );
        Vector2f[] mantleMiddle = map( mantleTop, new Function1<Vector2f, Vector2f>() {
                                           public Vector2f apply( Vector2f vector2f ) {
                                               return new Vector2f( vector2f.x, -750000 ); // to 750km depth
                                           }
                                       }, new Vector2f[mantleTop.length] );
        Vector2f[] mantleBottom = map( mantleTop, new Function1<Vector2f, Vector2f>() {
                                           public Vector2f apply( Vector2f vector2f ) {
                                               return new Vector2f( vector2f.x, -2921000 ); // to 2921km depth
                                           }
                                       }, new Vector2f[mantleTop.length] );
        Vector2f[] innerOuterCoreBoundary = map( mantleTop, new Function1<Vector2f, Vector2f>() {
                                                     public Vector2f apply( Vector2f vector2f ) {
                                                         return new Vector2f( vector2f.x, -5180000 ); // to 5180km depth
                                                     }
                                                 }, new Vector2f[mantleTop.length] );
        Vector2f[] centerOfTheEarth = new Vector2f[] { new Vector2f( 0, -PlateModel.EARTH_RADIUS ) };

        addRegion( new SimpleRegion( Type.CRUST,
                                     oceanTop,
                                     oceanCrustBottom,
                                     constantFunction( LEFT_OCEANIC_DENSITY ),
                                     constantFunction( 0.0 ) ) ); // TODO: crustal temperatures!
        addRegion( new SimpleRegion( Type.UPPER_MANTLE,
                                     oceanCrustBottom,
                                     oceanSideMinY,
                                     constantFunction( MANTLE_DENSITY ),
                                     constantFunction( 0.0 ) ) ); // TODO: mandle temperatures!

        addRegion( new SimpleRegion( Type.CRUST,
                                     middleTop,
                                     middleCrustBottom,
                                     new Function0<Double>() {
                                         public Double apply() {
                                             return getCenterCrustDensity();
                                         }
                                     }, constantFunction( 0.0 ) ) ); // TODO: crustal temperatures!
        addRegion( new SimpleRegion( Type.UPPER_MANTLE,
                                     middleCrustBottom,
                                     middleSideMinY,
                                     constantFunction( MANTLE_DENSITY ),
                                     constantFunction( 0.0 ) ) ); // TODO: crustal temperatures!

        addRegion( new SimpleRegion( Type.CRUST,
                                     continentTop,
                                     continentCrustBottom,
                                     constantFunction( RIGHT_CONTINENTAL_DENSITY ),
                                     constantFunction( 0.0 ) ) ); // TODO: crustal temperatures!
        addRegion( new SimpleRegion( Type.UPPER_MANTLE,
                                     continentCrustBottom,
                                     continentSideMinY,
                                     constantFunction( MANTLE_DENSITY ),
                                     constantFunction( 0.0 ) ) ); // TODO: temperatures!

        addRegion( new SimpleRegion( Type.UPPER_MANTLE,
                                     mantleTop,
                                     mantleMiddle,
                                     constantFunction( MANTLE_DENSITY ), // TODO: fix upper mantle density
                                     constantFunction( 0.0 ) ) ); // TODO: temperatures!

        addRegion( new SimpleRegion( Type.LOWER_MANTLE,
                                     mantleMiddle,
                                     mantleBottom,
                                     constantFunction( MANTLE_DENSITY ), // TODO: fix lower mantle density
                                     constantFunction( 0.0 ) ) ); // TODO: temperatures!

        addRegion( new SimpleRegion( Type.OUTER_CORE,
                                     mantleBottom,
                                     innerOuterCoreBoundary,
                                     constantFunction( MANTLE_DENSITY ), // TODO: fix core density
                                     constantFunction( 0.0 ) ) ); // TODO: temperatures!

        addRegion( new SimpleRegion( Type.INNER_CORE,
                                     innerOuterCoreBoundary,
                                     centerOfTheEarth,
                                     constantFunction( MANTLE_DENSITY ), // TODO: fix core density
                                     constantFunction( 0.0 ) ) ); // TODO: temperatures!

        updateView();
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

    public void updateView() {
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

    private double getCenterCrustElevation() {
        return computeCrustElevation( thickness.get(), getCenterCrustDensity() );
    }

    private double getCenterCrustBottomY() {
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
            if ( y > LEFT_OCEANIC_ELEVATION - LEFT_OCEANIC_THICKNESS ) {
                return LEFT_OCEANIC_DENSITY;
            }
            else {
                return MANTLE_DENSITY;
            }
        }
        if ( x > RIGHT_BOUNDARY ) {
            if ( y > RIGHT_CONTINENTAL_ELEVATION - RIGHT_CONTINENTAL_THICKNESS ) {
                return RIGHT_CONTINENTAL_DENSITY;
            }
            else {
                return MANTLE_DENSITY;
            }
        }
        if ( y > getCenterCrustBottomY() ) {
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

    private static class SimpleRegion extends Region {
        private final Vector2f[] top;
        private final Vector2f[] bottom;
        private final Function0<Double> density;
        private final Function0<Double> temperature;
        private final boolean aStatic;

        public SimpleRegion( Type type, Vector2f[] top, Vector2f[] bottom, Function0<Double> density, Function0<Double> temperature ) {
            this( type, top, bottom, density, temperature, false );
        }

        public SimpleRegion( Type type, Vector2f[] top, Vector2f[] bottom, Function0<Double> density, Function0<Double> temperature, boolean isStatic ) {
            super( type );
            this.top = top;
            this.bottom = bottom;
            this.density = density;
            this.temperature = temperature;
            aStatic = isStatic;
        }

        // constant density over the entire surface
        @Override public float getDensity( Vector2f position ) {
            return density.apply().floatValue();
        }

        // constant temperature over the entire surface
        @Override public float getTemperature( Vector2f position ) {
            return temperature.apply().floatValue();
        }

        @Override public boolean isStatic() {
            return aStatic;
        }

        @Override public Vector2f[] getBoundary() {
            int numVertices = top.length + bottom.length;
            Vector2f[] vertices = new Vector2f[numVertices];
            System.arraycopy( top, 0, vertices, 0, top.length );
            for ( int i = 0; i < bottom.length; i++ ) {
                vertices[numVertices - i - 1] = bottom[i];
            }
            return vertices;
        }
    }
}
