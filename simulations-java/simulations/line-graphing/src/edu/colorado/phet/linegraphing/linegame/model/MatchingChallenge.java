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

    public final StraightLine given;
    public final Property<StraightLine> answer;

    public MatchingChallenge( StraightLine given ) {
        this.given = given;
        this.answer = new Property<StraightLine>( StraightLine.Y_EQUALS_X_LINE );
    }

    // Correct if the we have 2 descriptions of the same line.
    public boolean correct() {
        return given.same( answer.get() );
    }
}
