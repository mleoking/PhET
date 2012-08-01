// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model.numbers;

import fj.F;
import fj.Ord;
import fj.data.List;

import edu.colorado.phet.fractions.buildafraction.model.Level;
import edu.colorado.phet.fractions.fractionsintro.intro.model.Fraction;

/**
 * Level for the build a fraction game.
 *
 * @author Sam Reid
 */
public class NumberLevel extends Level {

    public final List<Integer> numbers;
    public final List<NumberTarget> targets;

    public NumberLevel( final List<Integer> numbers, final List<NumberTarget> targets ) {
        super( targets.length() );
        this.numbers = numbers.sort( Ord.intOrd );
        this.targets = targets;
    }

    //Infer the number cards from the list of targets, using exactly what is necessary
    public NumberLevel( final List<NumberTarget> targets ) {
        this( targets.map( new F<NumberTarget, Integer>() {
            @Override public Integer f( final NumberTarget numberTarget ) {
                return numberTarget.fraction.numerator;
            }
        } ).
                append( targets.map( new F<NumberTarget, Integer>() {
                    @Override public Integer f( final NumberTarget numberTarget ) {
                        return numberTarget.fraction.denominator;
                    }
                } ) ), targets );
    }

    public static NumberLevel numberLevelReduced( final List<NumberTarget> targets ) {
        List<Fraction> reduced = targets.map( new F<NumberTarget, Fraction>() {
            @Override public Fraction f( final NumberTarget numberTarget ) {
                return numberTarget.fraction.reduce();
            }
        } );
        List<Integer> cards = reduced.map( Fraction._numerator ).append( reduced.map( Fraction._denominator ) );
        return new NumberLevel( cards, targets );
    }
}