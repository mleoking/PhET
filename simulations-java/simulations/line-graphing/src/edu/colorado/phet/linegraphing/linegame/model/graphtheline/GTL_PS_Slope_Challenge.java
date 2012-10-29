// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model.graphtheline;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.linegraphing.common.LGResources;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.view.ChallengeNode;
import edu.colorado.phet.linegraphing.linegame.view.graphtheline.GTL_PS_Slope_ChallengeNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Model for a "Graph the Line" (GTL) challenge.
 * Given an equation in point-slope (PS) form, graph the line by manipulating the Slope.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GTL_PS_Slope_Challenge extends GTL_Challenge {

    public GTL_PS_Slope_Challenge( Line answer ) {
        super( Strings.SET_THE_SLOPE, answer, Line.createPointSlope( answer.x1, answer.y1, 1, 1 ) );
    }

    public ChallengeNode createView( LineGameModel model, GameAudioPlayer audioPlayer, PDimension challengeSize ) {
        return new GTL_PS_Slope_ChallengeNode( model, this, audioPlayer, challengeSize );
    }
}
