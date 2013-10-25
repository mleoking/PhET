// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.fractions.research_november_2013;

/**
 * Created by Sam on 10/25/13.
 */
public class MatchingGameGuess {
    public MatchingGameGuess( int points, boolean correct, int leftScaleNumerator, int leftScaleDenominator, int rightScaleNumerator, int rightScaleDenominator, String leftScaleRepresentation, String rightScaleRepresentation ) {
        this.points = points;
        this.correct = correct;
        this.leftScaleNumerator = leftScaleNumerator;
        this.leftScaleDenominator = leftScaleDenominator;
        this.rightScaleNumerator = rightScaleNumerator;
        this.rightScaleDenominator = rightScaleDenominator;
        this.leftScaleRepresentation = leftScaleRepresentation;
        this.rightScaleRepresentation = rightScaleRepresentation;
    }

    public final int points;
    public final boolean correct;
    public final int leftScaleNumerator;
    public final int leftScaleDenominator;
    public final int rightScaleNumerator;
    public final int rightScaleDenominator;
    public final String leftScaleRepresentation;
    public final String rightScaleRepresentation;
}
