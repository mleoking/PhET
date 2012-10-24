// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model.placepoints;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.view.ChallengeNode;
import edu.colorado.phet.linegraphing.linegame.view.placepoints.P3P_PS_ChallengeNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Model for a "Place 3 Points" (P3P) challenge where the equation is given in point-slope (PS) form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class P3P_PS_Challenge extends P3P_Challenge {

    public P3P_PS_Challenge( Line line ) {
        super( line );
    }

    public ChallengeNode createView( LineGameModel model, GameAudioPlayer audioPlayer, PDimension challengeSize ) {
        return new P3P_PS_ChallengeNode( model, this, audioPlayer, challengeSize );
    }
}
