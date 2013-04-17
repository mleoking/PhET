// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balancingchemicalequations;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

/**
 * Sim-sharing enums that are specific to this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BCESimSharing {

    public static enum UserComponents implements IUserComponent {
        introductionTab, balancingGameTab
    }

    public static enum Actions implements IUserAction, IModelAction {
        guessChecked
    }

    public static enum Parameters implements IParameterKey {
        attempts, equation, isBalancedAndSimplified, isBalanced
    }
}
