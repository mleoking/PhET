// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.controller;

import fj.F;
import fj.F2;
import lombok.Data;

import edu.colorado.phet.fractionsintro.intro.model.IntroState;
import edu.colorado.phet.fractionsintro.intro.model.containerset.CellPointer;
import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;

/**
 * Controller that changes the numerator to the specified value
 *
 * @author Sam Reid
 */
public @Data class ChangeNumerator extends F2<IntroState, Integer, IntroState> {
    public IntroState f( IntroState s, Integer numerator ) {
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