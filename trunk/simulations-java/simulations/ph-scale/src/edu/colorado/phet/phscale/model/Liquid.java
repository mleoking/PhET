package edu.colorado.phet.phscale.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;


public class Liquid {
    
    private static final double AVOGADROS_NUMBER = 6.023E23;
    private static final double H2O_CONCENTRATION = 55; // moles/L
    
    private final ArrayList _listeners;
    
    private LiquidDescriptor _liquidDescriptor;
    private double _pH;
    private double _volume; // liters
    
    public Liquid( LiquidDescriptor liquidDescriptor, double volume ) {
        assert( volume >= 0 );
        _listeners = new ArrayList();
        setLiquidDescriptor( liquidDescriptor, volume );
    }
    
    public LiquidDescriptor geLiquidDescriptor() {
        return _liquidDescriptor;
    }
    
    public void setLiquidDescriptor( LiquidDescriptor baseLiquid, double volume ) {
        _liquidDescriptor = baseLiquid;
        _pH = baseLiquid.getPH();
        _volume = volume;
        notifyStateChanged();
    }
    
    public void add( LiquidDescriptor liquid, double volume ) {
        assert( volume >= 0 );
        _pH = -Math.log( ( Math.pow( 10, -_pH * _volume ) + Math.pow( 10, -liquid.getPH() * volume ) ) / ( _volume + volume ) );
        _volume += volume;
        notifyStateChanged();
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
    
    public void setNumberOfMolesH2O( double m ) {
        double volumeChange = ( m / getConcentrationH2O() ) - _volume;
        add( LiquidDescriptor.WATER, volumeChange );
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
}
