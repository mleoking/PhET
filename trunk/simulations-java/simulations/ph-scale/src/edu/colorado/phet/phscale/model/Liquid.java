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
    private Double _pH;
    private double _volume; // liters
    private boolean _draining;
    private double _drainRate;
    private boolean _filling;
    private double _fillRate;
    private LiquidDescriptor _fillLiquidDescriptor;
    private double _fillVolume;
    
    public Liquid( LiquidDescriptor liquidDescriptor ) {
        _pH = null;
        _volume = 0;
        _listeners = new ArrayList();
        _filling = false;
        _fillRate = 0;
        _fillLiquidDescriptor = null;
        _fillVolume = MAX_VOLUME;
        _draining = false;
        _drainRate = 0;
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
        drainImmediately();
        notifyStateChanged();
    }
    
    private void increaseVolume( double newVolume, LiquidDescriptor addedLiquid ) {
        assert ( newVolume > _volume );
        assert ( newVolume >= 0 );
        assert ( newVolume <= MAX_VOLUME );
        if ( newVolume != _volume ) {
            double dv = newVolume - _volume;
            // pH = -log[(10**-pH*Vo + 10**-pH'*Va)/(Vo+Va)]
            if ( _pH == null ) {
                _pH = new Double( addedLiquid.getPH() );
            }
            else {
                double newPH = -Math.log( ( ( Math.pow( 10, -_pH.doubleValue() ) * _volume ) + ( Math.pow( 10, -addedLiquid.getPH() ) * dv ) ) / ( _volume + dv ) );
                _pH = new Double( newPH );
            }
            System.out.println( "Liquid.increaseVolume _pH=" + _pH.doubleValue() + " dv=" + dv );//XXX
            _volume = newVolume;
            notifyStateChanged();
        }
    }
    
    private void decreaseVolume( double newVolume ) {
        assert ( newVolume < _volume );
        assert ( newVolume >= 0 );
        assert ( newVolume <= MAX_VOLUME );
        _volume = newVolume;
        if ( _volume == 0 ) {
            _pH = null;
        }
        notifyStateChanged();
    }
    
    public boolean isEmpty() {
        return _volume == 0;
    }
    
    public boolean isFull() {
        return _volume == MAX_VOLUME;
    }
    
    public void startDraining( double drainRate ) {
        if ( !_draining && !isEmpty() ) {
            _filling = false;
            _draining = true;
            _drainRate = drainRate;
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
        _pH = new Double( pH );
        notifyStateChanged();
    }
    
    public Double getPH() {
        return _pH;
    }
    
    public double getVolume() {
        return _volume;
    }
    
    public Color getColor() {
        return _liquidDescriptor.getColor(); //XXX need to dilute this
    }
    
    public void setConcentrationH3O( double c ) {
        _pH = new Double( -Math.log( c ) );
        notifyStateChanged();
    }
    
    public double getConcentrationH3O() {
        double c = 0;
        if ( _pH != null ) {
            c = Math.pow( 10, -_pH.doubleValue() );
        }
        return c;
    }
  
    public void setConcentrationOH( double c ) {
        _pH = new Double( 14 - ( -Math.log( c ) ) );
        notifyStateChanged();
    }
    
    public double getConcentrationOH() {
        double c = 0;
        if ( _pH != null ) {
            c = Math.pow( 10, -( 14 - _pH.doubleValue()) );
        }
        return c;
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
        _pH = new Double( -Math.log( m / _volume ) );
        notifyStateChanged();
    }
    
    public double getNumberOfMolesH3O() {
        return _volume * getConcentrationH3O();
    }
    
    public void setNumberOfMolesOH( double m ) {
        _pH = new Double( 14 - ( -Math.log( m / _volume ) ) );
        notifyStateChanged();
    }
    
    public double getNumberOfMolesOH() {
        return _volume * getConcentrationOH();
    }
    
    public void setNumberOfMolesH2O( double m ) {
        //XXX how to do this?...
    }
    
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
        if ( _filling ) {
            stepFill();
        }
        else if ( _draining ) {
            stepDrain();
        }
    }
    
    private void stepFill() {
        double newVolume = _volume + _fillRate;
        if ( newVolume >= _fillVolume ) {
            newVolume = _fillVolume;
            _filling = false;
        }
        increaseVolume( newVolume, _fillLiquidDescriptor );
    }
    
    private void stepDrain() {
        double newVolume = _volume - _drainRate;
        if ( newVolume <= 0 ) {
            newVolume = 0;
            _draining = false;
        }
        decreaseVolume( newVolume );
    }
}
