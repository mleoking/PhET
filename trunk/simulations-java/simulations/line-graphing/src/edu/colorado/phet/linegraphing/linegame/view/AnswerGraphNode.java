// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import edu.colorado.phet.linegraphing.linegame.model.Challenge;
import edu.colorado.phet.linegraphing.linegame.model.ManipulationMode;

/**
 * Challenge graph that initially shows the answer, but not the user's guess.
 * This graph is not interactive, has no manipulators, and is used in "Make the Equation" challenges.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class AnswerGraphNode extends ChallengeGraphNode {

    public AnswerGraphNode( Challenge challenge ) {
        super( challenge, true /* slopeToolEnabled */ );
        setAnswerVisible( true );
    }
}
