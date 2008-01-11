/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.glaciers.model.Climate.ClimateAdapter;
import edu.colorado.phet.glaciers.model.Climate.ClimateListener;


public class Thermometer extends AbstractTool {
    
    private Climate _climate;
    private ClimateListener _climateListener;
    private double _temperature; // units=Celcius
    private ArrayList _listeners; // list of ThermometerListener
    
    public Thermometer( Point2D position, Climate climate ) {
        this( position, 0 );
        _climate = climate;
        _climateListener = new ClimateAdapter() {
            public void referenceTemperatureChanged() {
                updateTemperature();
            }
        };
        _climate.addClimateListener( _climateListener );
    }
    
    public Thermometer( Point2D position, double temperature ) {
        super( position );
        _temperature = temperature;
        _listeners = new ArrayList();
    }
    
    public void cleanup() {
        super.cleanup();
        _climate.removeClimateListener( _climateListener );
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

    private void updateTemperature() {
        final double elevation = getElevation();
        final double temperature = _climate.getTemperature( elevation );
        setTemperature( temperature );
    }
    
    public interface ThermometerListener {
        public void temperatureChanged();
    }

    public void addThermometerListener( ThermometerListener listener ) {
        _listeners.add( listener );
    }

    public void removeThermometerListener( ThermometerListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyTemperatureChanged() {
        for ( int i = 0; i < _listeners.size(); i++ ) {
            ( (ThermometerListener) _listeners.get( i ) ).temperatureChanged();
        }
    }
}
