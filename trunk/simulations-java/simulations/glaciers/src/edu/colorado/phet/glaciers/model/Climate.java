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
    private static final double MODERN_SNOWFALL_REFERENCE_ELEVATION = 4E3; // reference elevation for snowfall in modern times (meters)
    
    private static final double SNOWFALL_TRANSITION_WIDTH = 300; // how wide the snow curve transition is (meters)
     
    private static final double ABLATION_SCALE_FACTOR = 30;
    private static final double ABLATION_TEMPERATURE_SCALE_FACTOR = 200;
    private static final double ABLATION_Z0 = 1300; // min elevation used to scale ablation (meters)
    private static final double ABLATION_Z1 = 4200; // max elevation used to scale ablation (meters)

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _temperature; // temperature at sea level (degrees C)
    private double _snowfall; // snow accumulation (meters/year)
    private double _snowfallReferenceElevation; // reference elevation for snowfall (meters)
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
    }
    
    /**
     * Call this before releasing all references to this object.
     */
    public void cleanup() {}
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    /**
     * Gets the temperature at sea level for modern times.
     * 
     * @return degrees C
     */
    public static double getModernTemperature() {
        return MODERN_TEMPERATURE;
    }
    
    /**
     * Gets the snowfall reference elevation for modern times.
     * 
     * @return meters
     */
    public static double getModernSnowfallReferenceElevation() {
        return MODERN_SNOWFALL_REFERENCE_ELEVATION;
    }
    
    /**
     * Sets the temperature at sea level.
     * 
     * @param temperature degrees C
     */
    public void setTemperature( double temperature ) {
        if ( temperature != _temperature ) {
            System.out.println( "Climate.setTemperature " + temperature );//XXX
            _temperature = temperature;
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
            System.out.println( "Climate.setSnowfall " + snowfall );//XXX
            _snowfall = snowfall;
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
     * Sets the reference elevation for snowfall.
     * 
     * @param snowfallReferenceElevation meters
     */
    public void setSnowfallReferenceElevation( double snowfallReferenceElevation ) {
        assert( snowfallReferenceElevation >= 0 );
        if ( snowfallReferenceElevation != _snowfallReferenceElevation ) {
            System.out.println( "Climate.setSnowfallReferenceElevation " + snowfallReferenceElevation );//XXX
            _snowfallReferenceElevation = snowfallReferenceElevation;
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
        double accumulation = 2. * _snowfall * ( 0.5 + ( ( 1 / Math.PI ) * Math.atan( ( elevation - _snowfallReferenceElevation ) / SNOWFALL_TRANSITION_WIDTH  ) ) );
        assert( accumulation >= 0 );
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
        double temperatureOffset = _temperature - MODERN_TEMPERATURE;
        double ablationThresholdElevation = ( temperatureOffset * ABLATION_TEMPERATURE_SCALE_FACTOR ) + ABLATION_Z1;
        double ablation = 0;
        if ( elevation <= ablationThresholdElevation ) {
            double term1 = ( elevation - ABLATION_Z0 - ( temperatureOffset * ABLATION_TEMPERATURE_SCALE_FACTOR ) );
            double term2 = ( ABLATION_Z1 - ABLATION_Z0 ) * 2 / Math.PI;
            double tmp = ABLATION_SCALE_FACTOR * ( 1. - Math.sin( term1 / term2 ) );  
            double offset = ( Math.atan( temperatureOffset / 2.5 ) / 3 ) + 0.5;
            ablation = tmp + offset;
        }
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
    
    /**
     * Converts ELA (equilibrium line altitude) to temperature.
     * This is a fudge, since an ELA doesn't map to a single value.
     * 
     * @param ela meters
     * @return degrees C
     */
    public double elaToTemperature( double ela ) {
        double temperatureOffset = ( ela - MODERN_SNOWFALL_REFERENCE_ELEVATION ) / ABLATION_TEMPERATURE_SCALE_FACTOR;
        return MODERN_TEMPERATURE + temperatureOffset;
    }
    
    /**
     * Converts ELA (equilibrium line altitude) to snowfall reference elevation.
     * This is a fudge, since an ELA doesn't map to a single value.
     * 
     * @param ela meters
     * @return meters
     */
    public double elaToSnowfallReferenceElevation( double ela ) {
        double ablation = getAblation( ela );
        double term = Math.PI * ( ( ablation / _snowfall ) - 0.5 );
        return ela - ( SNOWFALL_TRANSITION_WIDTH * Math.tan( term ) );
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

