// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.model;

import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.fractions.intro.Fraction;

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

    public final ObservableProperty<Fraction> reducedFraction = new CompositeProperty<Fraction>( new Function0<Fraction>() {
        public Fraction apply() {
            return new Fraction( numerator.get(), denominator.get() );
        }
    }, numerator, denominator );

    //Representations to show
    public final Property<Boolean> reducedFractionRepresentation = new Property<Boolean>( false );
    public final Property<Boolean> decimalRepresentation = new Property<Boolean>( false );
    public final Property<Boolean> wordsRepresentation = new Property<Boolean>( false );
    public final Property<Boolean> mixedRepresentation = new Property<Boolean>( false );
    public final Property<Boolean> cakeRepresentation = new Property<Boolean>( false );
    public final Property<Boolean> breadRepresentation = new Property<Boolean>( false );
    public final Property<Boolean> booksRepresentation = new Property<Boolean>( false );
}