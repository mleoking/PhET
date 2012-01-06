// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balancingchemicalequations;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingConstants;

/**
 * Sim-sharing strings that are specific to this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BCESimSharing {

    public static enum Actions implements SimSharingConstants.User.UserAction, SimSharingConstants.Model.ModelAction {
        guessChecked
    }

    public static enum Components implements SimSharingConstants.User.UserComponent {
    }

    public static enum Parameters implements SimSharingConstants.ParameterKey {
        attempts, equation, isBalancedAndSimplified, isBalanced
    }

    public static class CoefficientNodeSpinner implements SimSharingConstants.User.UserComponent {
        private final String symbol;

        public CoefficientNodeSpinner( String symbol ) {
            this.symbol = symbol;
        }

        @Override public String toString() {
            return symbol;
        }
    }
}
