// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.common.model;

import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.fractionsintro.intro.view.Representation;

/**
 * Model for something with a single fraction
 *
 * @author Sam Reid
 */
public class SingleFractionModel {
    //Model for numerator and denominator
    public final IntegerProperty numerator = new IntegerProperty( 0 );
    public final IntegerProperty denominator = new IntegerProperty( 1 );

    //Fraction value computed as numerator/denominator
    public final CompositeProperty<Double> fraction = new CompositeProperty<Double>( new Function0<Double>() {
        public Double apply() {
            return (double) numerator.get() / denominator.get();
        }
    }, numerator, denominator );

    public final Property<Representation> representation = new Property<Representation>( Representation.PIE );

    public void resetAll() {
        numerator.reset();
        denominator.reset();
        representation.reset();
    }
}