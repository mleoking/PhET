// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model.graphtheline;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.view.graphtheline.GTL_PS_Point_ChallengeNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Model for a "Graph the Line" (GTL) challenge.
 * Given an equation in point-slope (PS) form, graph the line by manipulating the Point.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GTL_PS_Point_Challenge extends GTL_Challenge {

    public GTL_PS_Point_Challenge( Line answer ) {
        super( answer, Line.createPointSlope( 0, 0, answer.rise, answer.run ) );
    }

    @Override public PNode createView( LineGameModel model, GameAudioPlayer audioPlayer, PDimension challengeSize ) {
        return new GTL_PS_Point_ChallengeNode( model, this, audioPlayer, challengeSize );
    }
}
