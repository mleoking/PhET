package edu.colorado.phet.forcesandmotionbasics;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

/**
 * Components and other elements for sim event data collection.
 *
 * @author Sam Reid
 */
public class ForcesAndMotionBasicsSimSharing {
    public static enum UserComponents implements IUserComponent {
        tugOfWarTab, motionTab, frictionTab,
        massCheckBox, valuesCheckBox, sumOfForcesCheckBox, speedCheckBox, forcesCheckBox, soundCheckBox, goButton, stopButton, returnButton, speedCheckBoxIcon
    }
}