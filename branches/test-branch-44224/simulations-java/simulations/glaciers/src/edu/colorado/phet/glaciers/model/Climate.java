/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Climate is the model of climate.
 * Documentation is in glaciers/doc/model.txt.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Climate {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // temperature
    private static final double TEMP_LAPSE_RATE = 6.5E-3; // C per meter
    
    // ablation
    private static final double MELT_V_ELEV = 30; // ablation rate per meter of elevation
    private static final double MELT_Z0 = 1100; // min elevation used to scale ablation (meters)
    private static final double MELT_Z1 = 4300; // max elevation used to scale ablation (meters)
    private static final double MELT_V_TEMP = 80;  // ablation curve shift per degree Celcius
    private static final double MODERN_TEMP = 21; // temperature at sea level in modern times (degrees C)

    // accumulation
    private static final double SNOW_MAX = 2.0; // maximum possible snowfall (meters)
    private static final double SNOW_MIN_ELEV = 1800; // min elevation used to scale accumulation (meters)
    private static final double SNOW_MAX_ELEV = 4600; // max elevation used to scale accumulation (meters)
    private static final double SNOW_TRANSITION_WIDTH = 300; // how wide the transition of the snowfall curve is (meters)
    
    // ELA
    private static final double ELA_MAX = 8000; // meters, this should be above the top of the birds-eye view
    private static final double ELA_SEARCH_STARTING_ELEVATION = 4000; // where to start searching for ELA (meters)
    private static final double ELA_SEARCH_STARTING_DELTA = -1000; // initial elevation delta when searching for ELA (meters)
    private static final double ELA_SEARCH_MIN_DELTA = 1; // smallest elevation delta when searching for ELA (meters)
    private static final double ELA_SEARCH_ALMOST_ZERO_GLACIAL_BUDGET = 1E-5; // when searching for ELA, any budget <= this value is considered zero
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _temperature; // temperature at sea level (degrees C)
    private double _snowfall; // average snow accumulation for the valley (meters/year)
    private double _ela; // equilibrium line altitude, elevation where glacial budget is zero (meters)
    private final ArrayList _listeners; // list of ClimateListener
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param temperature temperature at sea level (degrees C)
     * @param snowfall average snowfall for the valley (meters/year)
     */
    public Climate( double temperature, double snowfall ) {
        _temperature = temperature;
        _snowfall = snowfall;
        _listeners = new ArrayList();
        updateELA();
    }
    
    /**
     * Call this before releasing all references to this object.
     */
    public void cleanup() {}
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    /**
     * Sets the temperature at sea level.
     * 
     * @param temperature degrees C
     */
    public void setTemperature( double temperature ) {
        if ( temperature != _temperature ) {
            _temperature = temperature;
            updateELA();
            notifyTemperatureChanged();
        }
    }
    
    /**
     * Gets the temperature at sea level.
     *  
     * @return degrees C
     */
    public double getTemperature() {
        return _temperature;
    }
    
    /**
     * Sets the average snowfall.
     * 
     * @param snowfall meters
     */
    public void setSnowfall( double snowfall ) {
        assert( snowfall >= 0 );
        assert( snowfall <= SNOW_MAX );
        if ( snowfall != _snowfall ) {
            _snowfall = snowfall;
            updateELA();
            notifySnowfallChanged();
        }
    }
    
    /**
     * Gets the average snowfall.
     * 
     * @return meters
     */
    public double getSnowfall() {
        return _snowfall;
    }
    
    /**
     * Gets the temperature at a specified elevation above sea level.
     * 
     * @param elevation meters
     * @return degrees C
     */
    public double getTemperature( double elevation ) {
        assert( elevation >= 0 );
        return _temperature - ( TEMP_LAPSE_RATE * elevation );
    }

    /**
     * Gets the ablation at a specified elevation above sea level.
     * 
     * @param elevation meters
     * @return meters/year
     */
    public double getAblation( double elevation ) {
        assert ( elevation >= 0 );
        // base ablation
        double ablation = 0;
        final double tempDiff = _temperature - MODERN_TEMP;
        final double minAblationElevation = ( tempDiff * MELT_V_TEMP ) + MELT_Z1;
        if ( elevation <= minAblationElevation ) {
            ablation = MELT_V_ELEV * ( 1. - Math.sin( ( elevation - MELT_Z0 - ( tempDiff * MELT_V_TEMP ) ) / ( ( MELT_Z1 - MELT_Z0 ) * 2 / Math.PI ) ) );
        }
        // offset
        final double offset = ( 5.5E-5 * Math.pow( tempDiff + 9, 5 ) ) + ( 0.01 * ( tempDiff - 9 ) ) + 0.3;
        ablation = ablation + offset;
        assert ( ablation >= 0 );
        return ablation;
    }
    
    /**
     * Gets the accumulation at a specified elevation above sea level.
     * 
     * @param elevation meters
     * @return meters/year
     */
    public double getAccumulation( double elevation ) {
        assert( elevation >= 0 );
        final double s = _snowfall / 1.5;
        final double p0 = SNOW_MAX_ELEV - ( ( SNOW_MAX_ELEV - SNOW_MIN_ELEV ) * s );
        final double pMax = SNOW_MAX * s;
        final double tmp = .5 + ( 1. / Math.PI ) * Math.atan( ( elevation - p0 ) / SNOW_TRANSITION_WIDTH );
        final double accumulation = pMax * tmp;
        assert( accumulation >= 0 );
        assert( accumulation <= SNOW_MAX );
        return accumulation;
    }
    
    /**
     * Gets the glacial budget at a specified elevation above sea level.
     * Glacial budget is the difference between accumulation and ablation.
     * A positive value indicates a surplus, and the glacier grows.
     * A negative value indicates a shortage, and the glacier shrinks.
     * A zero value indicates equilibrium.
     * 
     * @param elevation meters
     * @return meters/year
     */
    public double getGlacialBudget( double elevation ) {
        assert( elevation >= 0 );
        return getAccumulation( elevation ) - getAblation( elevation );
    }
    
    /**
     * Gets the ELA (equilibrium line altitude).
     * 
     * @return
     */
    public double getELA() {
        return _ela;
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the equilibrium line altitude (ELA) by searching for the elevation where glacial budget = 0.
     * This uses a "divide and conquer" algorithm, gradually decreasing the sign and magnitude of dz until
     * we find a glacial budget that is close enough to 0, or until dz gets sufficiently small.
     * 
     * Note that, for "warm" climates, small variations in the climate parameters will result in
     * large ELA changes.  This behavior may appear strange, but it is accurate according to Archie Paulson.
     */
    private void updateELA() {

        double elevation = ELA_SEARCH_STARTING_ELEVATION;
        double deltaElevation = ELA_SEARCH_STARTING_DELTA;
        double glacialBudget = getGlacialBudget( elevation );
        double newGlacialBudget = 0;
        
        while ( Math.abs( glacialBudget ) > ELA_SEARCH_ALMOST_ZERO_GLACIAL_BUDGET && Math.abs( deltaElevation ) >= ELA_SEARCH_MIN_DELTA ) {
            
            // next elevation
            elevation += deltaElevation;
            if ( elevation < 0 ) {
                elevation = 0;
            }
            else if ( elevation > ELA_MAX ) {
                /*
                 * Limit the ELA to an artificial maximum, which should be off the top of the birds-eye view.
                 * We're doing this so that we don't have to wait a long time for the glacier to "grow"
                 * when we start with no glacier and change to a cooler climate.
                 */
                elevation = ELA_MAX;
                break;
            }
            
            newGlacialBudget = getGlacialBudget( elevation );
            
            // if we've gone too far in our search, change directions
            if ( ( deltaElevation > 0 && newGlacialBudget > 0 && newGlacialBudget > glacialBudget ) || 
                 ( deltaElevation < 0 && newGlacialBudget < 0 && newGlacialBudget < glacialBudget ) ) {
                deltaElevation = -( deltaElevation / 2. );
            }
            
            glacialBudget = newGlacialBudget;
        }
        
        _ela = elevation;
    }
    
    //----------------------------------------------------------------------------
    // Listener interface
    //----------------------------------------------------------------------------
    
    public interface ClimateListener {
        public void temperatureChanged();
        public void snowfallChanged();
    }
    
    public static class ClimateAdapter implements ClimateListener {
        public void temperatureChanged() {}
        public void snowfallChanged() {}
    }
    
    public void addClimateListener( ClimateListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeClimateListener( ClimateListener listener ) {
        _listeners.remove( listener );
    }
    
    //----------------------------------------------------------------------------
    // Notification of changes
    //----------------------------------------------------------------------------
    
    private void notifyTemperatureChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ClimateListener) i.next() ).temperatureChanged();
        }
    }
    
    private void notifySnowfallChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ClimateListener) i.next() ).snowfallChanged();
        }
    }
}

