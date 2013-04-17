// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionsintro.intro.controller;

import fj.F;
import lombok.EqualsAndHashCode;

import edu.colorado.phet.fractions.fractionsintro.intro.model.IntroState;
import edu.colorado.phet.fractions.fractionsintro.intro.model.containerset.ContainerSet;
import edu.colorado.phet.fractions.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractions.fractionsintro.intro.model.pieset.factories.FactorySet;

/**
 * Creates a new model by setting the vertical bar set to the specified PieSet
 *
 * @author Sam Reid
 */
public @EqualsAndHashCode(callSuper = false) class SetVerticalBarSet extends F<IntroState, IntroState> {
    private final PieSet pieSet;

    public SetVerticalBarSet( final PieSet pieSet ) {this.pieSet = pieSet;}

    @Override public IntroState f( final IntroState introState ) {
        final ContainerSet cs = pieSet.toContainerSet();
        FactorySet factorySet = introState.factorySet;
        //Update both the pie set and container state to match the user specified pie set
        return introState.verticalBarSet( pieSet ).
                containerSet( cs ).
                horizontalBarSet( factorySet.horizontalSliceFactory.fromContainerSetState( cs ) ).
                numerator( cs.numerator ).
                pieSet( factorySet.circularSliceFactory.fromContainerSetState( cs ) ).
                waterGlassSet( factorySet.waterGlassSetFactory.fromContainerSetState( cs ) ).
                cakeSet( factorySet.cakeSliceFactory.fromContainerSetState( cs ) );
    }
}