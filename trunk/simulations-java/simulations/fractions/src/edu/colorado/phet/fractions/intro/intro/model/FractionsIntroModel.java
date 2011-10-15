// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
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

    public final ObservableProperty<Fraction> reducedFraction = new CompositeProperty<Fraction>( new Function0<Fraction>() {
        public Fraction apply() {
            return Fraction.reduced( numerator.get(), denominator.get() );
        }
    }, numerator, denominator );

    //Representations to show
    public final ArrayList<Representation> representations = new ArrayList<Representation>();
    public final Representation reducedFractionRepresentation = add( "Reduced fraction" );
    public final Representation decimalRepresentation = add( "Decimal" );
    public final Representation wordsRepresentation = add( "Words" );
    public final Representation percentRepresentation = add( "Percent" );
    public final Representation piesRepresentation = add( "Pies" );
    public final Representation mixedRepresentation = add( "Mixed" );
    public final Representation cakeRepresentation = add( "Cake" );
    public final Representation breadRepresentation = add( "Bread" );
    public final Representation booksRepresentation = add( "Books" );

    private Representation add( String representation ) {
        Representation rep = new Representation( representation );
        representations.add( rep );
        return rep;
    }
}