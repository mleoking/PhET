// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

/**
 * States during the "play" phase of a game, mutually exclusive. (See GamePhase.)
 * For lack of better names, the state names correspond to the main action that
 * the user can take in that state.  For example. the FIRST_CHECK state is where the user
 * has their first opportunity to press the "Check" button.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public enum PlayState {
    FIRST_CHECK,
    TRY_AGAIN,
    SECOND_CHECK,
    SHOW_ANSWER,
    NEXT,
    NONE // use this value when game is not in the "play" phase
}
