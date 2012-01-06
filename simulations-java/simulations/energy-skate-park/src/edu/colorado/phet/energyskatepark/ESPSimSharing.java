package edu.colorado.phet.energyskatepark;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingConstants;

/**
 * @author Sam Reid
 */
public class ESPSimSharing {

    public static enum Objects implements SimSharingConstants.User.UserComponent {
        skater, trackButton
    }

    public static class Actions {
    }

    public static enum ParameterKeys implements SimSharingConstants.ParameterKey {
        track
    }
}
