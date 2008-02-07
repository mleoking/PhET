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
    
    private static final double TEMPERATURE_MODERN = 20; // temperature at sea level in modern times (degrees C)
    
    private static final double ACCUMULATION_MIN = 0.0; // minimum accumulation (meters/year)
    private static final double ACCUMULATION_MAX = 2.0; // maximum accumulation (meters/year)
    
    private static final double ABLATION_MODERN = 45.0; // ablation at sea level in modern times (meters/year)
    private static final double ABLATION_VERSUS_TEMPERATURE_OFFSET = 1.3; // ablation per degree C offset from TEMPERATURE_MODERN_TIMES (meters/year)
    private static final double ABLATION_VERSUS_ELEVATION = -0.011; // ablation per meter of elevation increase (meters/year/meter)
    private static final double ABLATION_MIN = 0.0; // minimum ablation (meters/year)

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _temperature; // temperature at sea level (degrees C)
    private double _snowfall; // accumulation per meter above sea level (meters/year/meter)
    private ArrayList _listeners; // list of ClimateListener
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param temperature temperature at sea level (degrees C)
     * @param snowfall accumulation per meter above sea level (meters/year/meter)
     */
    public Climate( double temperture, double snowfall ) {
        _temperature = temperture;
        _snowfall = snowfall;
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
     * Sets the snowfall, the accumulation per meter above sea level.
     * 
     * @param snowfall meters/year/meter
     */
    public void setSnowfall( double snowfall ) {
        if ( snowfall < 0 ) {
            throw new IllegalArgumentException( "snowfall must be >= 0: " + snowfall );
        }
        if ( snowfall != _snowfall ) {
            System.out.println( "Climate.setSnowfall " + snowfall );//XXX
            _snowfall = snowfall;
            notifySnowfallChanged();
        }
    }
    
    /**
     * Gets the snowfall, the accumulation per meter above sea level.
     * 
     * @return meters/year/meter
     */
    public double getSnowfall() {
        return _snowfall;
    }
    
    /**
     * Gets the temperature at sea level in modern times.
     * 
     * @return degrees C
     */
    public static double getModernTemperature() {
        return TEMPERATURE_MODERN;
    }
    
    /**
     * Gets the temperature at a specified elevation above sea level.
     * 
     * @param elevation meters
     * @return degrees C
     */
    public double getTemperature( double elevation ) {
        return _temperature - ( 6.5 * elevation / 1E3 );
    }
    
    /**
     * Gets the accumulation at a specified elevation above sea level
     * 
     * @param elevation meters
     * @return meters/year
     */
    public double getAccumulation( double elevation ) {
        double accumulation = elevation * _snowfall;
        if ( accumulation > ACCUMULATION_MAX ) {
            accumulation = ACCUMULATION_MAX;
        }
        else if ( accumulation < ACCUMULATION_MIN ) {
            accumulation = ACCUMULATION_MIN;
        }
        return accumulation;
    }

    /**
     * Gets the ablation at a specified elevation above sea level.
     * 
     * @param elevation meters
     * @return meters/year
     */
    public double getAblation( double elevation ) {
        double temperatureOffsetFromModernTimes = _temperature - TEMPERATURE_MODERN;
        double ablation = ABLATION_MODERN + ( ABLATION_VERSUS_TEMPERATURE_OFFSET * temperatureOffsetFromModernTimes ) + ( ABLATION_VERSUS_ELEVATION * elevation ) ;
        if ( ablation < ABLATION_MIN ) {
            ablation = ABLATION_MIN;
        }
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
        return getAccumulation( elevation ) - getAblation( elevation );
    }
    
    /**
     * Gets the mass balance, which is synonymous with glacial budget.
     * 
     * @param elevation meters
     * @return meters/year
     */
    public double getMassBalance( double elevation ) {
        return getGlacialBudget( elevation );
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

