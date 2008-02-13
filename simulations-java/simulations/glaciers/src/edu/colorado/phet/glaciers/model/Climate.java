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
    private static final double MODERN_SNOWFALL_REFERENCE_ELEVATION = 4E3; // elevation where snowfall is 50% of max in modern times (meters)
    
    private static final double SNOWFALL_MAX = 2.0; // maximum accumulation (meters/year)
    private static final double SNOWFALL_TRANSITION_WIDTH = 300; // how wide the snow curve transition is (meters)
     
    private static final double ABLATION_SCALE_FACTOR = 30;
    private static final double ABLATION_TEMPERATURE_SCALE_FACTOR = 200;
    private static final double ABLATION_Z0 = 1300; // min elevation used to scale ablation (meters)
    private static final double ABLATION_Z1 = 4200; // max elevation used to scale ablation (meters)

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _temperature; // temperature at sea level (degrees C)
    private double _snowfallReferenceElevation; // the reference elevation where snowfall is 50% of max (meters)
    private ArrayList _listeners; // list of ClimateListener
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param temperature temperature at sea level (degrees C)
     * @param snowfallReferenceElevation the elevation where snowfall is 50% of max (meters)
     */
    public Climate( double temperature, double snowfallReferenceElevation ) {
        _temperature = temperature;
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
     * Gets the temperature at sea level in modern times.
     * 
     * @return degrees C
     */
    public static double getModernTemperature() {
        return MODERN_TEMPERATURE;
    }
    
    /**
     * Gets the snowfall reference elevation in modern times.
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
     * Sets the elevation where snowfall is 50% of max.
     * 
     * @param snowfallReferenceElevation meters
     */
    public void setSnowfallReferenceElevation( double snowfallReferenceElevation ) {
        if ( snowfallReferenceElevation != _snowfallReferenceElevation ) {
            System.out.println( "Climate.setSnowfallReferenceElevation " + snowfallReferenceElevation );//XXX
            _snowfallReferenceElevation = snowfallReferenceElevation;
            notifySnowfallChanged();
        }
    }
    
    /**
     * Gets the elevation where snowfall is 50% of max.
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
        double accumulation = SNOWFALL_MAX * ( 0.5 + ( ( 1 / Math.PI ) * Math.atan( ( elevation - _snowfallReferenceElevation ) / SNOWFALL_TRANSITION_WIDTH  ) ) );
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
    
    //----------------------------------------------------------------------------
    // Listener interface
    //----------------------------------------------------------------------------
    
    public interface ClimateListener {
        public void temperatureChanged();
        public void snowfallChanged();
    }
    
    public static class ClimateAdapter implements ClimateListener {
        public void temperatureChanged() {};
        public void snowfallChanged() {};
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

