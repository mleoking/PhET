// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model.maketheequation;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.view.ChallengeNode;
import edu.colorado.phet.linegraphing.linegame.view.maketheequation.MTE_PS_Point_Slope_ChallengeNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Model for a "Make the Equation" (MTE) challenge.
 * Given a graph of a line in point-slope (PS) form, make the equation by changing the Point and Slope.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MTE_PS_PointSlope_Challenge extends MTE_Challenge {

    public MTE_PS_PointSlope_Challenge( Line answer ) {
        super( answer, Line.Y_EQUALS_X_LINE );
    }

    public ChallengeNode createView( LineGameModel model, GameAudioPlayer audioPlayer, PDimension challengeSize ) {
        return new MTE_PS_Point_Slope_ChallengeNode( model, this, audioPlayer, challengeSize );
    }
}
