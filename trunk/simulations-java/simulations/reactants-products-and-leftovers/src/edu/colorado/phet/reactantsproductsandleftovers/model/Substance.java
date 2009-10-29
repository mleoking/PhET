package edu.colorado.phet.reactantsproductsandleftovers.model;

import java.util.ArrayList;

import edu.umd.cs.piccolo.PNode;

/**
 * A substance is a participant in a chemical reaction.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Substance {
    
    private final String name;
    private final PNode node;
    private int coefficient;
    private int quantity;
    private final ArrayList<SubstanceChangeListener> listeners;

    public Substance( String name, PNode node, int coefficient, int quantity ) {
        this.name = name;
        this.node = node;
        this.coefficient = coefficient;
        this.quantity = quantity;
        listeners = new ArrayList<SubstanceChangeListener>();
    }
    
    public String getName() {
        return name;
    }
    
    public PNode getNode() {
        return node;
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
    
    protected interface SubstanceChangeListener {
        public void coefficientChanged();
        public void quantityChanged();
    }
    
    protected static class SubstanceChangeAdapter {
        public void coefficientChanged() {}
        public void quantityChanged() {}
    }
    
    protected void addSubstanceChangeListener( SubstanceChangeListener listener ) {
        listeners.add( listener );
    }
    
    protected void removeSubstanceChangeListener( SubstanceChangeListener listener ) {
        listeners.remove( listener );
    }
    
    private void fireCoefficientChanged() {
        for ( SubstanceChangeListener listener : listeners ) {
            listener.coefficientChanged();
        }
    }
    
    private void fireQuantityChanged() {
        for ( SubstanceChangeListener listener : listeners ) {
            listener.quantityChanged();
        }
    }
}
