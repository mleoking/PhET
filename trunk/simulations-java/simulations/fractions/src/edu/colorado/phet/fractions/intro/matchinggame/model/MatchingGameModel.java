// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.matchinggame.model;

import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.fractions.intro.intro.model.Fraction;

/**
 * @author Sam Reid
 */
public class MatchingGameModel {
    public final ObservableList<Representation> fractionRepresentations = new ObservableList<Representation>();

    public MatchingGameModel() {
        Fraction fraction = new Fraction( 9, 3 );
        fractionRepresentations.add( new DecimalFraction( fraction ) );
    }
}