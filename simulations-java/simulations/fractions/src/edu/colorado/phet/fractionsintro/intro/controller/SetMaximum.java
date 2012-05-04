// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.controller;

import fj.F;
import lombok.Data;

import edu.colorado.phet.fractionsintro.intro.model.IntroState;
import edu.colorado.phet.fractionsintro.intro.model.containerset.CellPointer;
import edu.colorado.phet.fractionsintro.intro.model.containerset.ContainerSet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Slice;
import edu.colorado.phet.fractionsintro.intro.model.pieset.factories.FactorySet;

/**
 * Creates a new model by setting the maximum
 *
 * @author Sam Reid
 */
public @Data class SetMaximum extends F<IntroState, IntroState> {
    private final Integer maximum;

    @Override public IntroState f( final IntroState s ) {
        final ContainerSet c = s.containerSet.maximum( maximum );
        FactorySet factorySet = s.factorySet;
        IntroState newState = s.maximum( maximum ).
                containerSet( c ).
                fromContainerSet( c, factorySet ).
                numerator( c.numerator ).
                denominator( c.denominator );

        int lastMax = s.maximum;
        int delta = maximum - lastMax;

        //Eject any pieces in the pie that will be dropped
        int numPiecesToEject = s.containerSet.getContainer( s.containerSet.getContainers().length() - 1 ).getFilledCells().length();

        //Animate pie pieces leaving from pies that are dropped when max decreases
        //Do this by creating a new slice in the location of the deleted slice, and animating it to the bucket.
        //This is necessary since the location of pies changed when max changed.
        if ( delta < 0 && numPiecesToEject > 0 ) {
            ContainerSet csx = s.containerSet;
            for ( int i = 0; i < numPiecesToEject; i++ ) {
                final CellPointer cp = csx.getLastFullCell();

                //TODO: improve readability
                final Slice newPieSlice = factorySet.circularSliceFactory.createPieCell( s.maximum, cp.container, cp.cell, s.denominator );
                newState = newState.pieSet( newState.pieSet.withSlices( newState.pieSet.slices.cons( newPieSlice ) ).animateSliceToBucket( newPieSlice, s.randomSeed ) );

                final Slice newHorizontalSlice = factorySet.horizontalSliceFactory.createPieCell( s.maximum, cp.container, cp.cell, s.denominator );
                newState = newState.horizontalBarSet( newState.horizontalBarSet.withSlices( newState.horizontalBarSet.slices.cons( newHorizontalSlice ) ).animateSliceToBucket( newHorizontalSlice, s.randomSeed ) );

                final Slice newVerticalSlice = factorySet.verticalSliceFactory.createPieCell( s.maximum, cp.container, cp.cell, s.denominator );
                newState = newState.verticalBarSet( newState.verticalBarSet.withSlices( newState.verticalBarSet.slices.cons( newVerticalSlice ) ).animateSliceToBucket( newVerticalSlice, s.randomSeed ) );

                final Slice newWaterSlice = factorySet.waterGlassSetFactory.createPieCell( s.maximum, cp.container, cp.cell, s.denominator );
                newState = newState.waterGlassSet( newState.waterGlassSet.withSlices( newState.waterGlassSet.slices.cons( newWaterSlice ) ).animateSliceToBucket( newWaterSlice, s.randomSeed ) );

                final Slice newCakeSlice = factorySet.cakeSliceFactory.createPieCell( s.maximum, cp.container, cp.cell, s.denominator );
                newState = newState.cakeSet( newState.cakeSet.withSlices( newState.cakeSet.slices.cons( newCakeSlice ) ).animateSliceToBucket( newCakeSlice, s.randomSeed ) );

                csx = csx.toggle( cp );
            }
        }
        return newState;
    }
}