package edu.colorado.phet.fractionsintro.buildafraction.model;

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

    public NumberLevel( final List<Integer> numbers, final List<NumberTarget> targets ) {
        this.numbers = numbers.sort( Ord.intOrd );
        this.targets = targets;
    }

    public NumberTarget getTarget( final int i ) {
        return targets.index( i );
    }

    public void resetAll() { createdFractions.reset(); }
}