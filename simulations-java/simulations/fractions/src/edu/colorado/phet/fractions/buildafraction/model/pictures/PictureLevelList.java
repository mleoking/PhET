// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model.pictures;

import fj.Equal;
import fj.Ord;
import fj.data.List;

import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.fractions.buildafraction.model.Distribution;
import edu.colorado.phet.fractions.fractionsintro.intro.model.Fraction;

import static edu.colorado.phet.fractions.buildafraction.model.numbers.NumberLevelList.*;
import static edu.colorado.phet.fractions.buildafraction.model.pictures.PictureLevel.pictureLevel;
import static edu.colorado.phet.fractions.buildafraction.view.LevelSelectionNode.colors;
import static edu.colorado.phet.fractions.fractionsintro.intro.model.Fraction._numerator;
import static edu.colorado.phet.fractions.fractionsintro.intro.model.Fraction.fraction;
import static fj.data.List.list;
import static fj.data.List.nil;

/**
 * @author Sam Reid
 */
public class PictureLevelList extends ArrayList<PictureLevel> {
    private final Random random = new Random();

    public PictureLevelList() {
        boolean level1Pies = random.nextBoolean();
        boolean level3Pies = random.nextBoolean();
        boolean level5Pies = random.nextBoolean();
        boolean level7Pies = random.nextBoolean();
        boolean level9Pies = random.nextBoolean();
        add( level1( level1Pies ) );
        add( level2( !level1Pies ) );
        add( level3( level3Pies ) );
        add( level4( !level3Pies ) );
        add( level5( level5Pies ) );

        //Copy level 5 for now until we have declarations 6-10
        add( level5( !level5Pies ) );
        add( level5( level7Pies ) );
        add( level5( !level7Pies ) );
        add( level5( level9Pies ) );
        add( level5( !level9Pies ) );
    }

    /* Level 1:
    --I like the idea of starting with this simple set of fractions
    - Can we make two draws, one target should be from the set  {1/1, 2/2, 3/3} and the second draw for the next two targets from the set {1/2, 1/3, 2/3} */
    private PictureLevel level1( final boolean pies ) {
        Distribution<Fraction> set1 = new Distribution<Fraction>( list( fraction( 1, 1 ),
                                                                        fraction( 2, 2 ),
                                                                        fraction( 3, 3 ) ) );
        Distribution<Fraction> set2 = new Distribution<Fraction>( list( fraction( 1, 2 ),
                                                                        fraction( 1, 3 ),
                                                                        fraction( 2, 3 ) ) );
        Fraction a = set1.draw();
        Fraction b = set2.drawAndRemove();
        Fraction c = set2.draw();
        return new PictureLevel( list( 1, 1, 2, 2, 3, 3 ),
                                 shuffle( list( new PictureTarget( a ),
                                                new PictureTarget( b ),
                                                new PictureTarget( c ) ) ), colors[0], booleanToShape( pies ) );
    }

    private ShapeType booleanToShape( final boolean pies ) {return pies ? ShapeType.PIE : ShapeType.BAR;}

    /* Level 2:
   --Here I would begin choosing from a distribution of fractions ranging from 1/2 to 4/5.
   As in the numerator could be 1, 2, 3, or 4 and the denominator could be 2, 3, 4, or 5 with the stipulation that the fraction is always less than 1.
   So I would remove the "wholes" from the shapes pile.
    */
    private PictureLevel level2( final boolean pies ) {
        List<Fraction> targets = list( fraction( 1, 2 ),
                                       fraction( 1, 3 ),
                                       fraction( 1, 4 ),
                                       fraction( 1, 5 ),
                                       fraction( 2, 3 ),
                                       fraction( 2, 4 ),
                                       fraction( 2, 5 ),
                                       fraction( 3, 4 ),
                                       fraction( 3, 5 ),
                                       fraction( 4, 5 ) );

        List<Fraction> selected = choose( 3, targets );

        //Use same number for each card type
        return pictureLevel( minimalNumberCards( selected ), selected, colors[1], booleanToShape( pies ) );
    }

