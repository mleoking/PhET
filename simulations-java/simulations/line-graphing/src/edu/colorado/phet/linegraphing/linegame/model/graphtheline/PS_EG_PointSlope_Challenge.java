// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model.graphtheline;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.linegame.model.Challenge;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.view.graphtheline.PS_EG_PointSlope_ChallengeNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Given an equation in point-slope form, graph the line by manipulating the point and slope.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PS_EG_PointSlope_Challenge extends Challenge {

    public PS_EG_PointSlope_Challenge( Line answer, ModelViewTransform mvt ) {
        super( answer, Line.Y_EQUALS_X_LINE, mvt );
    }

    @Override public PNode createView( LineGameModel model, GameAudioPlayer audioPlayer, PDimension challengeSize ) {
        return new PS_EG_PointSlope_ChallengeNode( model, audioPlayer, challengeSize );
    }
}
