package edu.colorado.phet.fractionsintro.buildafraction.model.pictures;

import fj.F;
import fj.data.List;

import java.util.ArrayList;

import edu.colorado.phet.fractionsintro.buildafraction.model.numbers.NumberLevelList;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;

import static fj.data.List.list;

/**
 * @author Sam Reid
 */
public class PictureLevelList extends ArrayList<PictureLevel> {
    public PictureLevelList() {
        add( pictureLevel0() );
        add( pictureLevel1() );
    }

    private PictureLevel pictureLevel0() {
        return new PictureLevel( list( 1, 2, 3, 4, 5, 6 ),
                                 list( 1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6 ),
                                 list( new PictureTarget( new Fraction( 1, 1 ) ),
                                       new PictureTarget( new Fraction( 1, 2 ) ),
                                       new PictureTarget( new Fraction( 2, 3 ) ) ) );
    }

    private PictureLevel pictureLevel1() {
        List<Fraction> targets = list( Fraction.fraction( 2, 2 ),
                                       Fraction.fraction( 2, 3 ),
                                       Fraction.fraction( 2, 4 ),
                                       Fraction.fraction( 2, 5 ) );

        List<Fraction> selected = NumberLevelList.choose( 3, targets );
        return new PictureLevel( selected.map( new F<Fraction, Integer>() {
            @Override public Integer f( final Fraction fraction ) {
                return fraction.denominator;
            }
        } ),
                                 list( 1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6 ),
                                 selected.map( new F<Fraction, PictureTarget>() {
                                     @Override public PictureTarget f( final Fraction fraction ) {
                                         return new PictureTarget( fraction );
                                     }
                                 } )
        );
    }


}