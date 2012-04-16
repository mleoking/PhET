// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.controller;

import fj.F;

import edu.colorado.phet.fractionsintro.intro.model.IntroState;
import edu.colorado.phet.fractionsintro.intro.model.containerset.ContainerSet;

/**
 * Creates a new model by setting the denominator
 *
 * @author Sam Reid
 */
public class SetDenominator extends F<IntroState, IntroState> {
    private final Integer denominator;

    public SetDenominator( final Integer denominator ) {
        this.denominator = denominator;
    }

    @Override public IntroState f( final IntroState s ) {
        //create a new container set
        ContainerSet c = s.containerSet.update( s.maximum, denominator );
        return s.containerSet( c ).denominator( denominator ).fromContainerSet( c, s.factorySet );
    }
}