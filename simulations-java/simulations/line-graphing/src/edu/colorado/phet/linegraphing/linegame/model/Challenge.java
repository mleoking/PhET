// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.linegame.view.GameConstants;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * A game challenge where the user is trying to match some "given" line.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Challenge {

    public final Line answer; // the correct answer
    public final Property<Line> guess; // the user's current guess

    public Challenge( Line answer, Line guess ) {
        this.answer = answer.withColor( GameConstants.ANSWER_COLOR );
        this.guess = new Property<Line>( guess.withColor( GameConstants.GUESS_COLOR ) );
    }

    // Correct if the guess and answer are descriptions of the same line.
    public boolean isCorrect() {
        return answer.same( guess.get() );
    }

    // Creates the view component for the challenge.
    public abstract PNode createView( LineGameModel model, GameAudioPlayer audioPlayer, PDimension challengeSize );

    // Do anything required when the answer is visible.
    public abstract void setAnswerVisible( boolean visible );
}
