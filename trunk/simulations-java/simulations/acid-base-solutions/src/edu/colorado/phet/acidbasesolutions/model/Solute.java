package edu.colorado.phet.acidbasesolutions.model;

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.acidbasesolutions.ABSConstants;


public abstract class Solute extends Molecule {
    
    public interface ICustomSolute {
        public void setStrength( double strength );
    }
    
    private double initialConcentration;
    private double strength;
    private final ArrayList<SoluteListener> listeners;
    
    protected Solute( String name, String symbol, double strength ) {
        super( name, symbol );
        this.initialConcentration = ABSConstants.CONCENTRATION_RANGE.getMin();
        if ( !isValidStrength( strength ) ) {
            throw new IllegalArgumentException( "strength is invalid: " + strength );
        }
        this.strength = strength;
        listeners = new ArrayList<SoluteListener>();
    }
    
    protected abstract boolean isValidStrength( double strength );
    
    protected void setStrength( double strength ) {
        if ( !isValidStrength( strength ) ) {
            throw new IllegalArgumentException( "strength is invalid: " + strength );
        }
        if ( strength != this.strength ) {
            this.strength = strength;
            notifyStrengthChanged();
        }
    }
    
    public double getStrength() {
        return strength;
    }
    
    // c
    public void setInitialConcentration( double initialConcentration ) {
        if ( initialConcentration != this.initialConcentration ) {
            this.initialConcentration = initialConcentration;
            notifyConcentrationChanged();
        }
    }
    
    // c
    public double getInitialConcentration() {
        return initialConcentration;
    }
    
    public interface SoluteListener {
        public void strengthChanged();
        public void concentrationChanged();
    }
    
    public void addSoluteListener( SoluteListener listener ) {
        listeners.add( listener );
    }
    
    public void removeSoluteListener( SoluteListener listener ) {
        listeners.remove( listener );
    }
    
    private void notifyStrengthChanged() {
        Iterator<SoluteListener> i = listeners.iterator();
        while ( i.hasNext() ) {
            i.next().strengthChanged();
        }
    }
    
    private void notifyConcentrationChanged() {
        Iterator<SoluteListener> i = listeners.iterator();
        while ( i.hasNext() ) {
            i.next().concentrationChanged();
        }
    }
}
