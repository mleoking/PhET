// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.reactantsproductsandleftovers;

import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;
import edu.colorado.phet.reactantsproductsandleftovers.model.Product;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;

/**
 * @author Sam Reid
 */
public class SimSharing {
    public static class ReactantEquationSpinner implements UserComponent {
        private final Reactant reactant;

        public ReactantEquationSpinner( Reactant reactant ) {
            this.reactant = reactant;
        }

        @Override public String toString() {
            return reactant.getName() + ".equationSpinner";
        }
    }

    public static class ProductSpinner implements UserComponent {
        private final Product reactant;

        public ProductSpinner( Product reactant ) {
            this.reactant = reactant;
        }

        @Override public String toString() {
            return reactant.getName() + ".productSpinner";
        }
    }

    public static class ReactantSpinner implements UserComponent {
        private final Reactant reactant;

        public ReactantSpinner( Reactant reactant ) {
            this.reactant = reactant;
        }

        @Override public String toString() {
            return reactant.getName() + ".reactantSpinner";
        }
    }

}