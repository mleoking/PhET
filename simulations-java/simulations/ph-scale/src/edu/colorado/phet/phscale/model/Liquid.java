package edu.colorado.phet.phscale.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;


public class Liquid extends ClockAdapter {
    
    private static final double MAX_VOLUME = 1.25;
    private static final double AVOGADROS_NUMBER = 6.023E23;
    private static final double H2O_CONCENTRATION = 55; // moles/L
    
    private final ArrayList _listeners;
    
    private LiquidDescriptor _liquidDescriptor;
    private double _pH;
    private double _volume; // liters
    private boolean _draining;
    private double _drainingRate;
    private boolean _filling;
    private double _fillRate;
    private LiquidDescriptor _fillLiquidDescriptor;
    private double _fillVolume;
    
    public Liquid( LiquidDescriptor liquidDescriptor, double volume ) {
        assert( volume >= 0 );
        _listeners = new ArrayList();
        _draining = false;
        _filling = false;
        setLiquidDescriptor( liquidDescriptor );
    }
    
    public double getMaxVolume() {
        return MAX_VOLUME;
    }
    
    public LiquidDescriptor getLiquidDescriptor() {
        return _liquidDescriptor;
    }
    
    public void setLiquidDescriptor( LiquidDescriptor liquidDescriptor ) {
        _liquidDescriptor = liquidDescriptor;
        _pH = liquidDescriptor.getPH();
        _volume = 0;
        notifyStateChanged();
    }
    
//    public void add( LiquidDescriptor liquid, double volume ) {
//        assert( volume >= 0 );
//        _pH = -Math.log( ( Math.pow( 10, -_pH * _volume ) + Math.pow( 10, -liquid.getPH() * volume ) ) / ( _volume + volume ) );
//        _volume += volume;
//        notifyStateChanged();
//    }
    
    public boolean isEmpty() {
        return _volume == 0;
    }
    
    public boolean isFull() {
        return _volume == MAX_VOLUME;
    }
    
    public void startDraining( double drainingRate ) {
        if ( !_draining && !isEmpty() ) {
            _filling = false;
            _draining = true;
            _drainingRate = drainingRate;
            notifyStateChanged();
        }
    }
    
    public void stopDraining() {
        if ( _draining ) {
            _draining = false;
            notifyStateChanged();
        }
    }
    
    public boolean isDraining() {
        return _draining;
    }
    
    public void drainImmediately() {
        if ( !isEmpty() ) {
            _volume = 0;
            notifyStateChanged();
        }
    }
    
    public void startFilling( double fillRate, LiquidDescriptor fillLiquidDescriptor ) {
        startFilling( fillRate, fillLiquidDescriptor, MAX_VOLUME );
    }
    
    public void startFilling( double fillRate, LiquidDescriptor fillLiquidDescriptor, double fillVolume ) {
        if ( !_filling && !isFull() && fillVolume > _volume ) {
            _draining = false;
            _filling = true;
            _fillRate = fillRate;
            _fillLiquidDescriptor = fillLiquidDescriptor;
            _fillVolume = fillVolume;
            notifyStateChanged();
        }
    }
    
    public void stopFilling() {
        if ( _filling ) {
            _filling = false;
            notifyStateChanged();
        }
    }
    
    public boolean isFilling() {
        return _filling;
    }
    
    public void setPH( double pH ) {
        _pH = pH;
        notifyStateChanged();
    }
    
    public double getPH() {
        return _pH;
    }
    
    public double getVolume() {
        return _volume;
    }
    
    public Color getColor() {
        return _liquidDescriptor.getColor(); //XXX need to dilute this
    }
    
    public void setConcentrationH3O( double c ) {
        _pH = -Math.log( c );
        notifyStateChanged();
    }
    
    public double getConcentrationH3O() {
        return Math.pow( 10, -_pH );
    }
  
    public void setConcentrationOH( double c ) {
        _pH = 14 - ( -Math.log( c ) );
        notifyStateChanged();
    }
    
    public double getConcentrationOH() {
        return Math.pow( 10, -( 14 - _pH ) );
    }
    
    public double getConcentrationH2O() {
        return H2O_CONCENTRATION;
    }
    
    public double getNumberOfMoleculesH3O() {
        return getConcentrationH3O() * AVOGADROS_NUMBER * _volume;
    }

    public double getNumberOfMoleculesOH() {
        return getConcentrationOH() * AVOGADROS_NUMBER * _volume;
    }

    public double getNumberOfMoleculesH2O() {
        return getConcentrationH2O() * AVOGADROS_NUMBER * _volume;
    }
    
    public void setNumberOfMolesH3O( double m ) {
        _pH = -Math.log( m / _volume );
        notifyStateChanged();
    }
    
    public double getNumberOfMolesH3O() {
        return _volume * getConcentrationH3O();
    }
    
    public void setNumberOfMolesOH( double m ) {
        _pH = 14 - ( -Math.log( m / _volume ) );
        notifyStateChanged();
    }
    
    public double getNumberOfMolesOH() {
        return _volume * getConcentrationOH();
    }
    
//    public void setNumberOfMolesH2O( double m ) {
//        double volumeChange = ( m / getConcentrationH2O() ) - _volume;
//        add( LiquidDescriptor.WATER, volumeChange );
//    }
    
    public double getNumberOfMolesH2O() {
        return _volume * getConcentrationH2O();
    }
    
    public interface LiquidListener {
        public void stateChanged();
    }
    
    public void addLiquidListener( LiquidListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeLiquidListener( LiquidListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyStateChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (LiquidListener) i.next() ).stateChanged();
        }
    }
    
    public void clockTicked( ClockEvent clockEvent ) {
        if ( _draining ) {
            _volume -= _drainingRate;
            if ( _volume <= 0 ) {
                _volume = 0;
                _draining = false;
            }
            notifyStateChanged();
        }
        else if ( _filling ) {
            _volume += _fillRate;
            if ( _volume >= _fillVolume ) {
                _volume = _fillVolume;
                _filling = false;
            }
            notifyStateChanged();
        }
    }
}
