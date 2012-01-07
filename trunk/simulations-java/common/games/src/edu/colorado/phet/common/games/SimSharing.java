// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.games;

import edu.colorado.phet.common.phetcommon.simsharing.messages.SimSharingConstants;

/**
 * @author Sam Reid
 */
public class SimSharing {
    public static enum Components implements SimSharingConstants.Model.ModelObject {game}

    public static enum Parameters implements SimSharingConstants.ParameterKey {score, perfectScore, time, bestTime, isNewBestTime, timerVisible, level}

    public static enum Actions implements SimSharingConstants.Model.ModelAction {
        ended
    }
}
