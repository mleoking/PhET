// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.linegame.LineGameConstants;

/**
 * Base class for game challenges where the user is trying to match some "given" line.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class MatchChallenge implements IChallenge {

    public final Line answer; // the correct answer
    public final Property<Line> guess; // the user's current guess

    protected MatchChallenge( Line answer, Line guess ) {
        this.answer = answer.withColor( LineGameConstants.ANSWER_COLOR );
        this.guess = new Property<Line>( guess.withColor( LineGameConstants.GUESS_COLOR ) );
    }

    // Correct if the guess and answer are descriptions of the same line.
    public boolean isCorrect() {
        return answer.same( guess.get() );
    }
}
