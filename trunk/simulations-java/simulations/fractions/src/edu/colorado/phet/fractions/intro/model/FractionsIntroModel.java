// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.model;

import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function0;

/**
 * Model for the Fractions Intro sim.
 *
 * @author Sam Reid
 */
public class FractionsIntroModel {
    //Model for numerator and denominator
    public final Property<Integer> numerator = new Property<Integer>( 1 );
    public final Property<Integer> denominator = new Property<Integer>( 2 );

    //Fraction value computed as numerator/denominator
    public final CompositeProperty<Double> fraction = new CompositeProperty<Double>( new Function0<Double>() {
        public Double apply() {
            return (double) numerator.get() / denominator.get();
        }
    }, numerator, denominator );
}