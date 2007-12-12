/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.glaciers.model.Movable.MovableListener;


public class Thermometer extends Movable {
    
    private double _temperature; // units=Celcius
    private ArrayList _listeners;
    
    public Thermometer( double temperature, Point2D position ) {
        super( position );
        _temperature = temperature;
        _listeners = new ArrayList();
    }
    
    public void setTemperature( double temperature ) {
        if ( temperature != _temperature ) {
            _temperature = temperature;
            notifyTemperatureChanged();
        }
    }
    
    public double getTemperature() {
        return _temperature;
    }
    
    public interface ThermometerListener extends MovableListener {
        public void temperatureChanged();
    }

    public static class ThermometerAdapter extends MovableAdapter implements ThermometerListener {
        public void temperatureChanged() {}
    }

    public void addListener( ThermometerListener listener ) {
        _listeners.add( listener );
        super.addListener( listener );
    }

    public void removeListener( ThermometerListener listener ) {
        _listeners.remove( listener );
        super.removeListener( listener );
    }
    
    public void removeAllListeners() {
        _listeners.clear();
        super.removeAllListeners();
    }

    private void notifyTemperatureChanged() {
        for ( int i = 0; i < _listeners.size(); i++ ) {
            ( (MovableListener) _listeners.get( i ) ).positionChanged();
        }
    }
}
