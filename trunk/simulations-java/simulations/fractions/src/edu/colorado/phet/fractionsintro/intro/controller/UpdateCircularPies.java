// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.controller;

import fj.F2;
import lombok.Data;

import edu.colorado.phet.fractionsintro.intro.model.IntroState;
import edu.colorado.phet.fractionsintro.intro.model.containerset.ContainerSet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.factories.FactorySet;

/**
 * Controller that updates the model when the user drags circular pies.
 *
 * @author Sam Reid
 */
public @Data class UpdateCircularPies extends F2<IntroState, PieSet, IntroState> {

    private final FactorySet factorySet;

    public UpdateCircularPies( FactorySet factorySet ) {
        this.factorySet = factorySet;
    }

    public IntroState f( IntroState s, PieSet pieSet ) {
        final ContainerSet c = pieSet.toContainerSet();
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