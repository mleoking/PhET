package edu.colorado.phet.fractionsintro.buildafraction.model;

import java.util.ArrayList;

import edu.colorado.phet.fractionsintro.intro.model.Fraction;

import static fj.data.List.list;

/**
 * @author Sam Reid
 */
public class PictureLevelList extends ArrayList<PictureLevel> {
    public PictureLevelList() {
        add( pictureLevel0() );
    }

    private PictureLevel pictureLevel0() {
        return new PictureLevel( list( 1, 2, 3, 4, 5, 6 ), list( new PictureTarget( new Fraction( 1, 1 ) ), new PictureTarget( new Fraction( 1, 2 ) ), new PictureTarget( new Fraction( 2, 3 ) ) ) );
    }

}