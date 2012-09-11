// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.linegame.view.SI_EG_Points_ChallengeNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Given an equation in slope-intercept form, graph the line by manipulating slope and intercept.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SI_EG_Points_Challenge extends Challenge {

    public SI_EG_Points_Challenge( Line answer, ModelViewTransform mvt ) {
        super( answer, Line.Y_EQUALS_X_LINE, mvt );
    }

    @Override public PNode createView( LineGameModel model, GameAudioPlayer audioPlayer, PDimension challengeSize ) {
        return new SI_EG_Points_ChallengeNode( model, audioPlayer, challengeSize );
    }
}
