// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model;

import lombok.Data;

import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.view.Representation;

import static edu.colorado.phet.fractionsintro.intro.model.pieset.CircularSliceFactory.CircularSliceFactory;
import static edu.colorado.phet.fractionsintro.intro.model.pieset.HorizontalSliceFactory.HorizontalSliceFactory;
import static edu.colorado.phet.fractionsintro.intro.model.pieset.VerticalSliceFactory.VerticalSliceFactory;

/**
 * Immutable state class representing the state of the model at any given instant.  Managed by FractionsIntroModel.
 *
 * @author Sam Reid
 */
@Data public class IntroState {
    public final ContainerSet containerSet;
    public final boolean showReduced;
    public final boolean showMixed;
    public final PieSet pieSet;
    public final PieSet horizontalBarSet;
    public final PieSet verticalBarSet;
    public final int numerator;
    public final int denominator;
    public final Representation representation;
    public final int maximum;

    public IntroState() {
        denominator = 1;
        numerator = 0;
        containerSet = new ContainerSet( denominator, new Container[] { new Container( 1, new int[] { } ) } ).padAndTrim();
        showReduced = false;
        showMixed = false;
        maximum = 1;
        pieSet = new PieSet( maximum, CircularSliceFactory );
        horizontalBarSet = new PieSet( maximum, HorizontalSliceFactory );
        verticalBarSet = new PieSet( maximum, VerticalSliceFactory );
        representation = Representation.PIE;
    }

    //I'm not sure why Lombok didn't generate this
    public IntroState( ContainerSet containerSet, boolean showReduced, boolean showMixed, PieSet pieSet, PieSet squareSet, PieSet verticalBarSet, int numerator, int denominator, Representation representation, int maximum ) {
        this.containerSet = containerSet;
        this.showReduced = showReduced;
        this.showMixed = showMixed;
        this.pieSet = pieSet;
        this.horizontalBarSet = squareSet;
        this.verticalBarSet = verticalBarSet;
        this.numerator = numerator;
        this.denominator = denominator;
        this.representation = representation;
        this.maximum = maximum;
    }

    public IntroState pieSet( PieSet pieSet ) { return new IntroState( containerSet, showReduced, showMixed, pieSet, horizontalBarSet, verticalBarSet, numerator, denominator, representation, maximum ); }

    public IntroState representation( Representation representation ) { return new IntroState( containerSet, showReduced, showMixed, pieSet, horizontalBarSet, verticalBarSet, numerator, denominator, representation, maximum ); }

    public IntroState numerator( Integer numerator ) { return new IntroState( containerSet, showReduced, showMixed, pieSet, horizontalBarSet, verticalBarSet, numerator, denominator, representation, maximum ); }

    public IntroState denominator( Integer denominator ) { return new IntroState( containerSet, showReduced, showMixed, pieSet, horizontalBarSet, verticalBarSet, numerator, denominator, representation, maximum ); }

    public IntroState containerSet( ContainerSet containerSet ) { return new IntroState( containerSet, showReduced, showMixed, pieSet, horizontalBarSet, verticalBarSet, numerator, denominator, representation, maximum ); }

    public IntroState horizontalBarSet( PieSet horizontalBarSet ) {return new IntroState( containerSet, showReduced, showMixed, pieSet, horizontalBarSet, verticalBarSet, numerator, denominator, representation, maximum );}

    public IntroState verticalBarSet( PieSet verticalBarSet ) {return new IntroState( containerSet, showReduced, showMixed, pieSet, horizontalBarSet, verticalBarSet, numerator, denominator, representation, maximum );}

    public IntroState maximum( int maximum ) {return new IntroState( containerSet, showReduced, showMixed, pieSet, horizontalBarSet, verticalBarSet, numerator, denominator, representation, maximum );}
}