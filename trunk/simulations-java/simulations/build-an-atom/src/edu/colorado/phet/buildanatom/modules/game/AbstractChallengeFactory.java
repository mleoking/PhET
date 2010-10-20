/* Copyright 2010, University of Colorado */

package edu.colorado.phet.buildanatom.modules.game;

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
}
