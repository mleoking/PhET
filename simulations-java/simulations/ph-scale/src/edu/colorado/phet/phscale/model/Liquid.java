/* Copyright 2008, University of Colorado */ 

package edu.colorado.phet.phscale.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.phscale.model.LiquidDescriptor.CustomLiquidDescriptor;
import edu.colorado.phet.phscale.model.LiquidDescriptor.LiquidDescriptorAdapter;
import edu.colorado.phet.phscale.model.LiquidDescriptor.LiquidDescriptorListener;

/**
 * Liquid is the model of the liquid in the beaker. 
 * The liquid can be "filled" with a combination of liquids, or drained.
 * Filling and draining are subject to maximum and minimum volume constraints.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Liquid extends ClockAdapter {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final double SLOW_FILL_RATE = 0.005; // liters per clock tick
    public static final double FAST_FILL_RATE = 0.03; // liters per clock tick
    public static final double FAST_FILL_VOLUME = 1.0; // liters
    
    private static final double MAX_VOLUME = 1.125;
    private static final double AVOGADROS_NUMBER = 6.023E23;
    private static final double H2O_CONCENTRATION = 55; // moles/L
    
    private static final LiquidDescriptor WATER = LiquidDescriptor.getWater();
    private static final CustomLiquidDescriptor CUSTOM_LIQUID = LiquidDescriptor.getCustom();
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final ArrayList _listeners;
    
    private final IntegerRange _pHRange;
    
    private LiquidDescriptor _liquidDescriptor;
    private final LiquidDescriptorListener _liquidDescriptorListener;
    
    private Double _pH;
    private double _volume; // L
    private double _waterVolume; // L

    private boolean _isFillingLiquid;
    private double _fillLiquidRate; // L per clock tick
    private double _fillLiquidVolume; // L
    
    private boolean _isFillingWater;
    private double _fillWaterRate; // L per clock tick
    private double _fillWaterVolume; // L
    
    private boolean _isDraining;
    private double _drainRate; // L per clock tick
    private double _drainVolume; // L
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Liquid( IntegerRange pHRange, LiquidDescriptor liquidDescriptor ) {
        
        _pHRange = new IntegerRange( pHRange );
        
        _listeners = new ArrayList();
        _liquidDescriptorListener = new LiquidDescriptorAdapter() {
            public void colorChanged( Color color ) {
                notifyStateChanged();
            }
        };
        
        // changing the color of water will change the color of the liquid
        WATER.addLiquidDescriptorListener( new LiquidDescriptorAdapter() {
            public void colorChanged( Color color ) {
                if ( _waterVolume > 0 ) {
                    notifyStateChanged();
                }
            }
        } );
        
        _pH = null;
        _volume = 0;
        _waterVolume = 0;

        _isFillingLiquid = false;
        _fillLiquidRate = 0;
        _fillLiquidVolume = MAX_VOLUME;

        _isFillingWater = false;
        _fillWaterRate = 0;
        _fillWaterVolume = MAX_VOLUME;
        
        _isDraining = false;
        _drainRate = 0;
        _drainVolume = 0;
        
        setLiquidDescriptor( liquidDescriptor );
    }
    
    //----------------------------------------------------------------------------
    // Properties
    //----------------------------------------------------------------------------
    
    public IntegerRange getPHRange() {
        return _pHRange;
    }
    
    public int getMaxPH() {
        return _pHRange.getMax();
    }
    
    public int getMinPH() {
        return _pHRange.getMin();
    }
    
    public void setLiquidDescriptor( LiquidDescriptor liquidDescriptor ) {
        
        if ( _liquidDescriptor != null ) {
            _liquidDescriptor.removeLiquidDescriptorListener( _liquidDescriptorListener );
        }
        
        _liquidDescriptor = liquidDescriptor;
        _liquidDescriptor.addLiquidDescriptorListener( _liquidDescriptorListener );
        
        drainImmediately();
        startFillingLiquid( FAST_FILL_RATE, FAST_FILL_VOLUME );
        notifyStateChanged();
    }
    
    public LiquidDescriptor getLiquidDescriptor() {
        return _liquidDescriptor;
    }
    
    /**
     * Sets the pH.
     * If the pH is out of range, it is silently clamped to the range.
     * NOTE: This clamping behavior is essential to other parts of the sim 
     * (eg, dragging bars in the bar graph).
     * 
     * @param pH
     */
    public void setPH( final double pH ) {
        
        // clamp to the pH range
        double clampedPH = pH;
        if ( pH < _pHRange.getMin() ) {
            clampedPH = _pHRange.getMin();
        }
        else if ( pH > _pHRange.getMax() ) {
            clampedPH = _pHRange.getMax();
        }
        
        // adjust pH of Custom liquid
        if ( _liquidDescriptor.equals( CUSTOM_LIQUID ) ) {
            CUSTOM_LIQUID.setPH( clampedPH );
        }
        
        // adjust pH of this liquid
        if ( _pH != null && pH != _pH.doubleValue() ) {
            _pH = new Double( clampedPH );
            notifyStateChanged();
        }
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
        Color color = null;
        if ( _volume > 0 ) {
            if ( _volume == _waterVolume ) {
                // all water
                color = WATER.getColor();
            }
            else if ( _waterVolume == 0 ) {
                // no water
                color = _liquidDescriptor.getColor();
            }
            else {
                // diluted with water
                double liquidVolume = _volume - _waterVolume;
                int alpha = (int) ( _liquidDescriptor.getColor().getAlpha() * ( liquidVolume / _volume ) );
                color = ColorUtils.createColor( _liquidDescriptor.getColor(), alpha );
            }
        }
        return color;
    }
    
    //----------------------------------------------------------------------------
    // Filling & draining
    //----------------------------------------------------------------------------
    
    /**
     * Starts filling with the liquid specified via setLiquidDescriptor.
     * Filling will continue until full.
     * 
     * @param fillRate how much to add each time the clock ticks (L)
     */
    public void startFillingLiquid( double fillRate ) {
        startFillingLiquid( fillRate, MAX_VOLUME );
    }
    
    /**
     * Starts filling with the liquid specified via setLiquidDescriptor.
     * Filling will continue until the specified volume is reached.
     * 
     * @param fillRate how much to add each time the clock ticks (L)
     * @param fillVolume stop filling when we reach this volume (L)
     */
    public void startFillingLiquid( double fillRate, double fillVolume ) {
        if ( !_isFillingLiquid && !isFull() && fillVolume > _volume ) {
            _isFillingLiquid = true;
            _fillLiquidRate = fillRate;
            _fillLiquidVolume = fillVolume;
            notifyStateChanged();
        }
    }
    
    /**
     * Stops filling liquid.
     */
    public void stopFillingLiquid() {
        if ( _isFillingLiquid ) {
            _isFillingLiquid = false;
            notifyStateChanged();
        }
    }
    
    /**
     * Are we in the process of filling liquid?
     * 
     * @return true or false
     */
    public boolean isFillingLiquid() {
        return _isFillingLiquid;
    }
    
    /**
     * Starts filling with water.
     * Filling will continue until full.
     * 
     * @param fillRate how much to add each time the clock ticks (L)
     */
    public void startFillingWater( double fillRate ) {
        startFillingWater( fillRate, MAX_VOLUME );
    }
    
    /**
     * Starts filling with water.
     * Filling will continue until the specified volume is reached.
     * 
     * @param fillRate how much to add each time the clock ticks (L)
     * @param fillVolume stop filling when we reach this volume (L)
     */
    public void startFillingWater( double fillRate, double fillVolume ) {
        if ( !_isFillingWater && !isFull() && fillVolume > _volume ) {
            _isFillingWater = true;
            _fillWaterRate = fillRate;
            _fillWaterVolume = fillVolume;
            notifyStateChanged();
        }
    }
    
    /**
     * Stops filling.
     */
    public void stopFillingWater() {
        if ( _isFillingWater ) {
            _isFillingWater = false;
            notifyStateChanged();
        }
    }
    
    /**
     * Are we in the process of filling water?
     * 
     * @return true or false
     */
    public boolean isFillingWater() {
        return _isFillingWater;
    }
    
    /**
     * Immediately drains to empty.
     */
    public void drainImmediately() {
        if ( !isEmpty() ) {
            _volume = 0;
            _waterVolume = 0;
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
        if ( !_isDraining && drainVolume < _volume ) {
            _isFillingLiquid = false;
            _isDraining = true;
            _drainRate = drainRate;
            _drainVolume = drainVolume;
            notifyStateChanged();
        }
    }
    
    /**
     * Stops draining.
     */
    public void stopDraining() {
        if ( _isDraining ) {
            _isDraining = false;
            notifyStateChanged();
        }
    }
    
    /**
     * Are we in the process of draining?
     * 
     * @return true or false
     */
    public boolean isDraining() {
        return _isDraining;
    }
    
    //----------------------------------------------------------------------------
    //  Concentrations (moles/L)
    //----------------------------------------------------------------------------
    
    public void setConcentrationH3O( double c ) {
        setPH( -MathUtil.log10( c ) );
    }
    
    public double getConcentrationH3O() {
        double c = 0;
        if ( _pH != null ) {
            c = getConcentrationH3O( _pH.doubleValue() );
        }
        return c;
    }
    
    public static double getConcentrationH3O( double pH ) {
        return Math.pow( 10, -pH );
    }
  
    public void setConcentrationOH( double c ) {
        setPH( 14 - ( -MathUtil.log10( c ) ) );
    }
    
    public double getConcentrationOH() {
        double c = 0;
        if ( _pH != null ) {
            c = getConcentrationOH( _pH.doubleValue() );
        }
        return c;
    }
    
    public static double getConcentrationOH( double pH ) {
        return Math.pow( 10, -( 14 - pH ) );
    }
    
    public double getConcentrationH2O() {
        return ( isEmpty() ? 0 : H2O_CONCENTRATION );
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
        setPH( -MathUtil.log10( m / _volume ) );
    }
    
    public double getMolesH3O() {
        return _volume * getConcentrationH3O();
    }
    
    public void setMolesOH( double m ) {
        setPH( 14 - ( -MathUtil.log10( m / _volume ) ) );
    }
    
    public double getMolesOH() {
        return _volume * getConcentrationOH();
    }
    
    public double getMolesH2O() {
        return _volume * getConcentrationH2O();
    }
    
    //----------------------------------------------------------------------------
    // Private
    //----------------------------------------------------------------------------
    
    /*
     * Gets the pH of two combined volumes of liquid.
     * Combining acids and bases is not supported by this model.
     * 
     * @param pH1
     * @param volume1 (L)
     * @param pH2 
     * @param volume2 (L)
     * @throws UnsupportedOperationException if you try to combine an acid and base
     */
    private static final double pHCombined( double pH1, double volume1, double pH2, double volume2 ) {
        if ( ( pH1 < 7 && pH2 > 7 ) || ( pH1 > 7 && pH2 < 7 ) ) {
            throw new UnsupportedOperationException( "combining acids and bases is not supported" );
        }
        double newPH = 0;
        if ( pH1 < 7 ) {
            newPH = -MathUtil.log10( ( Math.pow( 10, -pH1 ) * volume1 + Math.pow( 10, -pH2 ) * volume2 ) / ( volume1 + volume2 ) );
        }
        else {
            newPH = 14 + MathUtil.log10( ( Math.pow( 10, pH1 - 14 ) * volume1 + Math.pow( 10, pH2 - 14 ) * volume2 ) / ( volume1 + volume2 ) );
        }
        return newPH;
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
            double addedVolume = newVolume - _volume;
            if ( _volume == 0 ) {
                _pH = new Double( addedLiquid.getPH() );
            }
            else {
                double newPH = pHCombined( _pH.doubleValue(), _volume, addedLiquid.getPH(), addedVolume );
                _pH = new Double( newPH );
            }
            _volume = newVolume;
            if ( addedLiquid.equals( WATER ) ) {
                _waterVolume += addedVolume;
            }
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
        if ( _waterVolume > 0 ) {
            if ( _waterVolume == 0 ) {
                // no water
                _waterVolume = 0;
            }
            else if ( _waterVolume == _volume ) {
                // all water
                _waterVolume = newVolume;
            }
            else {
                // diluted with water
                if ( newVolume == 0 ) {
                    _waterVolume = 0;
                }
                else {
                    _waterVolume *= ( newVolume / _volume );
                }
            }
        }
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
        if ( _isFillingLiquid ) {
            stepFillLiquid();
        }
        if ( _isFillingWater ) {
            stepFillWater();
        }
        if ( _isDraining ) {
            stepDrain();
        }
    }
    
    /*
     * Fills liquid by one step, stops filling when the desired volume is reached.
     */
    private void stepFillLiquid() {
        double newVolume = _volume + _fillLiquidRate;
        if ( newVolume >= _fillLiquidVolume ) {
            newVolume = _fillLiquidVolume;
            _isFillingLiquid = false;
        }
        increaseVolume( newVolume, _liquidDescriptor );
    }
    
    /*
     * Fills water by one step, stops filling when the desired volume is reached.
     */
    private void stepFillWater() {
        double newVolume = _volume + _fillWaterRate;
        if ( newVolume >= _fillWaterVolume ) {
            newVolume = _fillWaterVolume;
            _isFillingWater = false;
        }
        increaseVolume( newVolume, WATER );
    }
    
    /*
     * Drains by one step, stops draining when the desired volume is reached.
     */
    private void stepDrain() {
        double newVolume = _volume - _drainRate;
        if ( newVolume <= _drainVolume ) {
            newVolume = _drainVolume;
            _isDraining = false;
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
}
