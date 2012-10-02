package edu.colorado.phet.forcesandmotionbasics;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

/**
 * Components and other elements for sim event data collection.
 *
 * @author Sam Reid
 */
public class ForcesAndMotionBasicsSimSharing {
    public static enum UserComponents implements IUserComponent {
        tugOfWarTab, motionTab, frictionTab,
        massCheckBox, valuesCheckBox, sumOfForcesCheckBox, speedCheckBox, forcesCheckBox, soundCheckBox, goButton, stopButton, returnButton, largeBluePuller, mediumBluePuller, smallBluePuller1, smallBluePuller2, largeRedPuller, mediumRedPuller, smallRedPuller1, smallRedPuller2, speedCheckBoxIcon
    }

    public static enum ModelComponents implements IModelComponent {
        forceModel, tugOfWarGame
    }

    public static enum ParameterKeys implements IParameterKey {
        winningTeam, sumOfForces
    }
}