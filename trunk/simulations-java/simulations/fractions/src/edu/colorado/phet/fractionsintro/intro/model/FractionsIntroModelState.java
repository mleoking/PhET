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
@Data public class FractionsIntroModelState {
    public final ContainerSet containerSet;
    public final boolean showReduced;
    public final boolean showMixed;
    public final PieSet pieSet;
    public final int numerator;
    public final int denominator;
    public final Representation representation;

    public FractionsIntroModelState() {
        denominator = 1;
        numerator = 0;
        containerSet = new ContainerSet( denominator, new Container[] { new Container( 1, new int[] { } ) } ).padAndTrim();
        showReduced = false;
        showMixed = false;
        pieSet = new PieSet();
        representation = Representation.PIE;
    }

    public FractionsIntroModelState( ContainerSet containerSet, boolean showReduced, boolean showMixed, PieSet pieSet, int numerator, int denominator, Representation representation ) {
        this.containerSet = containerSet;
        this.showReduced = showReduced;
        this.showMixed = showMixed;
        this.pieSet = pieSet;
        this.numerator = numerator;
        this.denominator = denominator;
        this.representation = representation;
    }

    public FractionsIntroModelState pieSet( PieSet pieSet ) { return new FractionsIntroModelState( containerSet, showReduced, showMixed, pieSet, numerator, denominator, representation ); }

    public FractionsIntroModelState representation( Representation representation ) {
        return new FractionsIntroModelState( containerSet, showReduced, showMixed, pieSet, numerator, denominator, representation );
    }

    public FractionsIntroModelState numerator( Integer numerator ) { return new FractionsIntroModelState( containerSet, showReduced, showMixed, pieSet, numerator, denominator, representation ); }

    public FractionsIntroModelState denominator( Integer denominator ) { return new FractionsIntroModelState( containerSet, showReduced, showMixed, pieSet, numerator, denominator, representation ); }

    public FractionsIntroModelState containerSet( ContainerSet containerSet ) {
        return new FractionsIntroModelState( containerSet, showReduced, showMixed, pieSet, numerator, denominator, representation );
    }
}