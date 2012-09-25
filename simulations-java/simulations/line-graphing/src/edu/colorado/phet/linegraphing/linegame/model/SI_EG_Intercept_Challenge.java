// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.linegame.view.SI_EG_Intercept_ChallengeNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Given an equation in slope-intercept form, graph the line by manipulating the intercept.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SI_EG_Intercept_Challenge extends Challenge {

    public SI_EG_Intercept_Challenge( Line answer, ModelViewTransform mvt ) {
        super( answer, Line.createSlopeIntercept( answer.rise, answer.run, 0 ), mvt );
    }

    @Override public PNode createView( LineGameModel model, GameAudioPlayer audioPlayer, PDimension challengeSize ) {
        return new SI_EG_Intercept_ChallengeNode( model, audioPlayer, challengeSize );
    }
}
