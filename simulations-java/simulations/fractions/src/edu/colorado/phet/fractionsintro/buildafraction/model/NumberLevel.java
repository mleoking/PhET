package edu.colorado.phet.fractionsintro.buildafraction.model;

import fj.F;
import fj.Ord;
import fj.data.List;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;

/**
 * Level for the build a fraction game.
 *
 * @author Sam Reid
 */
public class NumberLevel {

    //Fractions the user has created in the play area, which may match a target
    public final Property<List<Fraction>> createdFractions = new Property<List<Fraction>>( List.<Fraction>nil() );

    public final List<Integer> numbers;
    public final List<NumberTarget> targets;

    //Infer the numbers from the list of targets, using exactly what is necessary
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

    public NumberLevel( final List<Integer> numbers, final List<NumberTarget> targets ) {
        this.numbers = numbers.sort( Ord.intOrd );
        this.targets = targets;
    }

    public NumberTarget getTarget( final int i ) {
        return targets.index( i );
    }

    public void resetAll() { createdFractions.reset(); }
}