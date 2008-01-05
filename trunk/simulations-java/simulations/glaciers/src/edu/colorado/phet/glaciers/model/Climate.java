/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.ModelElement;


public class Climate implements ModelElement {
    
    private static final double DELTA_TEMPERATURE = -6.6E-3; // degrees C per meter
    private static final double DELTA_PRECIPITATION = 2; // (meters per year) per meter
    
    private final double _referenceAltitude; // meters
    private double _referenceTemperature; // degrees C
    private double _referencePrecipitation; // meters per years
    private ArrayList _listeners;
    
    public Climate() {
        this( 0 ); // reference altitude is sea level
    }
    
    public Climate( double referenceAltitude ) {
        _referenceAltitude = referenceAltitude;
        _referenceTemperature = 20;
        _referencePrecipitation = 0;
        _listeners = new ArrayList();
    }
    
    public void cleanup() {}
    
    public double getReferenceAltitude() {
        return _referenceAltitude;
    }
    
    public void setReferenceTemperature( double referenceTemperature ) {
        if ( referenceTemperature != _referenceTemperature ) {
            _referenceTemperature = referenceTemperature;
            notifyReferenceTemperatureChanged();
        }
    }
    
    public double getReferenceTemperature() {
        return _referenceTemperature;
    }
    
    public void setReferencePrecipitation( double referencePrecipitation ) {
        if ( referencePrecipitation < 0 ) {
            throw new IllegalArgumentException( "referencePrecipition must be > 0: " + referencePrecipitation );
        }
        if ( referencePrecipitation != _referencePrecipitation ) {
            _referencePrecipitation = referencePrecipitation;
            notifyReferencePrecipitationChanged();
        }
    }
    
    public double getReferencePrecipition() {
        return _referencePrecipitation;
    }
    
    public double getTemperature( double altitude ) {
        return _referenceTemperature + ( ( altitude - _referenceAltitude ) * DELTA_TEMPERATURE );
    }
    
    public double getPrecipitation( double altitude ) {
        return _referencePrecipitation + ( ( altitude - _referenceAltitude ) * DELTA_PRECIPITATION );
    }

    public void stepInTime( double dt ) {
        // do nothing
    }
    
    public interface ClimateListener {
        public void referenceTemperatureChanged();
        public void referencePrecipitationChanged();
    }
    
    public static class ClimateAdapter implements ClimateListener {
        public void referenceTemperatureChanged() {};
        public void referencePrecipitationChanged() {};
    }
    
    public void addListener( ClimateListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeListener( ClimateListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyReferenceTemperatureChanged() {
        for ( int i = 0; i < _listeners.size(); i++ ) {
            Object listener = _listeners.get( i );
            if ( listener instanceof ClimateListener ) {
                ( (ClimateListener) listener ).referenceTemperatureChanged();
            }
        }
    }
    
    private void notifyReferencePrecipitationChanged() {
        for ( int i = 0; i < _listeners.size(); i++ ) {
            Object listener = _listeners.get( i );
            if ( listener instanceof ClimateListener ) {
                ( (ClimateListener) listener ).referencePrecipitationChanged();
            }
        }
    }
}

