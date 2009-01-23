package edu.colorado.phet.acidbasesolutions.model;

import java.util.ArrayList;


public abstract class AbstractChemicalCompound {
    
    private double _concentration;
    private double _strength;
    private final ArrayList _listeners;
    
    public AbstractChemicalCompound() {
        this( 0, 0 );
    }
    
    public AbstractChemicalCompound( double concentration, double strength ) {
        _concentration = concentration;
        _strength = strength;
        _listeners = new ArrayList();
    }

    protected void setConcentration( double concentration ) {
        if ( concentration != _concentration ) {
            _concentration = concentration;
        }
    }

    public double getConcentration() {
        return _concentration;
    }
    
    protected void setStrength( double strength ) {
        if ( strength != _strength ) {
            _strength = strength;
        }
    }
    
    public double getStrength() {
        return _strength;
    }
    
    public abstract double getPercentIonization();
    
    public interface ChemicalCompoundListener {
        public void strengthChanged( double strength );
        public void concentrationChanged( double concentration );
    }
    
    public static class ChemicalCompoundAdapter implements ChemicalCompoundListener {
        public void concentrationChanged( double concentration ) {}
        public void strengthChanged( double strength ) {}
    }
    
    public void addChemicalCompoundListener( ChemicalCompoundListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeChemicalCompoundListener( ChemicalCompoundListener listener ) {
        _listeners.remove( listener );
    }
    
}
