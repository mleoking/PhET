/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;


public class Thermometer extends AbstractTool {
    
    private double _temperature; // units=Celcius
    private ArrayList _listeners;
    
    public Thermometer( Point2D position ) {
        this( position, 0 );
    }
    
    public Thermometer( Point2D position, double temperature ) {
        super( position );
        _temperature = temperature;
        _listeners = new ArrayList();
        addListener( new MovableAdapter() {
            public void positionChanged() {
                handlePositionChange();
            }
        });
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
    
    private void handlePositionChange() {
        //XXX recalculate the temperature, call setTemperature
    }

    public void stepInTime( double dt ) {
        // TODO update temperature as glacier evolves
    }
}
