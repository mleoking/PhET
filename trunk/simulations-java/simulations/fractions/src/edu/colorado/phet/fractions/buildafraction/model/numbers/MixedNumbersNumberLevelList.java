// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model.numbers;

import fj.data.List;

import java.awt.Color;
import java.util.ArrayList;

import static edu.colorado.phet.fractions.buildafraction.model.numbers.NumberLevelList.pie;
import static edu.colorado.phet.fractions.buildafraction.model.numbers.NumberLevelList.shuffle;
import static fj.data.List.list;

/**
 * List of levels used for the "mixed fractions" tab, for the levels in which number cards are used.
 *
 * @author Sam Reid
 */
public class MixedNumbersNumberLevelList extends ArrayList<NumberLevel> {
    public MixedNumbersNumberLevelList() {
        add( level1() );
        while ( size() < 10 ) { add( levelX() ); }
    }

    /*Level 1:
    -- Circles as targets
    -- {1:1/2, 2:1/2, 3:1/4} as the challenges
    --just enough cards to complete targets
    -- as before refreshing will randomly reorder, recolor
    */
    private NumberLevel level1() {
        RandomColors3 colors = new RandomColors3();
        return new NumberLevel( shuffle( list( NumberTarget.target( 1, 1, 2, colors.next(), pie.sequential() ),
                                               NumberTarget.target( 2, 1, 2, colors.next(), pie.sequential() ),
                                               NumberTarget.target( 3, 1, 4, colors.next(), pie.sequential() ) ) ) );
    }

    private NumberLevel levelX() {
        return new NumberLevel( List.list( 1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 5 ), List.replicate( 3, NumberTarget.target( 1, 2, 3, Color.red, pie.sequential() ) ) );
    }
}