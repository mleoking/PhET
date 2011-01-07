// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.model;

import java.awt.Image;
import java.util.EventListener;

import javax.swing.event.EventListenerList;

/**
 * A substance is a participant in a chemical reaction.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Substance {
    
    private final Molecule molecule;
    private int coefficient;
    private int quantity;
    private final EventListenerList listeners;

    public Substance( int coefficient, Molecule molecule, int quantity ) {
        this.molecule = molecule;
        this.coefficient = coefficient;
        this.quantity = quantity;
        listeners = new EventListenerList();
    }
    
    /**
     * @param reactant
     * @return
     */
    public boolean equals( Object obj ) {
        if ( ! ( obj instanceof Substance ) ) { return false; }
        Substance substance = (Substance) obj;
        if ( !getName().equals( substance.getName() ) ) { return false; }
        if ( getCoefficient() != substance.getCoefficient() ) { return false; }
        if ( getQuantity() != substance.getQuantity() ) { return false; }
        // images do not have to be the same, so that we're not forced to share image instances. Image.equals uses referential equality.
        // listeners do not have to be the same.
        return true;
    }
    
    public Molecule getMolecule() {
        return molecule;
    }
    
    public String getName() {
        return molecule.getSymbol();
    }
    
    public Image getImage() {
        return molecule.getImage();
    }
    
    public void setImage( Image image ) {
        if ( image != getImage() ) {
            molecule.setImage( image );
            fireImageChanged();
        }
    }
    
    public void setCoefficient( int coefficient ) {
        if ( coefficient < 0 ) {
            throw new IllegalArgumentException( "coefficient must be >= 0: " + coefficient );
        }
        if ( coefficient != this.coefficient ) {
            this.coefficient = coefficient;
            fireCoefficientChanged();
        }
    }
    
    public int getCoefficient() {
        return coefficient;
    }
    
    public void setQuantity( int quantity ) {
        if ( quantity < 0 ) {
            throw new IllegalArgumentException( "quantity must be >= 0: " + quantity );
        }
        if ( quantity != this.quantity ) {
            this.quantity = quantity;
            fireQuantityChanged();
        }
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public interface SubstanceChangeListener extends EventListener {
        public void coefficientChanged();
        public void quantityChanged();
        public void imageChanged();
    }
    
    public static class SubstanceChangeAdapter implements SubstanceChangeListener {
        public void coefficientChanged() {}
        public void quantityChanged() {}
        public void imageChanged() {}
    }
    
    public void addSubstanceChangeListener( SubstanceChangeListener listener ) {
        listeners.add( SubstanceChangeListener.class, listener );
    }
    
    public void removeSubstanceChangeListener( SubstanceChangeListener listener ) {
        listeners.remove( SubstanceChangeListener.class, listener );
    }
    
    private void fireCoefficientChanged() {
        for ( SubstanceChangeListener listener : listeners.getListeners( SubstanceChangeListener.class ) ) {
            listener.coefficientChanged();
        }
    }
    
    private void fireQuantityChanged() {
        for ( SubstanceChangeListener listener : listeners.getListeners( SubstanceChangeListener.class ) ) {
            listener.quantityChanged();
        }
    }
    
    private void fireImageChanged() {
        for ( SubstanceChangeListener listener : listeners.getListeners( SubstanceChangeListener.class ) ) {
            listener.imageChanged();
        }
    }
}
