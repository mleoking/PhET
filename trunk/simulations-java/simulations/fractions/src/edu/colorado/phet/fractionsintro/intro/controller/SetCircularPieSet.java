// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.controller;

import fj.F;
import lombok.Data;

import edu.colorado.phet.fractionsintro.intro.model.IntroState;
import edu.colorado.phet.fractionsintro.intro.model.containerset.ContainerSet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.factories.FactorySet;

/**
 * Creates a new model by setting the circular pie set to the specified PieSet
 *
 * @author Sam Reid
 */
public @Data class SetCircularPieSet extends F<IntroState, IntroState> {
    private final PieSet pieSet;

    @Override public IntroState f( final IntroState s ) {
        final ContainerSet c = pieSet.toContainerSet();
        FactorySet factorySet = s.factorySet;
        //Update both the pie set and container state to match the user specified pie set
        return s.pieSet( pieSet ).
                containerSet( c ).
                numerator( c.numerator ).
                horizontalBarSet( factorySet.horizontalSliceFactory.fromContainerSetState( c ) ).
                verticalBarSet( factorySet.verticalSliceFactory.fromContainerSetState( c ) ).
                waterGlassSet( factorySet.waterGlassSetFactory.fromContainerSetState( c ) ).
                cakeSet( factorySet.cakeSliceFactory.fromContainerSetState( c ) );
    }
}