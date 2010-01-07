/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.module.game;

import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.reactantsproductsandleftovers.model.ChemicalReaction;
import edu.colorado.phet.reactantsproductsandleftovers.model.Product;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.colorado.phet.reactantsproductsandleftovers.model.Product.ProductChangeAdapter;
import edu.colorado.phet.reactantsproductsandleftovers.model.Product.ProductChangeListener;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant.ReactantChangeAdapter;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant.ReactantChangeListener;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameChallenge.ChallengeType;

/**
 * A "guess" is the user's answer to a game challenge, and it may or may not be correct.
 * We call it a "guess" to distinguish the user's answer from the correct answer.
 * (And yes, "guess" is semantically incorrect, since a guess is uninformed. Live with it.)
 * <p>
 * A guess is correct if all of the reactants and products in the guess are equal to
 * the reactants and products in the reaction (the correct answer), as defined by method equals.
 * <p>
 * A guess is constructed based on a reaction and challenge type.
 * The guess will have the same number of reactants and products as the reaction,
 * and they are guaranteed to be in the same order.
 * Depending on the challenge type, values in the guess will be initialized to zero.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameGuess {
    
    private final ChemicalReaction reaction;
    private final Reactant[] reactants;
    private final Product[] products;
    private final ArrayList<ChangeListener> listeners;
    
    public GameGuess( ChemicalReaction reaction, ChallengeType challengeType ) {
        
        this.reaction = reaction;
        listeners = new ArrayList<ChangeListener>();
        
        // create similar reactants
        ReactantChangeListener reactantChangeListener = new ReactantChangeAdapter() {
            
            @Override
            public void quantityChanged() {
                fireStateChanged();
            }
            
            @Override
            public void leftoversChanged() {
                fireStateChanged();
            }
        };
        reactants = createReactants( reaction, challengeType, reactantChangeListener );
        assert( getNumberOfReactants() == reaction.getNumberOfReactants() );
        
        // create similar products
        ProductChangeListener productChangeListener = new ProductChangeAdapter() {
            @Override
            public void quantityChanged() {
                fireStateChanged();
            }
        };
        products = createProducts( reaction, challengeType, productChangeListener );
        assert( getNumberOfProducts() == reaction.getNumberOfProducts() );
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
    
    public boolean isCorrect() {
        // all reactants must be equal
        for ( int i = 0; i < getNumberOfReactants(); i++ ) {
            if ( !getReactant( i ).equals( reaction.getReactant( i ) ) ) { 
                return false;
            }
        }
        // all products must be equal
        for ( int i = 0; i < getNumberOfProducts(); i++ ) {
            if ( !getProduct( i ).equals( reaction.getProduct( i ) ) ) { 
                return false;
            }
        }
        return true;
    }
    
    /*
     * Copies the reactants from the reaction, in the same order.
     * Depending on the challenge type, either quantities or leftovers are initialized to zero.
     */
    private static Reactant[] createReactants( ChemicalReaction reaction, ChallengeType challengeType, ReactantChangeListener listener ) {
        Reactant[] reactionReactants = reaction.getReactants();
        Reactant[] guessReactants = new Reactant[ reactionReactants.length ];
        for ( int i = 0; i < reactionReactants.length; i++ ) {
            guessReactants[i] = Reactant.newInstance( reactionReactants[i] );
            if ( challengeType == ChallengeType.HOW_MANY_PRODUCTS_AND_LEFTOVERS ) { 
                guessReactants[i].setLeftovers( 0 );
            }
            else {
                guessReactants[i].setQuantity( 0 );
            }
            guessReactants[i].addReactantChangeListener( listener );
        }
        return guessReactants;
    }
    
    /*
     * Copies the products from the reaction, in the same order.
     * Quantities are initialized to zero.
     */
    private static Product[] createProducts( ChemicalReaction reaction, ChallengeType challengeType, ProductChangeListener listener ) {
        Product[] guessProducts = null;
        Product[] reactionProducts = reaction.getProducts();
        guessProducts = new Product[reactionProducts.length];
        for ( int i = 0; i < reactionProducts.length; i++ ) {
            guessProducts[i] = Product.newInstance( reactionProducts[i] );
            if ( challengeType == ChallengeType.HOW_MANY_PRODUCTS_AND_LEFTOVERS ) {
                guessProducts[i].setQuantity( 0 );
            }
            guessProducts[i].addProductChangeListener( listener );
        }
        return guessProducts;
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
