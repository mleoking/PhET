// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.awt.Color;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.linegame.view.SlopeInterceptLineChallengeNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * A game challenge where the user is trying to match some "given" line.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class MatchingChallenge {

    public final Line answer; // the correct answer
    public final Property<Line> guess; // the user's current guess
    public final ModelViewTransform mvt; // transform between model and view coordinate frames

    public MatchingChallenge( Line answer, Line guess, ModelViewTransform mvt ) {
        this.answer = answer;
        this.guess = new Property<Line>( guess );
        this.mvt = mvt;
    }

    // Correct if the guess and answer are descriptions of the same line.
    public boolean isCorrect() {
        return answer.same( guess.get() );
    }

    // Creates the view component for the challenge.
    public abstract PNode createView( LineGameModel model, GameAudioPlayer audioPlayer, PDimension challengeSize );

    // Given an equation in slope-intercept form, create a line by manipulating slope and intercept.
    public static class GraphSlopeInterceptChallenge extends MatchingChallenge {

        public GraphSlopeInterceptChallenge( Line answer, ModelViewTransform mvt ) {
            super( answer, Line.Y_EQUALS_X_LINE, mvt );
        }

        @Override public PNode createView( LineGameModel model, GameAudioPlayer audioPlayer, PDimension challengeSize ) {
            return new SlopeInterceptLineChallengeNode( model, audioPlayer, challengeSize, true, true );
        }
    }

    // Given an equation in slope-intercept form, create a line by manipulating slope.
    public static class GraphSlopeChallenge extends MatchingChallenge {

        public GraphSlopeChallenge( Line answer, ModelViewTransform mvt ) {
            super( answer, Line.createSlopeIntercept( 1, 1, answer.y1, Color.BLACK ), mvt );
        }

        @Override public PNode createView( LineGameModel model, GameAudioPlayer audioPlayer, PDimension challengeSize ) {
            return new SlopeInterceptLineChallengeNode( model, audioPlayer, challengeSize, true, false );
        }
    }

    // Given an equation in slope-intercept form, create a line by manipulating intercept.
    public static class GraphInterceptChallenge extends MatchingChallenge {

        public GraphInterceptChallenge( Line answer, ModelViewTransform mvt ) {
            super( answer, Line.createSlopeIntercept( answer.rise, answer.run, 0, Color.BLACK ), mvt );
        }

        @Override public PNode createView( LineGameModel model, GameAudioPlayer audioPlayer, PDimension challengeSize ) {
            return new SlopeInterceptLineChallengeNode( model, audioPlayer, challengeSize, false, true );
        }
    }
}
