package edu.colorado.phet.energyskatepark;

import edu.colorado.phet.common.phetcommon.simsharing.messages.SimSharingConstants;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;

/**
 * @author Sam Reid
 */
public class EnergySkateParkSimSharing {

    public static enum Objects implements UserComponent {
        skater, trackButton, slowMotionRadioButton, normalSpeedRadioButton
    }

    public static class Actions {
    }

    public static enum ParameterKeys implements SimSharingConstants.ParameterKey {
        track
    }
}
