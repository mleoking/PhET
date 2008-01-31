/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;

/**
 * Climate is the model of climate.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Climate extends ClockAdapter {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double TEMPERATURE_MODERN_TIMES = 20; // sea level temperature in modern times, degrees C
    
    private static final double ACCUMULATION_MIN = 0.0; // minimum accumulation, meters/year
    private static final double ACCUMULATION_MAX = 2.0; // maximum accumulation, meters/year
    
    private static final double ABLATION_MODERN_TIMES = 45.0; // meters/year of ablation when temperature is TEMPERATURE_MODERN_TIMES at sea level
    private static final double ABLATION_VERSUS_ELEVATION = -0.011; // meters/year of ablation per meter of elevation increase
    private static final double ABLATION_VERSUS_TEMPERATURE_OFFSET = 1.3; // meters/year of ablation per degree C offset from TEMPERATURE_MODERN_TIMES
    private static final double ABLATION_MIN = 0.0; // minimum ablation, meters/year

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _temperatureOffset; // offset from temperature of modern times, degrees C
    private double _snowfallLapseRate; // meters/year of accumulation per meter of elevation increase
    private ArrayList _listeners; // list of ClimateListener
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Climate( double temperatureOffset, double snowfallLapseRate ) {
        _temperatureOffset = temperatureOffset;
        _snowfallLapseRate = snowfallLapseRate;
        _listeners = new ArrayList();
    }
    
    public void cleanup() {}
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setTemperatureOffset( double temperatureOffset ) {
        if ( temperatureOffset != _temperatureOffset ) {
            _temperatureOffset = temperatureOffset;
            notifyTemperatureChanged();
        }
    }
    
    public double getTemperatureOffset() {
        return _temperatureOffset;
    }
    
    public void setSnowfallLapseRate( double snowfallLapseRate ) {
        if ( snowfallLapseRate < 0 ) {
            throw new IllegalArgumentException( "snowfallLapseRate must be > 0: " + snowfallLapseRate );
        }
        if ( snowfallLapseRate != _snowfallLapseRate ) {
            _snowfallLapseRate = snowfallLapseRate;
            notifySnowfallChanged();
        }
    }
    
    public double getSnowfallLapseRate() {
        return _snowfallLapseRate;
    }
    
    public double getTemperature( double elevation ) {
        return TEMPERATURE_MODERN_TIMES + _temperatureOffset - ( 6.5 * elevation / 1E3 );
    }
    
    public double getAccumulation( double elevation ) {
        double accumulation = elevation * _snowfallLapseRate;
        if ( accumulation > ACCUMULATION_MAX ) {
            accumulation = ACCUMULATION_MAX;
        }
        else if ( accumulation < ACCUMULATION_MIN ) {
            accumulation = ACCUMULATION_MIN;
        }
        return accumulation;
    }

    public double getAblation( double elevation ) {
        double ablation = ABLATION_MODERN_TIMES + ( ABLATION_VERSUS_ELEVATION * elevation ) + ( ABLATION_VERSUS_TEMPERATURE_OFFSET * _temperatureOffset );
        if ( ablation < ABLATION_MIN ) {
            ablation = ABLATION_MIN;
        }
        return ablation;
    }
    
    //----------------------------------------------------------------------------
    // ClockAdapter overrides
    //----------------------------------------------------------------------------
    
    public void simulationTimeChanged( ClockEvent event ) {
        // do nothing
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
        for ( int i = 0; i < _listeners.size(); i++ ) {
            Object listener = _listeners.get( i );
            if ( listener instanceof ClimateListener ) {
                ( (ClimateListener) listener ).temperatureChanged();
            }
        }
    }
    
    private void notifySnowfallChanged() {
        for ( int i = 0; i < _listeners.size(); i++ ) {
            Object listener = _listeners.get( i );
            if ( listener instanceof ClimateListener ) {
                ( (ClimateListener) listener ).snowfallChanged();
            }
        }
    }
}

