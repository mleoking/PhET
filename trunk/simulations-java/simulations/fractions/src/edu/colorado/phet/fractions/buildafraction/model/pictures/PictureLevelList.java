// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model.pictures;

import fj.Equal;
import fj.F;
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
import static fj.data.List.*;

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
        add( level5() );

        //Copy level 5 for now until we have declarations 6-10
        add( level5() );
        add( level5() );
        add( level5() );
        add( level5() );
        add( level5() );
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
        return pictureLevel( straightforwardCards( selected ), selected, colors[1], booleanToShape( pies ) );
    }

    //Just the exact cards necessary to fit the selected fractions
    private List<Integer> straightforwardCards( final List<Fraction> selected ) {
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
    -- I like the idea of trying to build the same targets with constrained pieces.
    -- let's make the 2 possible targets be {1/2, 1/1}.  For 1/2, I like the constraints you have chosen, but take away the "whole" piece. For 1/1,
    again take away the "whole" piece, and constrain one of the targets so they must use two different sizes.  For instance, only enough halves and
    quarters so they must do 1 half piece and 2 quarter pieces.  Or 2 third pieces and 2 sixth pieces.*/
    private PictureLevel level4( final boolean pies ) { return random.nextBoolean() ? halfLevel4( pies ) : wholesLevel4( pies ); }

    private PictureLevel wholesLevel4( final boolean pies ) {
        return pictureLevel( list( 2, 2, 2, 3, 3, 3, 4, 4, 4, 6, 6, 6 ), replicate( 3, fraction( 1, 1 ) ), colors[3], booleanToShape( pies ) );
    }

    private PictureLevel halfLevel4( final boolean pies ) {
        return pictureLevel( list( 2, 2, 2, 3, 3, 3, 4, 4, 4, 6, 6, 6 ), replicate( 3, fraction( 1, 2 ) ), colors[3], booleanToShape( pies ) );
    }

    /*Level 5:
    --At this point I think we should switch to 4 bins for all future levels
    -SR: Let's make this one have 3 challenges so it can fit on the page with 3 stars per level
    -- Let's use pie shapes for this level
    - numerator able to range from 1-8, and denominator able to range from 1-8, with the number less than or equal to 1
    - all pieces available to fulfill targets in the most straightforward way (so for instance if 3/8 appears there will 3 1/8 pieces)*/
    private PictureLevel level5() {
        List<Integer> values = range( 1, 9 );
        List<Integer> denominators = choose( 3, values );
        List<Fraction> selected = denominators.map( new F<Integer, Fraction>() {
            @Override public Fraction f( final Integer denominator ) {
                return new Fraction( chooseOne( range( 1, denominator + 1 ) ), denominator );
            }
        } );
        return pictureLevel( straightforwardCards( selected ), selected, colors[4], ShapeType.PIE );
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