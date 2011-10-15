// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.matchinggame.model;

import java.util.Random;

import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.fractions.intro.intro.model.Fraction;

/**
 * @author Sam Reid
 */
public class MatchingGameModel {
    public final ObservableList<Representation> fractionRepresentations = new ObservableList<Representation>();

    public MatchingGameModel() {
        Random random = new Random();
        for ( int i = 0; i < 5; i++ ) {
            Fraction fraction = new Fraction( random.nextInt( 11 ) + 1, random.nextInt( 11 ) + 1 );
            fractionRepresentations.add( new DecimalFraction( fraction, random.nextInt( 1000 ), random.nextInt( 600 ) ) );
            fractionRepresentations.add( new FractionRepresentation( fraction, random.nextInt( 1000 ), random.nextInt( 600 ) ) );
        }
    }
}