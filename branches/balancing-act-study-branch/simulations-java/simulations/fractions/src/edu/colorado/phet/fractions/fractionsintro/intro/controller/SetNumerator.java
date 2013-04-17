// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionsintro.intro.controller;

import fj.F;
import lombok.EqualsAndHashCode;

import edu.colorado.phet.fractions.fractionsintro.intro.model.IntroState;
import edu.colorado.phet.fractions.fractionsintro.intro.model.containerset.CellPointer;
import edu.colorado.phet.fractions.fractionsintro.intro.model.pieset.PieSet;

/**
 * Controller that changes the numerator to the specified value
 *
 * @author Sam Reid
 */
public @EqualsAndHashCode(callSuper = false) class SetNumerator extends F<IntroState, IntroState> {
    public final int numerator;

    public SetNumerator( final int numerator ) {this.numerator = numerator;}

    @Override public IntroState f( IntroState s ) {
        int oldValue = s.numerator;
        int delta = numerator - oldValue;
        if ( delta > 0 ) {
            for ( int i = 0; i < delta; i++ ) {
                final CellPointer firstEmptyCell = s.containerSet.getFirstEmptyCell();
                final long randomSeed = s.randomSeed;
                s = s.updatePieSets( new F<PieSet, PieSet>() {
                    @Override public PieSet f( PieSet p ) {
                        return p.animateBucketSliceToPie( firstEmptyCell, randomSeed );
                    }
                } ).numerator( numerator ).nextRandomSeed();
            }
        }
        else if ( delta < 0 ) {
            for ( int i = 0; i < Math.abs( delta ); i++ ) {
                final CellPointer lastFullCell = s.containerSet.getLastFullCell();
                final long randomSeed = s.randomSeed;
                s = s.updatePieSets( new F<PieSet, PieSet>() {
                    @Override public PieSet f( PieSet p ) {
                        return p.animateSliceToBucket( lastFullCell, randomSeed );
                    }
                } ).numerator( numerator ).nextRandomSeed();
            }
        }
        else {
            //Nothing to do if delta == 0
        }
        return s;
    }
}