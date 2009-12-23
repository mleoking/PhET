/* Copyright 2009, University of Colorado */
package edu.colorado.phet.reactantsproductsandleftovers.module.game;

import edu.colorado.phet.reactantsproductsandleftovers.model.ChemicalReaction;
import edu.colorado.phet.reactantsproductsandleftovers.model.Product;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameChallenge.ChallengeType;

/**
 * This is the user's answer to a game challenge, and it may or may not be correct.
 * <p>
 * An answer is correct if all of the reactants and products in the answer 
 * are equal to the reactants and products in the reaction, as defined by method equals.
 * <p>
 * An answer is constructed based on a reaction and challenge type.
 * The answer will have the same number of reactants and products as the reaction,
 * and they are guaranteed to be in the same order.
 * Depending on the challenge type, values in the answer will be initialized to zero.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameAnswer {
    
    private final ChemicalReaction reaction;
    private final Reactant[] reactants;
    private final Product[] products;
    
    GameAnswer( ChemicalReaction reaction, ChallengeType challengeType ) {
        this.reaction = reaction;
        reactants = createReactants( reaction, challengeType );
        products = createProducts( reaction, challengeType );
        assert( getNumberOfReactants() == reaction.getNumberOfReactants() );
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
    private static Reactant[] createReactants( ChemicalReaction reaction, ChallengeType challengeType ) {
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
        }
        return guessReactants;
    }
    
    /*
     * Copies the products from the reaction, in the same order.
     * Quantities are initialized to zero.
     */
    private static Product[] createProducts( ChemicalReaction reaction, ChallengeType challengeType ) {
        Product[] guessProducts = null;
        Product[] reactionProducts = reaction.getProducts();
        guessProducts = new Product[reactionProducts.length];
        for ( int i = 0; i < reactionProducts.length; i++ ) {
            guessProducts[i] = Product.newInstance( reactionProducts[i] );
            if ( challengeType == ChallengeType.HOW_MANY_PRODUCTS_AND_LEFTOVERS ) {
                guessProducts[i].setQuantity( 0 );
            }
        }
        return guessProducts;
    }
}