// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.model;

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

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
    private final EventListenerList listeners;
    
    public ChemicalReaction( Reactant[] reactants, Product[] products ) {
        this( getEquationHTML( reactants, products ), reactants, products );
    }

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
        this.listeners = new EventListenerList();
        
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
        int numberOfReactions = getNumberOfReactions();
        for ( Product product : products ) {
            product.setQuantity( numberOfReactions * product.getCoefficient() );
        }
        for ( Reactant reactant : reactants ) {
            reactant.setLeftovers( reactant.getQuantity() - ( numberOfReactions * reactant.getCoefficient() ) );
        }
        fireStateChanged();
    }
    
    /*
     * Gets the number of reactions we have, based on the coefficients and reactant quantities.
     */
    private int getNumberOfReactions() {
        int numberOfReactions = 0;
        if ( isReaction() ) {
            ArrayList<Integer> possibleValues = new ArrayList<Integer>();
            for ( Reactant reactant : reactants ) {
                if ( reactant.getCoefficient() != 0 ) {
                    possibleValues.add( new Integer( reactant.getQuantity() / reactant.getCoefficient() ) );
                }
            }
            assert ( possibleValues.size() > 0 );
            Collections.sort( possibleValues );
            numberOfReactions = possibleValues.get( 0 );
        }
        return numberOfReactions;
    }
    
    public void addChangeListener( ChangeListener listener ) {
        listeners.add( ChangeListener.class, listener );
    }
    
    public void removeChangeListener( ChangeListener listener ) {
        listeners.remove( ChangeListener.class, listener );
    }
    
    private void fireStateChanged() {
        ChangeEvent event = new ChangeEvent( this );
        for ( ChangeListener listener : listeners.getListeners( ChangeListener.class ) ) {
            listener.stateChanged( event );
        }
    }
    
    public String toString() {
        return getEquationPlainText() + " " + getQuantitiesString();
    }
    
    public String getEquationHTML() {
        return getEquationHTML( reactants, products );
    }
    
    /*
     * Creates an HTML fragment representation of the reaction's equation.
     * Example: 2F<sub>2</sub>+1H<sub>2</sub>O->1OF<sub>2</sub>+2HF
     */
    private static String getEquationHTML( Reactant[] reactants, Product[] products ) {
        String s = "";
        for ( int i = 0; i < reactants.length; i++ ) {
            if ( i != 0 ) {
                s += "+";
            }
            s += reactants[i].getCoefficient();
            s += reactants[i].getName();
        }
        s += "->";
        for ( int i = 0; i < products.length; i++ ) {
            if ( i != 0 ) {
                s += "+";
            }
            s += products[i].getCoefficient();
            s += products[i].getName();
        }
        return s;
    }
    
    /**
     * Removes the <sub> tags from the HTML form of the equation.
     * This is intended for use in debug output, where HTML is difficult to read.
     * @return
     */
    public String getEquationPlainText() {
        return getEquationHTML().replaceAll( "<sub>", "" ).replaceAll( "</sub>", "" );
    }
    
    /**
     * Example: 4,1 -> 1,2,2,0
     * @return
     */
    public String getQuantitiesString() {
        String s = "";
        // reactants
        for ( int i = 0; i < reactants.length; i++ ) {
            if ( i != 0 ) {
                s += ",";
            }
            s += String.valueOf( reactants[i].getQuantity() );
        }
        // arrow
        s += " -> ";
        // products
        for ( int i = 0; i < products.length; i++ ) {
            if ( i != 0 ) {
                s += ",";
            }
            s += String.valueOf( products[i].getQuantity() );
        }
        // leftovers
        for ( int i = 0; i < reactants.length; i++ ) {
            s += ",";
            s += String.valueOf( reactants[i].getLeftovers() );
        }
        return s;
    }
}
