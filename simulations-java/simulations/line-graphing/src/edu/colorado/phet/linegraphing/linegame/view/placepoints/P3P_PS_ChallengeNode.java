// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view.placepoints;

import java.awt.Color;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.model.placepoints.P3P_Challenge;
import edu.colorado.phet.linegraphing.pointslope.view.PointSlopeEquationNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * View component for a "Place 3 Points" (P3P) challenge with equations in point-slope (PS) form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class P3P_PS_ChallengeNode extends P3P_ChallengeNode {

    public P3P_PS_ChallengeNode( final LineGameModel model, final P3P_Challenge challenge, final GameAudioPlayer audioPlayer, PDimension challengeSize ) {
        super( model, challenge, audioPlayer, challengeSize );
    }

    // Creates the equation portion of the view.
    protected PNode createEquationNode( Line line, PhetFont font, Color color ) {
        return new PointSlopeEquationNode( line, font, color );
    }

    // Creates the graph portion of the view.
    protected P3P_GraphNode createGraphNode( P3P_Challenge challenge ) {
        return new P3P_PS_GraphNode( challenge );
    }
}
