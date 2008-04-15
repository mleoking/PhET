/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Climate is the model of climate.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Climate {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double MODERN_TEMPERATURE = 20; // temperature at sea level in modern times (degrees C)
    private static final double MODERN_SNOWFALL_REFERENCE_ELEVATION = 4000; // reference elevation for snowfall in modern times (meters)
    
    private static final double MAX_SNOWFALL_MUTILPIER = 2; // snowfall is multiplied by this value to get max snowfall
    private static final double SNOWFALL_TRANSITION_WIDTH = 300; // how wide the transition of the snowfall curve is (meters)
     
    private static final double ABLATION_SCALE_FACTOR = 30;
    private static final double ABLATION_TEMPERATURE_SCALE_FACTOR = 200;
    private static final double ABLATION_Z0 = 1300; // min elevation used to scale ablation (meters)
    private static final double ABLATION_Z1 = 4200; // max elevation used to scale ablation (meters)

    private static final double ELA_SEARCH_STARTING_ELEVATION = MODERN_SNOWFALL_REFERENCE_ELEVATION; // where to start searching for ELA (meters)
    private static final double ELA_SEARCH_STARTING_DELTA = -1000; // initial elevation delta when searching for ELA (meters)
    private static final double ELA_SEARCH_MIN_DELTA = 1; // smallest elevation delta when searching for ELA (meters)
    private static final double ELA_SEARCH_ALMOST_ZERO_GLACIAL_BUDGET = 1E-5; // when searching for ELA, any budget <= this value is considered zero
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _temperature; // temperature at sea level (degrees C)
    private double _snowfall; // snow accumulation (meters/year)
    private double _snowfallReferenceElevation; // reference elevation for snowfall (meters)
    private double _equilibriumLineAlitutude; // elevation where glacial budget is zero (meters)
    private ArrayList _listeners; // list of ClimateListener
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param temperature temperature at sea level (degrees C)
     * @param snowfall snowfall (meters/year)
     * @param snowfallReferenceElevation reference elevation for snowfall (meters)
     */
    public Climate( double temperature, double snowfall, double snowfallReferenceElevation ) {
        _temperature = temperature;
        _snowfall = snowfall;
        _snowfallReferenceElevation = snowfallReferenceElevation;
        _listeners = new ArrayList();
        updateEquilibriumLineAltitude();
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
            updateEquilibriumLineAltitude();
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
     * Sets the snowfall.
     * This much snowfall will occur at the snowfall reference elevation.
     * 
     * @param snowfall meters
     */
    public void setSnowfall( double snowfall ) {
        assert( snowfall >= 0 );
        if ( snowfall != _snowfall ) {
            _snowfall = snowfall;
            updateEquilibriumLineAltitude();
            notifySnowfallChanged();
        }
    }
    
    /**
     * Gets the snowfall.
     * This much snowfall will occur at the snowfall reference elevation.
     * 
     * @return meters
     */
    public double getSnowfall() {
        return _snowfall;
    }
    
    /**
     * Sets the maximum snowfall.
     * As elevation approaches infinity, accumulation will approach this value.
     * 
     * @return meters
     */
    public void setMaximumSnowfall( double maxSnowfall ) {
        setSnowfall( maxSnowfall / MAX_SNOWFALL_MUTILPIER );
    }
    
    /**
     * Gets the maximum snowfall.
     * As elevation approaches infinity, accumulation will approach this value.
     * 
     * @return meters
     */
    public double getMaximumSnowfall() {
        return MAX_SNOWFALL_MUTILPIER * _snowfall;
    }
    
    /**
     * Sets the reference elevation for snowfall.
     * 
     * @param snowfallReferenceElevation meters
     */
    public void setSnowfallReferenceElevation( double snowfallReferenceElevation ) {
        assert( snowfallReferenceElevation >= 0 );
        if ( snowfallReferenceElevation != _snowfallReferenceElevation ) {
            _snowfallReferenceElevation = snowfallReferenceElevation;
            updateEquilibriumLineAltitude();
            notifySnowfallReferenceElevationChanged();
        }
    }
    
    /**
     * Gets the reference elevation for snowfall.
     * 
     * @return meters
     */
    public double getSnowfallReferenceElevation() {
        return _snowfallReferenceElevation;
    }
    
    /**
     * Gets the temperature at a specified elevation above sea level.
     * 
     * @param elevation meters
     * @return degrees C
     */
    public double getTemperature( double elevation ) {
        assert( elevation >= 0 );
        return _temperature - ( 6.5 * elevation / 1E3 );
    }
    
    /**
     * Gets the accumulation at a specified elevation above sea level
     * 
     * @param elevation meters
     * @return meters/year
     */
    public double getAccumulation( double elevation ) {
        assert( elevation >= 0 );
        double accumulation = getMaximumSnowfall() * ( 0.5 + ( ( 1 / Math.PI ) * Math.atan( ( elevation - _snowfallReferenceElevation ) / SNOWFALL_TRANSITION_WIDTH  ) ) );
        assert( accumulation >= 0 );
        assert( accumulation <= getMaximumSnowfall() );
        return accumulation;
    }

    /**
     * Gets the ablation at a specified elevation above sea level.
     * 
     * @param elevation meters
     * @return meters/year
     */
    public double getAblation( double elevation ) {
        assert( elevation >= 0 );
        
        double ablation = 0;
        double temperatureOffset = _temperature - MODERN_TEMPERATURE;
        
        // ablation remains zero above a threshold
        double ablationThresholdElevation = ( temperatureOffset * ABLATION_TEMPERATURE_SCALE_FACTOR ) + ABLATION_Z1;
        if ( elevation <= ablationThresholdElevation ) {
            double term1 = ( elevation - ABLATION_Z0 - ( temperatureOffset * ABLATION_TEMPERATURE_SCALE_FACTOR ) );
            double term2 = ( ABLATION_Z1 - ABLATION_Z0 ) * 2 / Math.PI;
            ablation = ABLATION_SCALE_FACTOR * ( 1. - Math.sin( term1 / term2 ) );  
        }
        
        // add an offset so that ablation is never zero
        double offset = ( Math.atan( temperatureOffset / 2.5 ) / 3 ) + 0.5;
        ablation += offset;
        
        assert( ablation >= 0 );
        return ablation;
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
    
    public double getEquilibriumLineAltitude() {
        return _equilibriumLineAlitutude;
    }
    
    /*
     * Updates the equilibrium line altitude (ELA) by searching for the elevation where glacial budget = 0.
     * This uses a "divide and conquer" algorithm, gradually decreasing the sign and magnitude of dz until
     * we find a glacial budget that is close enough to 0, or until dz gets sufficiently small.
     */
    private void updateEquilibriumLineAltitude() {

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
            else if ( elevation > 10 * MODERN_SNOWFALL_REFERENCE_ELEVATION ) {
                System.err.println( "Climate.updateEquilibriumLineAltitude, elevation is outside our range of interest, elevation=: " + elevation );
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
        
        _equilibriumLineAlitutude = elevation;
    }
    
    //----------------------------------------------------------------------------
    // Listener interface
    //----------------------------------------------------------------------------
    
    public interface ClimateListener {
        public void temperatureChanged();
        public void snowfallChanged();
        public void snowfallReferenceElevationChanged();
    }
    
    public static class ClimateAdapter implements ClimateListener {
        public void temperatureChanged() {};
        public void snowfallChanged() {};
        public void snowfallReferenceElevationChanged() {};
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
    
    private void notifySnowfallReferenceElevationChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ClimateListener) i.next() ).snowfallReferenceElevationChanged();
        }
    }
}

