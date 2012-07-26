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

    //True if the scoring target cell should blink when the user creates a match.  Disabled on higher levels to make it more difficult.
    public final boolean flashTargetCellOnMatch = false;

    //Infer the number cards from the list of targets, using exactly what is necessary
    public NumberLevel( boolean flashTargetCellOnMatch, final List<NumberTarget> targets ) {
        this( flashTargetCellOnMatch, targets.map( new F<NumberTarget, Integer>() {
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

    public static NumberLevel numberLevelReduced( boolean flashTargetCellOnMatch, final List<NumberTarget> targets ) {
        List<Fraction> reduced = targets.map( new F<NumberTarget, Fraction>() {
            @Override public Fraction f( final NumberTarget numberTarget ) {
                return numberTarget.fraction.reduce();
            }
        } );
        List<Integer> cards = reduced.map( Fraction._numerator ).append( reduced.map( Fraction._denominator ) );
        return new NumberLevel( flashTargetCellOnMatch, cards, targets );
    }

    public NumberLevel( boolean flashTargetCellOnMatch, final List<Integer> numbers, final List<NumberTarget> targets ) {
        //TODO: are we keeping this feature?  In mid-july 2012 we were directed to avoid flashing target matches, but weren't sure if we'd keep it that way
        //this.flashTargetCellOnMatch = flashTargetCellOnMatch;
        this.numbers = numbers.sort( Ord.intOrd );
        this.targets = targets;
    }

    public NumberTarget getTarget( final int i ) {
        return targets.index( i );
    }
}