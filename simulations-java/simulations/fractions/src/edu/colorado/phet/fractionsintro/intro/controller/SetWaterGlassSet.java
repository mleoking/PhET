// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.controller;

import fj.F;
import lombok.Data;

import edu.colorado.phet.fractionsintro.intro.model.IntroState;
import edu.colorado.phet.fractionsintro.intro.model.containerset.ContainerSet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.factories.FactorySet;

/**
 * Creates a new model by setting the water glass set to the specified PieSet
 *
 * @author Sam Reid
 */
public @Data class SetWaterGlassSet extends F<IntroState, IntroState> {
    private final PieSet pieSet;

    @Override public IntroState f( final IntroState introState ) {
        final ContainerSet cs = pieSet.toContainerSet();
        FactorySet factorySet = introState.factorySet;
        //Update both the pie set and container state to match the user specified pie set
        return introState.waterGlassSet( pieSet ).
                containerSet( cs ).
                verticalBarSet( factorySet.verticalSliceFactory.fromContainerSetState( cs ) ).
                horizontalBarSet( factorySet.horizontalSliceFactory.fromContainerSetState( cs ) ).
                numerator( cs.numerator ).
                pieSet( factorySet.circularSliceFactory.fromContainerSetState( cs ) ).
                cakeSet( factorySet.cakeSliceFactory.fromContainerSetState( cs ) );
    }
}