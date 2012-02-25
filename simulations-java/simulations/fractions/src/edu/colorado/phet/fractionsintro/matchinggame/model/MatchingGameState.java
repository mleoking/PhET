// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

import fj.data.List;
import lombok.Data;

/**
 * Immutable class for the state of the matching game model.
 *
 * @author Sam Reid
 */
@Data public class MatchingGameState {
    public final List<Fraction> fractions;

    public static MatchingGameState initialState() {
        return new MatchingGameState( List.single( new Fraction() ) );
    }
}