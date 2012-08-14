// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model.numbers;

import java.util.ArrayList;

/**
 * @author Sam Reid
 */
public class MixedNumbersNumberLevelList extends ArrayList<NumberLevel> {
    public MixedNumbersNumberLevelList() {
        for ( int i = 0; i < 10; i++ ) { add( NumberLevelList.level10() ); }
    }
}