// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model.numbers;

import fj.data.List;
import lombok.Data;

import edu.colorado.phet.fractions.buildafraction.model.numbers.NumberLevelList.PatternMaker;
import edu.colorado.phet.fractions.fractionsintro.intro.model.Fraction;

import static edu.colorado.phet.fractions.buildafraction.model.numbers.NumberLevelList.rangeInclusive;
import static fj.data.List.list;

/**
 * Types of representations to be used for the patterns to match, including pie, grid, horizontalBars, verticalBars, flower, polygon
 *
 * @author Sam Reid
 */
public @Data abstract class RepresentationType {
    public final List<Integer> denominators;

    private static final RepresentationType pies = new RepresentationType( rangeInclusive( 1, 9 ) ) {
        @Override public PatternMaker toPattern( final Fraction fraction ) {
            return NumberLevelList.pie;
        }
    };
    private static final RepresentationType horizontalBars = new RepresentationType( rangeInclusive( 1, 9 ) ) {
        @Override public PatternMaker toPattern( final Fraction fraction ) {
            return NumberLevelList.horizontalBar;
        }
    };
    private static final RepresentationType verticalBars = new RepresentationType( rangeInclusive( 1, 9 ) ) {
        @Override public PatternMaker toPattern( final Fraction fraction ) {
            return NumberLevelList.verticalBar;
        }
    };
    private static final RepresentationType grid = new RepresentationType( list( 1, 4, 9 ) ) {
        @Override public PatternMaker toPattern( final Fraction fraction ) {
            return fraction.denominator == 1 ? NumberLevelList.grid1 :
                   fraction.denominator == 4 ? NumberLevelList.grid4 :
                   NumberLevelList.grid9;
        }
    };
    private static final RepresentationType pyramid = new RepresentationType( list( 1, 4, 9 ) ) {
        @Override public PatternMaker toPattern( final Fraction fraction ) {
            return fraction.denominator == 1 ? NumberLevelList.pyramid1 :
                   fraction.denominator == 4 ? NumberLevelList.pyramid4 :
                   NumberLevelList.pyramid9;
        }
    };
    private static final RepresentationType flower = new RepresentationType( list( 6 ) ) {
        @Override public PatternMaker toPattern( final Fraction fraction ) {
            return NumberLevelList.flower;
        }
    };
    private static final RepresentationType polygon = new RepresentationType( rangeInclusive( 3, 9 ) ) {
        @Override public PatternMaker toPattern( final Fraction fraction ) {
            return NumberLevelList.polygon;
        }
    };

    public static final List<RepresentationType> all = list( pies, horizontalBars, verticalBars, grid, pyramid, flower, polygon );

    public abstract PatternMaker toPattern( final Fraction fraction );
}