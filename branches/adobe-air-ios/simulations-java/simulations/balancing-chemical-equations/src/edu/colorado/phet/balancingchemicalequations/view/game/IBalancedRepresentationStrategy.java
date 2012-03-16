// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.game;

import edu.colorado.phet.balancingchemicalequations.view.BalancedRepresentation;

/**
 * Strategies for selection of the visual representation for "balanced".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface IBalancedRepresentationStrategy {

    /**
     * Gets the representation.
     * @return
     */
    public BalancedRepresentation getBalancedRepresentation();

    /**
     * Randomly returns one of the 2 representations.
     */
    public static class Random implements IBalancedRepresentationStrategy {

        public BalancedRepresentation getBalancedRepresentation() {
            return ( Math.random() < 0.5 ) ? BalancedRepresentation.BALANCE_SCALES : BalancedRepresentation.BAR_CHARTS;
        }
    }

    /**
     * Always returns the representation specified in the constructor.
     */
    public static class Constant implements IBalancedRepresentationStrategy {

        private final BalancedRepresentation balancedRepresentation;

        public Constant( BalancedRepresentation balancedRepresentation ) {
            this.balancedRepresentation = balancedRepresentation;
        }

        public BalancedRepresentation getBalancedRepresentation() {
            return balancedRepresentation;
        }
    }
}
