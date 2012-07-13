// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Model for the "Line Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LineGameModel implements Resettable {

    // phases of a game
    public enum GamePhase {
        SETTINGS,  // user is choosing game settings
        PLAY, // user is playing the game
        RESULTS, // user is receiving results of playing the game
        REWARD // user is receiving a reward for exceptional game play
    }

    public final Property<GamePhase> phase;

    public LineGameModel() {
        phase = new Property<GamePhase>( GamePhase.SETTINGS );
    }

    public void reset() {
        phase.reset();
    }
}