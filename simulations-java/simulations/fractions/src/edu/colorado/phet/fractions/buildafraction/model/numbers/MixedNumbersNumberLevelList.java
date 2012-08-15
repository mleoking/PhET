// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model.numbers;

import fj.data.List;

import java.awt.Color;
import java.util.ArrayList;

/**
 * List of levels used for the "mixed fractions" tab, for the levels in which number cards are used.
 *
 * @author Sam Reid
 */
public class MixedNumbersNumberLevelList extends ArrayList<NumberLevel> {
    public MixedNumbersNumberLevelList() {
        for ( int i = 0; i < 10; i++ ) { add( level1() ); }
    }

    private NumberLevel level1() {
        return new NumberLevel( List.list( 1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 5 ), List.replicate( 3, NumberTarget.target( 1, 2, 3, Color.red, NumberLevelList.pie.sequential() ) ) );
    }
}