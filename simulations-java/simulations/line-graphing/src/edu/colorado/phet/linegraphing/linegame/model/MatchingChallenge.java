// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.linegraphing.common.model.StraightLine;

/**
 * A game challenge where the user is trying to match some "given" line.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MatchingChallenge {

    public final StraightLine answer; // the correct answer
    public final Property<StraightLine> guess; // the user's current guess

    public MatchingChallenge( StraightLine answer ) {
        this.answer = answer;
        this.guess = new Property<StraightLine>( StraightLine.Y_EQUALS_X_LINE.withColor( answer.color ) );
    }

    // Correct if the we have 2 descriptions of the same line.
    public boolean isCorrect() {
        return answer.same( guess.get() );
    }
}
