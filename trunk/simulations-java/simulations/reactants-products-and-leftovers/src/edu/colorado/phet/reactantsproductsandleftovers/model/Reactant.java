// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.model;

import javax.swing.event.EventListenerList;

/**
 * A reactant is a substance that is initially involved in a chemical reaction.
 * <p>
 * This class is final and cannot be extended in order to support newInstance, which is needed 
 * for creating a Game "guess" for a specific reaction.
 * This approach is an alternative to the evils of implementing clone.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public final /* yes, final, see javadoc! */ class Reactant extends Substance {
    
    private int leftovers;
    private final EventListenerList listeners;
    
    public Reactant( int coefficient, Molecule molecule ) {
        this( coefficient, molecule, 0 /* quantity */ );
    }

    public Reactant( int coefficient, Molecule molecule, int quantity ) {
        super( coefficient, molecule, quantity );
        this.leftovers = 0;
        listeners = new EventListenerList();
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
        listeners.add( ReactantChangeListener.class, listener );
    }
    
    public void removeReactantChangeListener( ReactantChangeListener listener ) {
        super.removeSubstanceChangeListener( listener );
        listeners.remove( ReactantChangeListener.class, listener );
    }
    
    private void fireLeftoverChanged() {
        for ( ReactantChangeListener listener : listeners.getListeners( ReactantChangeListener.class ) ) {
            listener.leftoversChanged();
        }
    }
}
