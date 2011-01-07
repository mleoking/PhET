// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.module.game;

import edu.colorado.phet.reactantsproductsandleftovers.model.ChemicalReaction;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;

/**
 * Base class for creating game challenges.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractChallengeFactory implements IChallengeFactory {
    
    /*
     * Generates a random non-zero quantity.
     * We need at least one of each reactant to have a valid reaction.
     */
    protected static int getRandomQuantity( int max ) {
        return getRandomQuantity( 1, max );
    }
    
    /*
     * Generates a random quantity in some range.
     * Min must be > 0, because we need at least one of each reactant to have a valid reaction.
     */
    protected static int getRandomQuantity( int min, int max ) {
        if ( !( min > 0 ) ) {
            throw new IllegalArgumentException( "min must be > 0: " + min );
        }
        if ( !( min <= max ) ) {
            throw new IllegalArgumentException( "min must be <= max" );
        }
        return min + (int) ( Math.random() * ( max - min + 1 ) );
    }

    /*
     * Uses reflection to instantiate a chemical reaction by class.
     */
    protected static ChemicalReaction instantiateReaction( Class<? extends ChemicalReaction> c ) {
        ChemicalReaction reaction = null;
        try {
            reaction = c.newInstance();
        }
        catch ( InstantiationException e ) {
            e.printStackTrace();
        }
        catch ( IllegalAccessException e ) {
            e.printStackTrace();
        }
        return reaction;
    }
    
    protected static void fixQuantityRangeViolation( ChemicalReaction reaction, int maxQuantity ) {
        fixQuantityRangeViolation( reaction, maxQuantity, false /* enableDebugOutput */ );
    }
    
    /*
     * Fixes any quantity range violations in a reaction.
     * We do this by decrementing reactant quantites by 1, alternating reactants as we do so.
     * Each reactant must have a quantity of at least 1, in order to have a valid reaction.
     * 
     * @throw IllegalStateException if reducing all reactant quantities to 1 does not fix a range violation
     */
    protected static void fixQuantityRangeViolation( ChemicalReaction reaction, int maxQuantity, boolean enableDebugOutput ) {
        
        if ( hasQuantityRangeViolation( reaction ) ) {

            if ( enableDebugOutput ) {
                System.out.print( "AbstractGameStrategy.fixQuantityRangeViolation" );
                System.out.print( " reaction: " + reaction.getEquationPlainText() );
                System.out.print( " violation: " + reaction.getQuantitiesString() );
            }

            // First, make sure all reactant quantities are in range.
            for ( Reactant reactant : reaction.getReactants() ) {
                if ( reactant.getQuantity() > maxQuantity ) {
                    reactant.setQuantity( maxQuantity );
                }
            }

            // Then incrementally reduce reactant quantities, alternating reactants.
            int reactantIndex = 0;
            boolean changed = false;
            while ( hasQuantityRangeViolation( reaction ) ) {
                Reactant reactant = reaction.getReactant( reactantIndex );
                int quantity = reactant.getQuantity();
                if ( quantity > 1 ) {
                    reactant.setQuantity( quantity - 1 );
                    changed = true;
                }
                reactantIndex++;
                if ( reactantIndex > reaction.getNumberOfReactants() - 1 ) {
                    reactantIndex = 0;
                    if ( !changed ) {
                        // we haven't been able to reduce any reactant
                        break;
                    }
                }
            }

            // If all reactants have been reduced to 1 and we are still out of range, bail with a serious error.
            if ( hasQuantityRangeViolation( reaction ) ) {
                throw new IllegalStateException( "range violation can't be fixed: " + reaction.getEquationHTML() + " : " + reaction.getQuantitiesString() );
            }

            if ( enableDebugOutput ) {
                System.out.println( " fixed: " + reaction.getQuantitiesString() );
            }
        }
    }
    
    /*
     * Checks a reaction for quantity range violations.
     */
    protected static boolean hasQuantityRangeViolation( ChemicalReaction reaction ) {
        final int maxQuantity = GameModel.getQuantityRange().getMax();
        boolean violation = false;
        for ( int i = 0; !violation && i < reaction.getNumberOfReactants(); i++ ) {
            if ( reaction.getReactant( i ).getQuantity() > maxQuantity || reaction.getReactant( i ).getLeftovers() > maxQuantity ) {
                violation = true;
            }
        }
        for ( int i = 0; !violation && i < reaction.getNumberOfProducts(); i++ ) {
            if ( reaction.getProduct( i ).getQuantity() > maxQuantity ) {
                violation = true;
            }
        }
        return violation;
    }
}
