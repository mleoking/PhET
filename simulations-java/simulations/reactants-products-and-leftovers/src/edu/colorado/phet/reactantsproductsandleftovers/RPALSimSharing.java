// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.reactantsproductsandleftovers;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.reactantsproductsandleftovers.model.Product;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;

//TODO this all looks very suspicious, and very misplaced. Should be replaced with proper chaining.
/**
 * @author Sam Reid
 */
public class RPALSimSharing {
    public static class ReactantEquationSpinner implements IUserComponent {
        private final Reactant reactant;

        public ReactantEquationSpinner( Reactant reactant ) {
            this.reactant = reactant;
        }

        @Override public String toString() {
            return reactant.getName() + ".equationSpinner";
        }
    }

    public static class ProductSpinner implements IUserComponent {
        private final Product reactant;

        public ProductSpinner( Product reactant ) {
            this.reactant = reactant;
        }

        @Override public String toString() {
            return reactant.getName() + ".productSpinner";
        }
    }

    public static class ReactantSpinner implements IUserComponent {
        private final Reactant reactant;

        public ReactantSpinner( Reactant reactant ) {
            this.reactant = reactant;
        }

        @Override public String toString() {
            return reactant.getName() + ".reactantSpinner";
        }
    }

}