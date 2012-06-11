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

    //True if the scoring target cell should blink when the user creates a match.  Disabled on higher levels to make it more difficult.
    public final boolean flashTargetCellOnMatch;

    //Infer the numbers from the list of targets, using exactly what is necessary
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

    public NumberLevel( boolean flashTargetCellOnMatch, final List<Integer> numbers, final List<NumberTarget> targets ) {
        this.flashTargetCellOnMatch = flashTargetCellOnMatch;
        this.numbers = numbers.sort( Ord.intOrd );
        this.targets = targets;
    }

    public NumberTarget getTarget( final int i ) {
        return targets.index( i );
    }

    public void resetAll() { createdFractions.reset(); }
}