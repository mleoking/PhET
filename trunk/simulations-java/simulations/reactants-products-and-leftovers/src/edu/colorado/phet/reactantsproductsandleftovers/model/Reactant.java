package edu.colorado.phet.reactantsproductsandleftovers.model;

import java.util.ArrayList;

import edu.umd.cs.piccolo.PNode;

/**
 * A reactant is a substance that is initially involved in a chemical reaction.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Reactant extends Substance {
    
    private int leftovers;
    private final ArrayList<ReactantChangeListener> listeners;
    
    public Reactant( String name, PNode node, int coefficient, int quantity ) {
        super( name, node, coefficient, quantity );
        this.leftovers = 0;
        listeners = new ArrayList<ReactantChangeListener>();
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
