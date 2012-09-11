// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.awt.Color;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.linegame.view.PS_EG_Slope_ChallengeNode;
import edu.colorado.phet.linegraphing.linegame.view.SI_EG_Slope_ChallengeNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Given an equation in point-slope form, graph the line by manipulating slope.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PS_EG_Slope_Challenge extends Challenge {

    public PS_EG_Slope_Challenge( Line answer, ModelViewTransform mvt ) {
        super( answer, Line.createPointSlope( answer.x1, answer.y1, 1, 3, Color.BLACK ), mvt );
    }

    @Override public PNode createView( LineGameModel model, GameAudioPlayer audioPlayer, PDimension challengeSize ) {
        return new PS_EG_Slope_ChallengeNode( model, audioPlayer, challengeSize );
    }
}
