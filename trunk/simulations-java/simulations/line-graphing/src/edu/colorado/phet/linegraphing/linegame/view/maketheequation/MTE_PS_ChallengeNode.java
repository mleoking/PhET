// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view.maketheequation;

import java.awt.Color;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.EquationNode;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.model.MTE_Challenge;
import edu.colorado.phet.linegraphing.pointslope.view.PointSlopeEquationNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Base class for "Make the Equation" (MTE) challenges that use point-slope (PS) form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class MTE_PS_ChallengeNode extends MTE_ChallengeNode {

    public MTE_PS_ChallengeNode( LineGameModel model, MTE_Challenge challenge, GameAudioPlayer audioPlayer, PDimension challengeSize ) {
        super( model, challenge, audioPlayer, challengeSize );
    }

    @Override protected EquationNode createAnswerEquationNode( Line line, PhetFont font, Color color ) {
        return new PointSlopeEquationNode( line, font, color );
    }

    // Creates the graph portion of the view.
    @Override protected MTE_GraphNode createGraphNode( MTE_Challenge challenge ) {
        return new MTE_GraphNode( challenge );
    }
}
