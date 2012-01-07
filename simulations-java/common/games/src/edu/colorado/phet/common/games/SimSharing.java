// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.games;

import edu.colorado.phet.common.phetcommon.simsharing.messages.ModelAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ModelObject;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKey;

/**
 * @author Sam Reid
 */
public class SimSharing {
    public static enum Components implements ModelObject {game}

    public static enum Parameters implements ParameterKey {score, perfectScore, time, bestTime, isNewBestTime, timerVisible, level}

    public static enum Actions implements ModelAction {
        ended
    }
}
