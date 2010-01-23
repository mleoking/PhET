/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.model;

import java.util.ArrayList;

/**
 * A reactant is a substance that is initially involved in a chemical reaction.
 * This class is final and cannot be extended in order to support newInstance.
 * This approach is an alternative to the evils of implementing clone.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public final /* yes, final, see javadoc! */ class Reactant extends Substance {
    
    private int leftovers;
    private final ArrayList<ReactantChangeListener> listeners;
    
    public Reactant( int coefficient, Molecule molecule ) {
        this( coefficient, molecule, 0 /* quantity */ );
    }

    public Reactant( int coefficient, Molecule molecule, int quantity ) {
        super( coefficient, molecule, quantity );
        this.leftovers = 0;
        listeners = new ArrayList<ReactantChangeListener>();
    }
    
    /**
     * Copy constructor.
     * @param reactant
     */
    public Reactant( Reactant reactant ) {
        this( reactant.getCoefficient(), reactant.getMolecule(), reactant.getQuantity() );
        setLeftovers( reactant.getLeftovers() );
        // listeners are not copied.
    }
    
    /**
     * Factory method to create a new instance.
     * @param reactant
     * @return
     */
    public static Reactant newInstance( Reactant reactant ) {
        return new Reactant( reactant );
    }
    
    /**
     * @param reactant
     * @return
     */
    public boolean equals( Object obj ) {
        if ( ! ( obj instanceof Reactant ) ) { return false; }
        Reactant reactant = (Reactant) obj;
        if ( !super.equals( reactant ) ) { return false; }
        if ( getLeftovers() != reactant.getLeftovers() ) { return false; }
        // listeners do not have to be the same.
        return true;
    }
    
    public void setLeftovers( int leftovers ) {
        if ( leftovers < 0 ) {
            throw new IllegalArgumentException( "leftover must be >= 0: " + leftovers );
        }
        if ( leftovers != this.leftovers ) {
            this.leftovers = leftovers;
            fireLeftoverChanged();
        }
    }
    
    public int getLeftovers() {
        return leftovers;
    }
    
    public interface ReactantChangeListener extends SubstanceChangeListener {
        public void leftoversChanged();
    }
    
    public static class ReactantChangeAdapter extends SubstanceChangeAdapter implements ReactantChangeListener {
        public void leftoversChanged() {}
    }
    
    public void addReactantChangeListener( ReactantChangeListener listener ) {
        super.addSubstanceChangeListener( listener );
        listeners.add( listener );
    }
    
    public void removeReactantChangeListener( ReactantChangeListener listener ) {
        super.removeSubstanceChangeListener( listener );
        listeners.remove( listener );
    }
    
    private void fireLeftoverChanged() {
        ArrayList<ReactantChangeListener> listenersCopy = new ArrayList<ReactantChangeListener>( listeners ); // avoid ConcurrentModificationException
        for ( ReactantChangeListener listener : listenersCopy ) {
            listener.leftoversChanged();
        }
    }
}
