package edu.colorado.phet.reactantsproductsandleftovers.model;

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant.ReactantChangeAdapter;

/**
 * A chemical reaction is a process that leads to the transformation of one set of 
 * chemical substances (reactants) to another (products).  The reactants that do not
 * transform to products are referred to herein as "leftovers".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ChemicalReaction {
    
    private final String name;
    private final Reactant[] reactants;
    private final Product[] products;
    private final ArrayList<ChangeListener> listeners;
    
    public ChemicalReaction( String name, Reactant[] reactants, Product[] products ) {
        
        if ( reactants.length < 2 ) {
            throw new IllegalArgumentException( "a reaction requires at least 2 reactants" );
        }
        if ( products.length < 1 ) {
            throw new IllegalArgumentException( "a reaction requires at least 1 product" );
        }
        
        this.name = name;
        this.reactants = reactants;
        this.products = products;
        this.listeners = new ArrayList<ChangeListener>();
        
        ReactantChangeAdapter reactantChangeListener = new ReactantChangeAdapter() {

            public void coefficientChanged() {
                update();
            }

            public void quantityChanged() {
                update();
            }
        };
        for ( Reactant reactant : reactants ) {
            reactant.addReactantChangeListener( reactantChangeListener );
        }
        
        update();
    }
    
    /**
     * Gets the localized name of the reaction, suitable for display to the user.
     * @return
     */
    public String getName() {
        return name;
    }
    
    public Reactant[] getReactants() {
        return reactants;
    }
    
    public Reactant getReactant( int index ) {
        return reactants[index];
    }
    
    public int getNumberOfReactants() {
        return reactants.length;
    }
    
    public Product[] getProducts() {
        return products;
    }
    
    public Product getProduct( int index ) {
        return products[index];
    }
    
    public int getNumberOfProducts() {
        return products.length;
    }
    
    /**
     * Formula is a reaction if more than one coefficient is non-zero,
     * or if any coefficient is > 1.
     * @return
     */
    public boolean isReaction() {
        int greaterThanZero = 0;
        int greaterThanOne = 0;
        for ( Reactant reactant : reactants ) {
            if ( reactant.getCoefficient() > 0 ) {
                greaterThanZero++;
            }
            if ( reactant.getCoefficient() > 1 ) {
                greaterThanOne++;
            } 
        }
        return ( greaterThanZero > 1 || greaterThanOne > 0 );
    }
    
    /*
     * Updates the quantities of products and leftovers.
     */
    private void update() {
        int numReactions = getNumReactions();
        if ( numReactions > 0 ) {
            updateReaction( numReactions );
        }
        else {
            updateNoReaction();
        }
        fireStateChanged();
    }
    
    /*
     * Gets the number of reactions we have, based on the reaction cofficients and reactant quantities.
     */
    private int getNumReactions() {
        int numReactions = 0;
        if ( isReaction() ) {
            ArrayList<Integer> possibleValues = new ArrayList<Integer>();
            for ( Reactant reactant : reactants ) {
                if ( reactant.getCoefficient() != 0 ) {
                    possibleValues.add( new Integer( reactant.getQuantity() / reactant.getCoefficient() ) );
                }
            }
            assert ( possibleValues.size() > 0 );
            Collections.sort( possibleValues );
            numReactions = possibleValues.get( 0 );
        }
        return numReactions;
    }
    
    /*
     * We have a reaction, update the quantity of products and leftovers.
     */
    private void updateReaction( int numReactions ) {
        for ( Product product : products ) {
            product.setQuantity( numReactions * product.getCoefficient() );
        }
        for ( Reactant reactant : reactants ) {
            reactant.setLeftovers( reactant.getQuantity() - ( numReactions * reactant.getCoefficient() ) );
        }
    }
    
    /*
     * We have no reaction, update the quantity of products and leftovers.
     */
    private void updateNoReaction() {
        // no products
        for ( Product product : products ) { 
            product.setQuantity( 0 );
        }
        // all reactants are leftovers
        for ( Reactant reactant : reactants ) {
            reactant.setLeftovers( reactant.getQuantity() );
        }
    }
    
    public void addChangeListener( ChangeListener listener ) {
        listeners.add( listener );
    }
    
    public void removeChangeListener( ChangeListener listener ) {
        listeners.remove( listener );
    }
    
    private void fireStateChanged() {
        ChangeEvent event = new ChangeEvent( this );
        ArrayList<ChangeListener> listenersCopy = new ArrayList<ChangeListener>( listeners ); // avoid ConcurrentModificationException
        for ( ChangeListener listener : listenersCopy ) {
            listener.stateChanged( event );
        }
    }
}
