// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionmatcher.model;

import fj.data.List;

import java.util.HashSet;

import edu.colorado.phet.fractionsintro.intro.model.Fraction;

import static edu.colorado.phet.fractions.common.util.FJUtils.shuffle;
import static edu.colorado.phet.fractionsintro.intro.model.Fraction.fraction;
import static fj.data.List.*;

/**
 * Description for levels in the matching game.  Pretty much the same as levels in IntroFractionLevelFactory, but with mixed numbers appearing earlier and more often.
 *
 * @author Sam Reid
 */
public class MixedFractionLevelFactory extends AbstractLevelFactory {

    private static final List<Fraction> LEVEL_1_FRACTIONS = list( fraction( 1, 3 ), fraction( 2, 3 ),
                                                                  fraction( 1, 4 ), fraction( 3, 4 ), fraction( 1, 2 ),

                                                                  //Mixed numbers
                                                                  fraction( 4, 3 ),
                                                                  fraction( 3, 2 ) );

    private static final List<Fraction> LEVEL_2_FRACTIONS = list( fraction( 1, 2 ),
                                                                  fraction( 2, 4 ),
                                                                  fraction( 3, 4 ),
                                                                  fraction( 1, 3 ),
                                                                  fraction( 2, 3 ),
                                                                  fraction( 3, 6 ),
                                                                  fraction( 2, 6 ),

                                                                  //Mixed numbers
                                                                  fraction( 3, 2 ), fraction( 4, 3 ),
                                                                  fraction( 5, 4 ), fraction( 5, 3 ),
                                                                  fraction( 6, 5 ), fraction( 6, 4 ),
                                                                  fraction( 6, 3 )
    );

    private static final List<Fraction> LEVEL_3_FRACTIONS = list( fraction( 3, 2 ), fraction( 4, 2 ),
                                                                  fraction( 4, 3 ), fraction( 6, 3 ),
                                                                  fraction( 4, 5 ),
                                                                  fraction( 7, 4 ), fraction( 5, 4 ), fraction( 6, 4 ),
                                                                  fraction( 5, 6 ), fraction( 4, 6 ), fraction( 3, 6 ), fraction( 2, 6 ), fraction( 7, 6 ),
                                                                  fraction( 3, 8 ), fraction( 4, 8 ), fraction( 5, 8 ), fraction( 6, 8 ), fraction( 7, 8 ),

                                                                  //Mixed numbers (also includes some above)
                                                                  fraction( 6, 5 ), fraction( 7, 5 ), fraction( 8, 5 ), fraction( 9, 5 ),
                                                                  fraction( 7, 6 ), fraction( 8, 6 ), fraction( 9, 6 ), fraction( 10, 6 ), fraction( 11, 6 ),
                                                                  fraction( 8, 7 ), fraction( 9, 7 ), fraction( 10, 7 ), fraction( 11, 7 ), fraction( 12, 7 ), fraction( 13, 7 ),
                                                                  fraction( 9, 8 ), fraction( 10, 8 ), fraction( 11, 8 ), fraction( 12, 8 ), fraction( 13, 8 ), fraction( 14, 8 ), fraction( 15, 8 ),
                                                                  fraction( 5, 4 ), fraction( 5, 3 ),
                                                                  fraction( 6, 5 ), fraction( 6, 4 ) );

    private static final List<Fraction> LEVEL_4_FRACTIONS = list( fraction( 13, 7 ), fraction( 13, 7 ),
                                                                  fraction( 14, 8 ),
                                                                  fraction( 9, 5 ),
                                                                  fraction( 9, 8 ),
                                                                  fraction( 8, 9 ), fraction( 6, 9 ), fraction( 4, 9 ), fraction( 3, 9 ), fraction( 2, 9 ),
                                                                  fraction( 9, 7 ),

                                                                  fraction( 6, 5 ), fraction( 7, 5 ), fraction( 8, 5 ), fraction( 9, 5 ),
                                                                  fraction( 7, 6 ), fraction( 8, 6 ), fraction( 9, 6 ), fraction( 10, 6 ), fraction( 11, 6 ),
                                                                  fraction( 8, 7 ), fraction( 9, 7 ), fraction( 10, 7 ), fraction( 11, 7 ), fraction( 12, 7 ), fraction( 13, 7 ),
                                                                  fraction( 9, 8 ), fraction( 10, 8 ), fraction( 11, 8 ), fraction( 12, 8 ), fraction( 13, 8 ), fraction( 14, 8 ), fraction( 15, 8 ),
                                                                  fraction( 10, 9 ), fraction( 11, 9 ), fraction( 13, 9 ), fraction( 14, 9 ), fraction( 15, 9 ), fraction( 16, 9 ), fraction( 17, 9 )

    );

    private static final List<Fraction> LEVEL_5_FRACTIONS = LEVEL_4_FRACTIONS;

