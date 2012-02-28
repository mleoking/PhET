// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.reactantsproductsandleftovers;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.reactantsproductsandleftovers.model.Product;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;

/**
 * Sim-sharing enums that are specific to this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RPALSimSharing {

    public static enum UserComponents implements IUserComponent {
        sandwichShopTab, realReactionTab, gameTab,
        equationSpinner, reactantSpinner, productSpinner, leftoverSpinner,
        sandwichRadioButton, realReactionRadioButton,
        checkButton, nextButton, tryAgainButton, showAnswerButton
    }
}