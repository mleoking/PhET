// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionsintro.intro.model.pieset.factories;

/**
 * Manages factories for all representations for one set.
 *
 * @author Sam Reid
 */
public class FactorySet {
    public final SliceFactory circularSliceFactory;
    public final SliceFactory horizontalSliceFactory;
    public final SliceFactory verticalSliceFactory;
    public final SliceFactory waterGlassSetFactory;
    public final SliceFactory cakeSliceFactory;

    public FactorySet( SliceFactory circularSliceFactory, SliceFactory horizontalSliceFactory, SliceFactory verticalSliceFactory, SliceFactory waterGlassSetFactory, SliceFactory cakeSliceFactory ) {
        this.circularSliceFactory = circularSliceFactory;
        this.horizontalSliceFactory = horizontalSliceFactory;
        this.verticalSliceFactory = verticalSliceFactory;
        this.waterGlassSetFactory = waterGlassSetFactory;
        this.cakeSliceFactory = cakeSliceFactory;
    }
}