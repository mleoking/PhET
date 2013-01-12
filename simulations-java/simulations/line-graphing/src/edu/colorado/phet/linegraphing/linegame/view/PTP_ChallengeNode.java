// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.linegraphing.linegame.model.Challenge;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.model.ManipulationMode;
import edu.colorado.phet.linegraphing.linegame.model.PTP_Challenge;
import edu.colorado.phet.linegraphing.linegame.model.PlayState;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * View for "Place the Points" (PTP) challenges.
 * User manipulates 3 arbitrary points, equations are displayed on the left.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PTP_ChallengeNode extends GTL_ChallengeNode {

    /**
     * Constructor
     *
     * @param model         the game model
     * @param challengeSize dimensions of the view rectangle that is available for rendering the challenge
     * @param audioPlayer   the audio player, for providing audio feedback during game play
     * @parma challenge the challenge
     */
    public PTP_ChallengeNode( final PTP_Challenge challenge, LineGameModel model, PDimension challengeSize, GameAudioPlayer audioPlayer ) {
        super( challenge, model, challengeSize, audioPlayer );

        // Make the user's line visible only in states where there guess is wrong.
        model.state.addObserver( new VoidFunction1<PlayState>() {
            public void apply( PlayState state ) {
                graphNode.setGuessVisible( !challenge.isCorrect() && ( state == PlayState.TRY_AGAIN || state == PlayState.NEXT ) );
            }
        } );
    }

    // Creates the graph portion of the view.
    @Override protected ChallengeGraphNode createGraphNode( Challenge challenge ) {
        assert ( challenge instanceof PTP_Challenge );
        return new ThreePointsGraphNode( (PTP_Challenge) challenge );
    }
}
