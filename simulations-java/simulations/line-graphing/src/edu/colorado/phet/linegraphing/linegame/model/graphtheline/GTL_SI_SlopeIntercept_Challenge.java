// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model.graphtheline;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.view.ChallengeNode;
import edu.colorado.phet.linegraphing.linegame.view.graphtheline.GTL_SI_SlopeIntercept_ChallengeNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Model for a "Graph the Line" (GTL) challenge.
 * Given an equation in slope-intercept (SI) form, graph the line by manipulating the Slope and Intercept.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GTL_SI_SlopeIntercept_Challenge extends GTL_Challenge {

    public GTL_SI_SlopeIntercept_Challenge( Line answer ) {
        super( answer, Line.Y_EQUALS_X_LINE );
    }

    public ChallengeNode createView( LineGameModel model, GameAudioPlayer audioPlayer, PDimension challengeSize ) {
        return new GTL_SI_SlopeIntercept_ChallengeNode( model, this, audioPlayer, challengeSize );
    }
}