    private static final List<Fraction> LEVEL_6_FRACTIONS = list( fraction( 9, 5 ), fraction( 8, 5 ), fraction( 7, 5 ), fraction( 6, 5 ),
                                                                  fraction( 7, 6 ), fraction( 8, 6 ), fraction( 9, 6 ),
                                                                  fraction( 9, 7 ), fraction( 10, 7 ), fraction( 13, 7 ),
                                                                  fraction( 9, 8 ), fraction( 10, 8 ), fraction( 11, 8 ), fraction( 14, 8 ),
                                                                  fraction( 4, 9 ), fraction( 6, 9 ), fraction( 8, 9 ), fraction( 10, 9 ), fraction( 11, 9 ),

                                                                  //More Mixed:
                                                                  fraction( 7, 6 ), fraction( 8, 6 ), fraction( 9, 6 ), fraction( 10, 6 ), fraction( 11, 6 ),
                                                                  fraction( 8, 7 ), fraction( 9, 7 ), fraction( 10, 7 ), fraction( 11, 7 ), fraction( 12, 7 ), fraction( 13, 7 ),
                                                                  fraction( 9, 8 ), fraction( 10, 8 ), fraction( 11, 8 ), fraction( 12, 8 ), fraction( 13, 8 ), fraction( 14, 8 ), fraction( 15, 8 ),
                                                                  fraction( 10, 9 ), fraction( 11, 9 ), fraction( 13, 9 ), fraction( 14, 9 ), fraction( 15, 9 ), fraction( 16, 9 ), fraction( 17, 9 )
    );

    private static final List<Fraction> LEVEL_7_FRACTIONS = list( fraction( 3, 2 ),
                                                                  fraction( 4, 3 ), fraction( 5, 3 ),
                                                                  fraction( 5, 4 ), fraction( 7, 4 ),
                                                                  fraction( 6, 5 ), fraction( 7, 5 ), fraction( 8, 5 ), fraction( 9, 5 ),
                                                                  fraction( 7, 6 ), fraction( 11, 6 ),

                                                                  fraction( 6, 5 ), fraction( 7, 5 ), fraction( 8, 5 ), fraction( 9, 5 ),
                                                                  fraction( 7, 6 ), fraction( 8, 6 ), fraction( 9, 6 ), fraction( 10, 6 ), fraction( 11, 6 ),
                                                                  fraction( 8, 7 ), fraction( 9, 7 ), fraction( 10, 7 ), fraction( 11, 7 ), fraction( 12, 7 ), fraction( 13, 7 ),
                                                                  fraction( 9, 8 ), fraction( 10, 8 ), fraction( 11, 8 ), fraction( 12, 8 ), fraction( 13, 8 ), fraction( 14, 8 ), fraction( 15, 8 ),
                                                                  fraction( 10, 9 ), fraction( 11, 9 ), fraction( 13, 9 ), fraction( 14, 9 ), fraction( 15, 9 ), fraction( 16, 9 ), fraction( 17, 9 )
    );

    private static final List<Fraction> LEVEL_8_FRACTIONS = list( fraction( 8, 7 ), fraction( 9, 7 ), fraction( 10, 7 ), fraction( 11, 7 ), fraction( 12, 7 ), fraction( 13, 7 ),
                                                                  fraction( 9, 8 ), fraction( 10, 8 ), fraction( 11, 8 ), fraction( 12, 8 ), fraction( 13, 8 ), fraction( 14, 8 ),
                                                                  fraction( 15, 8 ),
                                                                  fraction( 10, 9 ), fraction( 11, 9 ), fraction( 12, 9 ), fraction( 13, 9 ), fraction( 14, 9 ), fraction( 15, 9 ),
                                                                  fraction( 16, 9 ), fraction( 17, 9 ),

                                                                  fraction( 6, 5 ), fraction( 7, 5 ), fraction( 8, 5 ), fraction( 9, 5 ),
                                                                  fraction( 7, 6 ), fraction( 8, 6 ), fraction( 9, 6 ), fraction( 10, 6 ), fraction( 11, 6 ),
                                                                  fraction( 8, 7 ), fraction( 9, 7 ), fraction( 10, 7 ), fraction( 11, 7 ), fraction( 12, 7 ), fraction( 13, 7 ),
                                                                  fraction( 9, 8 ), fraction( 10, 8 ), fraction( 11, 8 ), fraction( 12, 8 ), fraction( 13, 8 ), fraction( 14, 8 ), fraction( 15, 8 ),
                                                                  fraction( 10, 9 ), fraction( 11, 9 ), fraction( 13, 9 ), fraction( 14, 9 ), fraction( 15, 9 ), fraction( 16, 9 ), fraction( 17, 9 ) );

    //Primary method for this class, creates a set of movable fractions for a level.
    public List<MovableFraction> createLevel( final int level, final List<Cell> cells ) {
        final List<Fraction> fractionList = shuffle( level == 1 ? LEVEL_1_FRACTIONS :
                                                     level == 2 ? LEVEL_2_FRACTIONS :
                                                     level == 3 ? LEVEL_3_FRACTIONS :
                                                     level == 4 ? LEVEL_4_FRACTIONS :
                                                     level == 5 ? LEVEL_5_FRACTIONS :
                                                     level == 6 ? LEVEL_6_FRACTIONS :
                                                     level == 7 ? LEVEL_7_FRACTIONS :
                                                     level == 8 ? LEVEL_8_FRACTIONS :
                                                     List.<Fraction>nil() );

        final List<Integer> numericScaleFactors = level < 5 ? single( 1 ) :
                                                  level == 5 ? list( 1, 2, 3 ) :
                                                  level == 6 ? list( 1, 4, 5 ) :
                                                  level == 7 ? list( 1, 6, 7 ) :
                                                  level == 8 ? list( 1, 2, 3, 4, 5, 6, 7, 8, 9 ) :
                                                  List.<Integer>nil();
        final List<Fraction> selectedFractions = uniqueElements( fractionList ).take( 6 );
        return generateLevel( level, cells, numericScaleFactors, selectedFractions, true );
    }

    private List<Fraction> uniqueElements( final List<Fraction> fractionList ) {
        HashSet<Fraction> set = new HashSet<Fraction>();
        for ( Fraction fraction : fractionList ) {
            set.add( fraction );
        }
        return iterableList( set );
    }
}