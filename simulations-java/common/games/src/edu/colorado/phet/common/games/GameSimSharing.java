// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.games;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

/**
 * Sim-sharing enums related to the game package.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameSimSharing {

    public static enum UserComponents implements IUserComponent {
        startGameButton, newGameButton,
        newGameYesButton, newGameNoButton,
        levelRadioButton, timerOnRadioButton, timerOffRadioButton, soundOnRadioButton, soundOffRadioButton
    }
}
