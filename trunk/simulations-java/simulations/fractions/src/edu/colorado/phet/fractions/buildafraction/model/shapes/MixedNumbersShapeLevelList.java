// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model.shapes;

import java.util.ArrayList;

/**
 * @author Sam Reid
 */
public class MixedNumbersShapeLevelList extends ArrayList<ShapeLevel> {
    public MixedNumbersShapeLevelList() {
        for ( int i = 0; i < 10; i++ ) { add( ShapeLevelList.level10() ); }
    }
}