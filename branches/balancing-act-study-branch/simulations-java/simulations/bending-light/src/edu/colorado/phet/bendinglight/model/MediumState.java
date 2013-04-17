// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.model;

import static edu.colorado.phet.bendinglight.model.BendingLightModel.WAVELENGTH_RED;

/**
 * Immutable state for a medium, with the name and dispersion function, and flags for "mystery" and "custom".
 *
 * @author Sam Reid
 */
public class MediumState {
    public final String name;
    public final DispersionFunction dispersionFunction;
    public final boolean mystery;
    public final boolean custom;

    public MediumState( String name, double indexForRed ) {
        this( name, indexForRed, false );
    }

    public MediumState( String name, double indexForRed, boolean mystery ) {
        this( name, indexForRed, mystery, false );
    }

    public MediumState( String name, double indexForRed, boolean mystery, boolean custom ) {
        this( name, new DispersionFunction( indexForRed ), mystery, custom );
    }

    public MediumState( String name, DispersionFunction dispersionFunction, boolean mystery, boolean custom ) {
        this.name = name;
        this.dispersionFunction = dispersionFunction;
        this.mystery = mystery;
        this.custom = custom;
    }

    @Override public String toString() {
        return name;
    }

    public double getIndexOfRefractionForRedLight() {
        return dispersionFunction.getIndexOfRefraction( WAVELENGTH_RED );
    }
}