    //Just the exact cards necessary to fit the selected fractions
    private List<Integer> minimalNumberCards( final List<Fraction> selected ) {
        List<Integer> cards = nil();
        for ( Fraction fraction : selected ) {
            for ( int i = 0; i < fraction.numerator; i++ ) {
                cards = cards.snoc( fraction.denominator );
            }
        }
        return cards;
    }

    private List<Integer> createCardsSameNumberEachType( final List<Fraction> selected ) {
        Integer maxNumerator = selected.map( _numerator ).maximum( Ord.intOrd );
        List<Integer> cards = nil();
        for ( Fraction fraction : selected ) {
            for ( int i = 0; i < maxNumerator; i++ ) {
                cards = cards.snoc( fraction.denominator );
            }
        }
        return cards;
    }

    /* Level 3:
--Like level 2, but now fractions ranging from 1/1 to 6/6, and with "whole" pieces available.
--Number of pieces of each fraction allowing for multiple solutions*/
    private PictureLevel level3( final boolean pies ) {
        List<Fraction> targets = list( fraction( 1, 1 ), fraction( 1, 2 ), fraction( 1, 3 ), fraction( 1, 4 ), fraction( 1, 5 ), fraction( 1, 6 ),
                                       fraction( 2, 2 ), fraction( 2, 3 ), fraction( 2, 4 ), fraction( 2, 5 ), fraction( 2, 6 ),
                                       fraction( 3, 3 ), fraction( 3, 4 ), fraction( 3, 5 ), fraction( 3, 6 ),
                                       fraction( 4, 4 ), fraction( 4, 5 ), fraction( 4, 6 ),
                                       fraction( 5, 5 ), fraction( 5, 6 ),
                                       fraction( 6, 6 ) );

        List<Fraction> selected = choose( 3, targets );
        return pictureLevel( createCardsSameNumberEachType( selected ), selected, colors[2], booleanToShape( pies ) );
    }

    /* Level 4:
        Description: Random level with moderate difficulty and more opportunities to match/mismatch
        Targets: Choose 3 unique fractions from {2/3, 3/4, 4/5, 3/5, 5/6} and one from {1/3, 1/4, 1/5, 1/6}
        Containers: 3 each of {3/3, 4/4, 5/5, 6/6}
        Pieces: 6 each of (1/3, 1/4, 1/5, 1/6}*/
    private PictureLevel level4( final boolean b ) {
        List<Fraction> largeList = list( fraction( 2, 3 ),
                                         fraction( 3, 4 ),
                                         fraction( 4, 5 ),
                                         fraction( 5, 6 ) );
        List<Fraction> smallList = list( fraction( 1, 3 ),
                                         fraction( 1, 4 ),
                                         fraction( 1, 5 ),
                                         fraction( 1, 6 ) );

        List<Fraction> selected = shuffle( choose( 3, largeList ).snoc( chooseOne( smallList ) ) );
        return pictureLevel(
                pad( list( 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6 ) ), selected, colors[3], ShapeType.PIE );
    }

    /* Level 5:
Description: Numbers larger than 1
Targets: Choose 3 unique fractions from {2/3, 3/4, 4/5, 3/5, 5/6} and one from {1/3, 1/4, 1/5, 1/6}
Containers: 3 each of {3/3, 4/4, 5/5, 6/6}
Pieces: 6 each of (1/3, 1/4, 1/5, 1/6}*/
    private PictureLevel level5( final boolean level5Pies ) {
        List<Fraction> list = list( fraction( 3, 2 ),
                                    fraction( 4, 3 ),
                                    fraction( 5, 4 ),
                                    fraction( 5, 3 ) );

        List<Fraction> selected = choose( 4, list );
        return pictureLevel(
                pad( list( 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6 ) ), selected, colors[4], ShapeType.PIE );
    }

    private List<Integer> pad( List<Integer> list ) {
        for ( int i = 1; i <= 6; i++ ) {
            list = pad( list, i );
        }
        return list;
    }

    //Make sure at least one of each number 1-6
    private List<Integer> pad( final List<Integer> list, int value ) {
        return list.elementIndex( Equal.intEqual, value ).isNone() ? list.snoc( value ) : list;
    }
}