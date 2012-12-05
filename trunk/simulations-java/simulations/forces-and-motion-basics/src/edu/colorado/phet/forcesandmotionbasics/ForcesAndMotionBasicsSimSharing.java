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
        tugOfWarTab, motionTab, frictionTab, accelerationTab,
        massCheckBox, valuesCheckBox, sumOfForcesCheckBox, speedCheckBox, accelerometerCheckBox, forcesCheckBox, soundCheckBox, goButton, stopButton, returnButton, largeBluePuller, mediumBluePuller, smallBluePuller1, smallBluePuller2, largeRedPuller, mediumRedPuller, smallRedPuller1, smallRedPuller2, appliedForceSliderKnob, frictionSliderKnob, fridge, crate1, crate2, girl, man, trash, gift, appliedForceTextField, speedCheckBoxIcon, accelerometerCheckBoxIcon,
        showForcesCheckBoxIcon, showSumOfForcesCheckBoxIcon
    }

    public static enum ModelComponents implements IModelComponent {
        forceModel, tugOfWarGame, stack
    }

    public static enum ParameterKeys implements IParameterKey {
        winningTeam, mass, sumOfForces, items, appliedForce
    }
}