// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.common.model;

import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.colorado.phet.fractionsintro.intro.view.ChosenRepresentation;

/**
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

    public final ObservableProperty<Fraction> reducedFraction = new CompositeProperty<Fraction>( new Function0<Fraction>() {
        public Fraction apply() {
            return Fraction.reduced( numerator.get(), denominator.get() );
        }
    }, numerator, denominator );

    //Representations to show
    public final Property<Integer> reducedNumerator = new Property<Integer>( reducedFraction.get().numerator ) {{
        reducedFraction.addObserver( new VoidFunction1<Fraction>() {
            public void apply( Fraction fraction ) {
                set( fraction.numerator );
            }
        } );
    }};
    public final Property<Integer> reducedDenominator = new Property<Integer>( reducedFraction.get().denominator ) {{
        reducedFraction.addObserver( new VoidFunction1<Fraction>() {
            public void apply( Fraction fraction ) {
                set( fraction.denominator );
            }
        } );
    }};

    public final Property<Integer> mixedInteger = new Property<Integer>( (int) Math.floor( reducedFraction.get().getValue() ) ) {{
        reducedFraction.addObserver( new VoidFunction1<Fraction>() {
            public void apply( Fraction f ) {
                set( (int) Math.floor( f.getValue() ) );
            }
        } );
    }};
    public final Property<Integer> mixedNumerator = new Property<Integer>( 1 ) {{
        reducedFraction.addObserver( new VoidFunction1<Fraction>() {
            public void apply( Fraction f ) {
                int coeff = (int) Math.floor( f.getValue() );
                //subtract off coeff*denominator from the numerator
                set( f.numerator - coeff * f.denominator );
            }
        } );
    }};
    public final Property<Integer> mixedDenominator = new Property<Integer>( 1 ) {{
        reducedFraction.addObserver( new VoidFunction1<Fraction>() {
            public void apply( Fraction fraction ) {
                set( fraction.denominator );
            }
        } );
    }};

    public final Property<ChosenRepresentation> representation = new Property<ChosenRepresentation>( ChosenRepresentation.PIE );

    public final Clock clock = new ConstantDtClock();

    public void resetAll() {
        numerator.reset();
        denominator.reset();
        representation.reset();
    }
}