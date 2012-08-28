// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.linegraphing.common.model.PointSlopeLine;
import edu.colorado.phet.linegraphing.common.model.SlopeInterceptLine;
import edu.colorado.phet.linegraphing.linegame.view.GameConstants;

/**
 * A game challenge where the user is trying to match some "given" line.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MatchingChallenge {

    public final SlopeInterceptLine answer; // the correct answer
    public final Property<SlopeInterceptLine> guess; // the user's current guess

    public MatchingChallenge( SlopeInterceptLine answer ) {
        this.answer = answer;
        this.guess = new Property<SlopeInterceptLine>( new SlopeInterceptLine( 1, 1, 0, GameConstants.GUESS_COLOR ) /* y=x */ );
    }

    // Correct if the we have 2 descriptions of the same line.
    public boolean isCorrect() {
        return answer.same( guess.get() );
    }
}
