package edu.colorado.phet.fractionsintro.buildafraction.model.pictures;

import fj.F;
import fj.data.List;

import java.util.ArrayList;

import edu.colorado.phet.fractionsintro.buildafraction.model.numbers.NumberLevelList;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;

import static edu.colorado.phet.fractionsintro.buildafraction.model.numbers.NumberLevelList.shuffle;
import static edu.colorado.phet.fractionsintro.buildafraction.model.pictures.PictureLevel.pictureLevel;
import static edu.colorado.phet.fractionsintro.intro.model.Fraction.fraction;
import static fj.data.List.list;

/**
 * @author Sam Reid
 */
public class PictureLevelList extends ArrayList<PictureLevel> {
    public PictureLevelList() {
        add( level1() );
        add( level2() );
        add( level3() );
    }

    /*Level 1:
    Description: Getting familiar with the user interface, only single pieces in each target
    Targets: randomize order of: 1/2, 1/3, 1/1
    Containers: exact matches for targets (1/1, 2/2, 3/3)
    Pieces: exact matches for targets (1/2, 1/3, 1/1)*/
    private PictureLevel level1() {
        return new PictureLevel( list( 1, 2, 3, 4, 5, 6 ),
                                 list( 1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6 ),
                                 shuffle( list( new PictureTarget( fraction( 1, 1 ) ),
                                                new PictureTarget( fraction( 1, 2 ) ),
                                                new PictureTarget( fraction( 2, 3 ) ) ) ) );
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

        List<Fraction> selected = NumberLevelList.choose( 3, targets );
        return pictureLevel( selected.map( new F<Fraction, Integer>() {
            @Override public Integer f( final Fraction fraction ) {
                return fraction.denominator;
            }
        } ), list( 1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6 ), selected );
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

        List<Fraction> selected = NumberLevelList.choose( 3, targets );
        return pictureLevel( shuffle( list( 2, 4, 6 ) ), list( 2, 4, 4, 6, 6, 6, 6 ), selected );
    }


}