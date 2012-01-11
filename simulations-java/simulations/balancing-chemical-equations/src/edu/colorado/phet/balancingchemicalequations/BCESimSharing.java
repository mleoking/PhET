// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balancingchemicalequations;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

/**
 * Sim-sharing strings that are specific to this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BCESimSharing {

    public static enum Actions implements IUserAction, IModelAction {
        guessChecked
    }

    public static enum Components implements IUserComponent {
    }

    public static enum Parameters implements IParameterKey {
        attempts, equation, isBalancedAndSimplified, isBalanced
    }

    public static class CoefficientNodeSpinner implements IUserComponent {
        private final String symbol;

        public CoefficientNodeSpinner( String symbol ) {
            this.symbol = symbol;
        }

        @Override public String toString() {
            return symbol;
        }
    }
}
