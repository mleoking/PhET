// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.platetectonics.util.PiecewiseLinearFunction;

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

    public CrustModel() {
        // fire a change event when anything is modified
        SimpleObserver fireModelChanged = new SimpleObserver() {
            public void update() {
                modelChanged.updateListeners();
            }
        };
        temperatureRatio.addObserver( fireModelChanged );
        compositionRatio.addObserver( fireModelChanged );
        thickness.addObserver( fireModelChanged );
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
        double elevation = getElevation( x, 0 );
        double surfaceTemperature = getSurfaceTemperature( elevation );

        if ( elevation > y ) {
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
