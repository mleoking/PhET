/* Copyright 2008, University of Colorado */ 

package edu.colorado.phet.phscale.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;

/**
 * Liquid is the model of the liquid. The liquid can be "filled" with a combination
 * of liquids, or drained.  Filling and draining are subject to maximum and minimum
 * volume constraints.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Liquid extends ClockAdapter {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double MAX_VOLUME = 1.25;
    private static final double AVOGADROS_NUMBER = 6.023E23;
    private static final double H2O_CONCENTRATION = 55; // moles/L
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final ArrayList _listeners;
    
    private LiquidDescriptor _liquidDescriptor;
    private Double _pH;
    private double _volume; // L

    private boolean _filling;
    private double _fillRate; // L per clock tick
    private double _fillVolume; // L
    private LiquidDescriptor _fillLiquidDescriptor;
    
    private boolean _draining;
    private double _drainRate; // L per clock tick
    private double _drainVolume; // L
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Liquid( LiquidDescriptor liquidDescriptor ) {
        
        _listeners = new ArrayList();
        _pH = null;
        _volume = 0;

        _filling = false;
        _fillRate = 0;
        _fillVolume = MAX_VOLUME;
        _fillLiquidDescriptor = null;
        
        _draining = false;
        _drainRate = 0;
        _drainVolume = 0;
        
        setLiquidDescriptor( liquidDescriptor );
    }
    
    //----------------------------------------------------------------------------
    // Properties
    //----------------------------------------------------------------------------
    
    public void setLiquidDescriptor( LiquidDescriptor liquidDescriptor ) {
        _liquidDescriptor = liquidDescriptor;
        drainImmediately();
        notifyStateChanged();
    }
    
    public LiquidDescriptor getLiquidDescriptor() {
        return _liquidDescriptor;
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
    
    public double getMaxVolume() {
        return MAX_VOLUME;
    }
    
    public boolean isEmpty() {
        return _volume == 0;
    }
    
    public boolean isFull() {
        return _volume == MAX_VOLUME;
    }
    
    public Color getColor() {
        return _liquidDescriptor.getColor(); //XXX need to dilute this
    }
    
    //----------------------------------------------------------------------------
    // Filling & draining
    //----------------------------------------------------------------------------
    
    /**
     * Starts filling with a specified liquid.
     * Filling will continue until full.
     * 
     * @param fillRate how much to add each time the clock ticks (L)
     * @param fillLiquidDecriptor the liquid to add
     */
    public void startFilling( double fillRate, LiquidDescriptor fillLiquidDescriptor ) {
        startFilling( fillRate, fillLiquidDescriptor, MAX_VOLUME );
    }
    
    /**
     * Starts filling with a specified liquid.
     * Filling will continue until the specified volume is reached.
     * 
     * @param fillRate how much to add each time the clock ticks (L)
     * @param fillLiquidDecriptor the liquid to add
     * @param fillVolume stop filling when we reach this volume (L)
     */
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
    
    /**
     * Stops filling.
     */
    public void stopFilling() {
        if ( _filling ) {
            _filling = false;
            notifyStateChanged();
        }
    }
    
    /**
     * Are we in the process of filling?
     * 
     * @return true or false
     */
    public boolean isFilling() {
        return _filling;
    }
    
    /**
     * Immediately drains to empty.
     */
    public void drainImmediately() {
        if ( !isEmpty() ) {
            _volume = 0;
            notifyStateChanged();
        }
    }
    
    /**
     * Starts draining.
     * Draining will continue until empty.
     * 
     * @param drainRate how much to add each time the clock ticks (liters)
     */
    public void startDraining( double drainRate ) {
        startDraining( drainRate, 0 );
    }
    
    /**
     * Starts draining.
     * Draining will continue until the specified volume is reached.
     * 
     * @param drainRate how much to add each time the clock ticks (liters)
     * @param drainVolume stop draining when we reach this volume (liters)
     */
    public void startDraining( double drainRate, double drainVolume ) {
        if ( !_draining && drainVolume < _volume ) {
            _filling = false;
            _draining = true;
            _drainRate = drainRate;
            _drainVolume = drainVolume;
            notifyStateChanged();
        }
    }
    
    /**
     * Stops draining.
     */
    public void stopDraining() {
        if ( _draining ) {
            _draining = false;
            notifyStateChanged();
        }
    }
    
    /**
     * Are we in the process of draining?
     * 
     * @return true or false
     */
    public boolean isDraining() {
        return _draining;
    }
    
    //----------------------------------------------------------------------------
    //  Concentrations (moles/L)
    //----------------------------------------------------------------------------
    
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
    
    //----------------------------------------------------------------------------
    // Number of molecules
    //----------------------------------------------------------------------------
    
    public double getMoleculesH3O() {
        return getConcentrationH3O() * AVOGADROS_NUMBER * _volume;
    }

    public double getMoleculesOH() {
        return getConcentrationOH() * AVOGADROS_NUMBER * _volume;
    }

    public double getMoleculesH2O() {
        return getConcentrationH2O() * AVOGADROS_NUMBER * _volume;
    }
    
    //----------------------------------------------------------------------------
    // Number of moles
    //----------------------------------------------------------------------------
    
    public void setMolesH3O( double m ) {
        _pH = new Double( -Math.log( m / _volume ) );
        notifyStateChanged();
    }
    
    public double getMolesH3O() {
        return _volume * getConcentrationH3O();
    }
    
    public void setMolesOH( double m ) {
        _pH = new Double( 14 - ( -Math.log( m / _volume ) ) );
        notifyStateChanged();
    }
    
    public double getMolesOH() {
        return _volume * getConcentrationOH();
    }
    
    public void setMolesH2O( double m ) {
        //XXX how to do this?...
    }
    
    public double getMolesH2O() {
        return _volume * getConcentrationH2O();
    }
    
    //----------------------------------------------------------------------------
    // Private
    //----------------------------------------------------------------------------
    
    /*
     * Gets the pH of two combined volumes of liquid.
     * 
     * @param pH1
     * @param volume1 (L)
     * @param pH2 
     * @param volume2 (L)
     */
    private static final double pHCombined( double pH1, double volume1, double pH2, double volume2 ) {
        return -Math.log( ( Math.pow( 10, -pH1 ) * volume1 + Math.pow( 10, -pH2 ) * volume2 ) / ( volume1 + volume2 ) );
    }
    
    /*
     * Increases the volume by adding a liquid.
     * This changes the volume and the pH.
     * 
     * @param newVolume (L)
     * @param addedLiquid
     */
    private void increaseVolume( double newVolume, LiquidDescriptor addedLiquid ) {
        assert ( newVolume > _volume );
        assert ( newVolume >= 0 );
        assert ( newVolume <= MAX_VOLUME );
        if ( newVolume != _volume ) {
            if ( _volume == 0 ) {
                _pH = new Double( addedLiquid.getPH() );
            }
            else {
                double addedVolume = newVolume - _volume;
                double newPH = pHCombined( _pH.doubleValue(), _volume, addedLiquid.getPH(), addedVolume );
                _pH = new Double( newPH );
            }
            _volume = newVolume;
            notifyStateChanged();
        }
    }
    
    /*
     * Decreases the volume by removing some liquid.
     * This changes the volume, but pH is the same.
     * 
     * @param newVolume (L)
     */
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
    
    //----------------------------------------------------------------------------
    // ClockAdapter overrides
    //----------------------------------------------------------------------------
    
    /**
     * Fills or drains some liquid when the clock ticks.
     */
    public void clockTicked( ClockEvent clockEvent ) {
        if ( _filling ) {
            stepFill();
        }
        else if ( _draining ) {
            stepDrain();
        }
    }
    
    /*
     * Fills by one step, stops filling when the desired volume is reached.
     */
    private void stepFill() {
        double newVolume = _volume + _fillRate;
        if ( newVolume >= _fillVolume ) {
            newVolume = _fillVolume;
            _filling = false;
        }
        increaseVolume( newVolume, _fillLiquidDescriptor );
    }
    
    /*
     * Drains by one step, stops draining when the desired volume is reached.
     */
    private void stepDrain() {
        double newVolume = _volume - _drainRate;
        if ( newVolume <= _drainVolume ) {
            newVolume = _drainVolume;
            _draining = false;
        }
        decreaseVolume( newVolume );
    }
    
    //----------------------------------------------------------------------------
    // Listener interface
    //----------------------------------------------------------------------------
    
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
    
    //----------------------------------------------------------------------------
    // Tests
    //----------------------------------------------------------------------------
    
    public static void main( String[] args ) {
        // adding some more of the same liquid should result in the same pH
        double pH = 2.4;
        double newPH = pHCombined( pH, 0.01, pH, 0.01 );
        System.out.println( "pH=" + pH + " newPH=" + newPH );
    }
}
