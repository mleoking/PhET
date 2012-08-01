// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model.numbers;

import fj.data.List;

import edu.colorado.phet.fractions.buildafraction.model.Level;
import edu.colorado.phet.fractions.fractionsintro.intro.model.Fraction;

import static edu.colorado.phet.fractions.buildafraction.model.numbers.NumberTarget._fraction;
import static edu.colorado.phet.fractions.fractionsintro.intro.model.Fraction.*;
import static fj.Ord.intOrd;

/**
 * Level for the build a fraction game.
 *
 * @author Sam Reid
 */
public class NumberLevel extends Level {

    //The numbers that the user can use, sorted in increasing order
    public final List<Integer> numbers;

    //The targets the user must match.
    public final List<NumberTarget> targets;

    public NumberLevel( final List<Integer> numbers, final List<NumberTarget> targets ) {
        super( targets.length() );
        this.numbers = numbers.sort( intOrd );
        this.targets = targets;
    }

    //Infer the number cards from the list of targets, using exactly what is necessary
    public NumberLevel( final List<NumberTarget> targets ) {
        this( targets.map( _fraction ).map( _numerator ).append( targets.map( _fraction ).map( _denominator ) ), targets );
    }

    public static NumberLevel numberLevelReduced( final List<NumberTarget> targets ) {
        List<Fraction> reduced = targets.map( _fraction ).map( _reduce );
        List<Integer> cards = reduced.map( _numerator ).append( reduced.map( Fraction._denominator ) );
        return new NumberLevel( cards, targets );
    }
}