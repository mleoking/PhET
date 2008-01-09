/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;


public class Thermometer extends AbstractTool {
    
    private Climate _climate;
    private double _temperature; // units=Celcius
    private ArrayList _listeners;
    
    public Thermometer( Point2D position, Climate climate ) {
        this( position, 0 );
        _climate = climate;
    }
    
    public Thermometer( Point2D position, double temperature ) {
        super( position );
        _temperature = temperature;
        _listeners = new ArrayList();
    }
    
    public void cleanup() {
        super.cleanup();
    }
    
    public double getTemperature() {
        return _temperature;
    }
    
    private void setTemperature( double temperature ) {
        if ( temperature != _temperature ) {
            _temperature = temperature;
            notifyTemperatureChanged();
        }
    }
    
    protected void handlePositionChanged() {
        updateTemperature();
    }

    protected void handleClockTimeChanged() {
        updateTemperature();
    }
    
    private void updateTemperature() {
        final double altitude = getY();
        setTemperature( _climate.getTemperature( altitude ) );
    }
    
    public interface ThermometerListener {
        public void temperatureChanged();
    }

    public void addListener( ThermometerListener listener ) {
        _listeners.add( listener );
    }

    public void removeListener( ThermometerListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyTemperatureChanged() {
        for ( int i = 0; i < _listeners.size(); i++ ) {
            ( (ThermometerListener) _listeners.get( i ) ).temperatureChanged();
        }
    }
}
