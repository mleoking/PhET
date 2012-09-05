// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.model.LineFactory;
import edu.colorado.phet.linegraphing.common.model.LineFactory.PointSlopeLineFactory;
import edu.colorado.phet.linegraphing.common.model.LineFactory.SlopeInterceptLineFactory;
import edu.colorado.phet.linegraphing.common.model.PointSlopeLine;
import edu.colorado.phet.linegraphing.common.model.SlopeInterceptLine;

/**
 * A game challenge where the user is trying to match some "given" line.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class MatchingChallenge<T extends PointSlopeLine> {

    public final T answer; // the correct answer
    public final Property<T> guess; // the user's current guess
    public final ModelViewTransform mvt; // transform between model and view coordinate frames
    public final LineFactory<T> lineFactory;

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

    // A challenge that involves a line in slope-intercept form.
    public static class SlopeInterceptChallenge extends MatchingChallenge<SlopeInterceptLine> {
        public SlopeInterceptChallenge( SlopeInterceptLine answer, ModelViewTransform mvt ) {
            super( answer, SlopeInterceptLine.Y_EQUALS_X_LINE, new SlopeInterceptLineFactory(), mvt );
        }
    }

    // A challenge that involves a line in point-slope form.
    public static class PointSlopeChallenge extends MatchingChallenge<PointSlopeLine> {
        public PointSlopeChallenge( PointSlopeLine answer, ModelViewTransform mvt ) {
            super( answer, SlopeInterceptLine.Y_EQUALS_X_LINE, new PointSlopeLineFactory(), mvt );
        }
    }
}
