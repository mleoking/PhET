/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import java.util.EventListener;

import javax.swing.event.EventListenerList;

/**
 * A solute is a substance that is dissolved in a solution.
 * It's conjugate (or product) is the substance that is produced as the result of dissolving.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Solute {
    
    public interface ICustomSolute {
        public void setStrength( double strength );
    }
    
    private final Molecule molecule;
    private final Molecule product;
    private double strength;
    private final EventListenerList listeners;
    
    public Solute( Molecule molecule, Molecule product, double strength ) {
        this.molecule = molecule;
        this.product = product;
        this.strength = strength;
        listeners = new EventListenerList();
    }
    
    public Molecule getMolecule() {
        return molecule;
    }
    
    public Molecule getProduct() {
        return product;
    }
    
    /**
     * Strength of real solutes is immutable.
     * This method is provided for use by "custom" solutes that allow strength to be varied.
     * @param strength
     */
    protected void setStrength( double strength ) {
        if ( strength != this.strength ) {
            this.strength = strength;
            fireStrengthChanged();
        }
    }
    
    public double getStrength() {
        return strength;
    }
    
    public abstract String getStrengthSymbol();
    
    public interface SoluteListener extends EventListener {
        public void strengthChanged();
    }
    
    public void addSoluteListener( SoluteListener listener ) {
        listeners.add( SoluteListener.class, listener );
    }
    
    public void removeSoluteListener( SoluteListener listener ) {
        listeners.remove( SoluteListener.class, listener );
    }
    
    private void fireStrengthChanged() {
        for ( SoluteListener listener : listeners.getListeners( SoluteListener.class ) ) {
            listener.strengthChanged();
        }
    }
}
