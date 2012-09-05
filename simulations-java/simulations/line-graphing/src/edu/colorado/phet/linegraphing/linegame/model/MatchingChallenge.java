// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.model.LineFactory;
import edu.colorado.phet.linegraphing.common.model.LineFactory.PointSlopeLineFactory;
import edu.colorado.phet.linegraphing.common.model.LineFactory.SlopeInterceptLineFactory;
import edu.colorado.phet.linegraphing.common.model.PointSlopeLine;
import edu.colorado.phet.linegraphing.common.model.SlopeInterceptLine;
import edu.colorado.phet.linegraphing.linegame.view.GraphSlopeInterceptLineNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * A game challenge where the user is trying to match some "given" line.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class MatchingChallenge<T extends PointSlopeLine> {

    public final T answer; // the correct answer
    public final Property<T> guess; // the user's current guess
    public final ModelViewTransform mvt; // transform between model and view coordinate frames
    public final LineFactory<T> lineFactory; // instantiates lines

    public MatchingChallenge( T answer, T guess, LineFactory<T> lineFactory, ModelViewTransform mvt ) {
        this.answer = answer;
        this.guess = new Property<T>( guess );
        this.lineFactory = lineFactory;
        this.mvt = mvt;
    }

    // Correct if the guess and answer are descriptions of the same line.
    public boolean isCorrect() {
        return answer.same( guess.get() );
    }

    // Creates the view component for the challenge.
    public abstract PNode createView( LineGameModel model, GameAudioPlayer audioPlayer, PDimension challengeSize );

    // A challenge that involves a line in slope-intercept form.
    public static class SlopeInterceptChallenge extends MatchingChallenge<SlopeInterceptLine> {

        public SlopeInterceptChallenge( SlopeInterceptLine answer, ModelViewTransform mvt ) {
            super( answer, SlopeInterceptLine.Y_EQUALS_X_LINE, new SlopeInterceptLineFactory(), mvt );
        }

        @Override public PNode createView( LineGameModel model, GameAudioPlayer audioPlayer, PDimension challengeSize ) {
            return new GraphSlopeInterceptLineNode( model, audioPlayer, challengeSize );
        }
    }

    // A challenge that involves a line in point-slope form.
    public static class PointSlopeChallenge extends MatchingChallenge<PointSlopeLine> {

        public PointSlopeChallenge( PointSlopeLine answer, ModelViewTransform mvt ) {
            super( answer, SlopeInterceptLine.Y_EQUALS_X_LINE, new PointSlopeLineFactory(), mvt );
        }

        @Override public PNode createView( LineGameModel model, GameAudioPlayer audioPlayer, PDimension challengeSize ) {
            return null; //TODO
        }
    }
}
