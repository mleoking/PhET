// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model.shapes;

import fj.data.List;

import java.awt.Color;
import java.util.ArrayList;

import edu.colorado.phet.fractions.buildafraction.model.MixedFraction;

/**
 * This class enumerates the different levels for "Build a Mixed Fraction: Shapes"
 *
 * @author Sam Reid
 */
public class MixedNumbersShapeLevelList extends ArrayList<ShapeLevel> {
    public MixedNumbersShapeLevelList() {
        for ( int i = 0; i < 10; i++ ) { add( level1() ); }
    }

    private ShapeLevel level1() {
        return new ShapeLevel( List.list( 1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6 ),
                               List.replicate( 3, new MixedFraction( 1, 2, 3 ) ), Color.blue, ShapeType.PIE );
    }
}