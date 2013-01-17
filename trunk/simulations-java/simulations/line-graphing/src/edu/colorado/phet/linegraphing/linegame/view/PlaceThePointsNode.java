// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.linegraphing.linegame.model.Challenge;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.model.PlaceThePoints;
import edu.colorado.phet.linegraphing.linegame.model.PlayState;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * View for "Place the Points" challenges.
 * This is a specialization of the "Graph the Line" view.
 * User manipulates 3 arbitrary points, equations are displayed on the left.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PlaceThePointsNode extends GraphTheLineNode {

    /**
     * Constructor
     *
     * @param model         the game model
     * @param challengeSize dimensions of the view rectangle that is available for rendering the challenge
     * @param audioPlayer   the audio player, for providing audio feedback during game play
     * @param challenge the challenge
     */
    public PlaceThePointsNode( final PlaceThePoints challenge, LineGameModel model, PDimension challengeSize, GameAudioPlayer audioPlayer ) {
        super( challenge, model, challengeSize, audioPlayer );

        model.state.addObserver( new VoidFunction1<PlayState>() {
            public void apply( PlayState state ) {

                // show user's line only in states where there guess is wrong.
                graphNode.setGuessVisible( !challenge.isCorrect() && ( state == PlayState.TRY_AGAIN || state == PlayState.NEXT ) );

                /*
                 * Plot (x1,y1) when for answer when user got the challenge wrong.
                 * Do not plot (x1,y1) for guess because none of the 3 points corresponds to (x1,y1).
                 */
                graphNode.setAnswerPointVisible( state == PlayState.NEXT && !challenge.isCorrect() );
                graphNode.setGuessPointVisible( false );
            }
        } );
    }

    // Creates the graph portion of the view.
    @Override protected ChallengeGraphNode createGraphNode( Challenge challenge ) {
        assert ( challenge instanceof PlaceThePoints );
        return new ThreePointsGraphNode( (PlaceThePoints) challenge );
    }
}
