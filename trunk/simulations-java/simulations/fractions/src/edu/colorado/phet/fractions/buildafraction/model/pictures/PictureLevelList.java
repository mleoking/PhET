// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model.pictures;

import fj.Equal;
import fj.data.List;

import java.util.ArrayList;

import edu.colorado.phet.fractions.buildafraction.view.DefaultLevelSelectionScreen;
import edu.colorado.phet.fractions.fractionsintro.intro.model.Fraction;

import static edu.colorado.phet.fractions.buildafraction.model.numbers.NumberLevelList.*;
import static edu.colorado.phet.fractions.buildafraction.model.pictures.PictureLevel.pictureLevel;
import static edu.colorado.phet.fractions.fractionsintro.intro.model.Fraction.fraction;
import static fj.data.List.list;

/**
 * @author Sam Reid
 */
public class PictureLevelList extends ArrayList<PictureLevel> {
    public PictureLevelList() {
        add( level1() );
        add( level2() );
        add( level3() );
        add( level4() );
        add( level5() );
    }

    /*Level 1:
    Description: Getting familiar with the user interface, only single pieces in each target
    Targets: randomize order of: 1/2, 1/3, 1/1
    Containers: exact matches for targets (1/1, 2/2, 3/3)
    Pieces: exact matches for targets (1/2, 1/3, 1/1)*/
    private PictureLevel level1() {
        return new PictureLevel( list( 1, 1, 2, 2, 3, 3 ),
                                 shuffle( list( new PictureTarget( fraction( 1, 1 ) ),
                                                new PictureTarget( fraction( 1, 2 ) ),
                                                new PictureTarget( fraction( 2, 3 ) ) ) ), DefaultLevelSelectionScreen.colors[0] );
    }

    /* Level 2:
    Description: Two pieces in each container
    Targets: choose 3 uniquely from: 2/2, 2/3, 2/4, 2/5
    Containers: exact matches for targets
    Pieces: exact matches for targets (2 each of 1/2, 1/3, 1/4, 1/5) */
    private PictureLevel level2() {
        List<Fraction> targets = list( fraction( 2, 2 ),
                                       fraction( 2, 3 ),
                                       fraction( 2, 4 ),
                                       fraction( 2, 5 ) );

        List<Fraction> selected = choose( 3, targets );
        return pictureLevel( list( 1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6 ), selected, DefaultLevelSelectionScreen.colors[1] );
    }

    /*Level 3:
      Description: Different ways of making 1/2
      Targets: 1/2, 1/2, 1/2
      Containers: randomize order of: 1/2, 1/4, 1/6
      Pieces: 3 each of 1/2, 1/4, 1/6 */
    private PictureLevel level3() {
        List<Fraction> targets = list( fraction( 1, 2 ),
                                       fraction( 1, 2 ),
                                       fraction( 1, 2 ) );

        List<Fraction> selected = choose( 3, targets );
        return pictureLevel( pad( list( 2, 4, 4, 6, 6, 6, 6 ) ), selected, DefaultLevelSelectionScreen.colors[2] );
    }

    /* Level 4:
        Description: Random level with moderate difficulty and more opportunities to match/mismatch
        Targets: Choose 3 unique fractions from {2/3, 3/4, 4/5, 3/5, 5/6} and one from {1/3, 1/4, 1/5, 1/6}
        Containers: 3 each of {3/3, 4/4, 5/5, 6/6}
        Pieces: 6 each of (1/3, 1/4, 1/5, 1/6}*/
    private PictureLevel level4() {
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
                pad( list( 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6 ) ), selected, DefaultLevelSelectionScreen.colors[3] );
    }

    /* Level 5:
Description: Numbers larger than 1
Targets: Choose 3 unique fractions from {2/3, 3/4, 4/5, 3/5, 5/6} and one from {1/3, 1/4, 1/5, 1/6}
Containers: 3 each of {3/3, 4/4, 5/5, 6/6}
Pieces: 6 each of (1/3, 1/4, 1/5, 1/6}*/
    private PictureLevel level5() {
        List<Fraction> list = list( fraction( 3, 2 ),
                                    fraction( 4, 3 ),
                                    fraction( 5, 4 ),
                                    fraction( 5, 3 ) );

        List<Fraction> selected = choose( 4, list );
        return pictureLevel(
                pad( list( 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6 ) ), selected, DefaultLevelSelectionScreen.colors[4] );
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