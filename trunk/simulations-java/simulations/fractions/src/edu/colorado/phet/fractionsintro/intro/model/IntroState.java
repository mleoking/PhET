// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model;

import lombok.Data;

import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.view.Representation;

/**
 * Immutable state class representing the state of the model at any given instant.
 *
 * @author Sam Reid
 */
@Data public class IntroState {
    public final ContainerSet containerSet;
    public final boolean showReduced;
    public final boolean showMixed;
    public final PieSet pieSet;
    public final PieSet horizontalBarSet;
    public final int numerator;
    public final int denominator;
    public final Representation representation;

    public IntroState() {
        denominator = 1;
        numerator = 0;
        containerSet = new ContainerSet( denominator, new Container[] { new Container( 1, new int[] { } ) } ).padAndTrim();
        showReduced = false;
        showMixed = false;
        pieSet = new PieSet();
        horizontalBarSet = new PieSet();
        representation = Representation.PIE;
    }

    //I'm not sure why Lombok didn't generate this
    public IntroState( ContainerSet containerSet, boolean showReduced, boolean showMixed, PieSet pieSet, PieSet squareSet, int numerator, int denominator, Representation representation ) {
        this.containerSet = containerSet;
        this.showReduced = showReduced;
        this.showMixed = showMixed;
        this.pieSet = pieSet;
        this.horizontalBarSet = squareSet;
        this.numerator = numerator;
        this.denominator = denominator;
        this.representation = representation;
    }

    public IntroState pieSet( PieSet pieSet ) { return new IntroState( containerSet, showReduced, showMixed, pieSet, horizontalBarSet, numerator, denominator, representation ); }

    public IntroState representation( Representation representation ) {
        return new IntroState( containerSet, showReduced, showMixed, pieSet, horizontalBarSet, numerator, denominator, representation );
    }

    public IntroState numerator( Integer numerator ) { return new IntroState( containerSet, showReduced, showMixed, pieSet, horizontalBarSet, numerator, denominator, representation ); }

    public IntroState denominator( Integer denominator ) { return new IntroState( containerSet, showReduced, showMixed, pieSet, horizontalBarSet, numerator, denominator, representation ); }

    public IntroState containerSet( ContainerSet containerSet ) {
        return new IntroState( containerSet, showReduced, showMixed, pieSet, horizontalBarSet, numerator, denominator, representation );
    }
}