// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.games;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IParameterKey;
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

    public static enum ModelComponents implements IModelComponent {
        game
    }

    public static enum ModelActions implements IModelAction {
        completed
    }

    public static enum ParameterKeys implements IParameterKey {
        score, perfectScore, time, bestTime, isNewBestTime, timerVisible, level, correct, attempts
    }
}
