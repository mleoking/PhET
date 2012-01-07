package edu.colorado.phet.energyskatepark;

import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;

/**
 * @author Sam Reid
 */
public class EnergySkateParkSimSharing {

    public static enum UserComponents implements UserComponent {
        skater, trackButton, slowMotionRadioButton, normalSpeedRadioButton,
        barGraphCheckBox, pieChartCheckBox, gridCheckBox, speedCheckBox,

        trackPlaygroundTab, introTab, frictionTab
    }

    public static class Actions {
    }

    public static enum ParameterKeys implements ParameterKey {
        track
    }
}
