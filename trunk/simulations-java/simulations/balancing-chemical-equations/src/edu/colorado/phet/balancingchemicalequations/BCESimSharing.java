// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balancingchemicalequations;

import edu.colorado.phet.common.phetcommon.simsharing.messages.ModelAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;

/**
 * Sim-sharing strings that are specific to this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BCESimSharing {

    public static enum Actions implements UserAction, ModelAction {
        guessChecked
    }

    public static enum Components implements UserComponent {
    }

    public static enum Parameters implements ParameterKey {
        attempts, equation, isBalancedAndSimplified, isBalanced
    }

    public static class CoefficientNodeSpinner implements UserComponent {
        private final String symbol;

        public CoefficientNodeSpinner( String symbol ) {
            this.symbol = symbol;
        }

        @Override public String toString() {
            return symbol;
        }
    }
}
