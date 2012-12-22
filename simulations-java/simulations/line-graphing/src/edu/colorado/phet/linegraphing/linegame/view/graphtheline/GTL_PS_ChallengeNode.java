// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view.graphtheline;

import java.awt.Color;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.EquationNode;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.model.GTL_Challenge;
import edu.colorado.phet.linegraphing.pointslope.view.PointSlopeEquationNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Base class view for "Graph the Line" (GTL) challenges that use point-slope (PS) form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class GTL_PS_ChallengeNode extends GTL_ChallengeNode {

    public GTL_PS_ChallengeNode( LineGameModel model, GTL_Challenge challenge, GameAudioPlayer audioPlayer, PDimension challengeSize ) {
        super( model, challenge, audioPlayer, challengeSize );
    }

    // Creates the equation portion of the view.
    @Override protected EquationNode createEquationNode( Line line, PhetFont font, Color color ) {
        return new PointSlopeEquationNode( line, font, color );
    }

}
