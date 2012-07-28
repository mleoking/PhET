// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model.pictures;

import fj.Equal;
import fj.F;
import fj.Ord;
import fj.P2;
import fj.data.List;
import lombok.Data;

import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

import edu.colorado.phet.common.phetcommon.math.Permutation;
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
        boolean level6Pies = random.nextBoolean();
        boolean level8Pies = random.nextBoolean();
        boolean level9Pies = random.nextBoolean();
        add( level1( level1Pies ) );
        add( level2( !level1Pies ) );
        add( level3( level3Pies ) );
        add( level4( !level3Pies ) );
        add( level5() );
        add( level6() );
        add( level7() );
        add( level6() );
        add( level6() );
        add( level6() );
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

    public static final @Data class ShapesAndTargets {
        public final List<Integer> cards;
        public final List<Fraction> targets;
    }

    public static @Data class Coefficients {
        public final int n;
        public final int m;
    }

    /*Level 6:
--Lets try a challenge for this level where all targets are made from only 2 stacks of the same size pieces
--So for instance we give a stack of thirds and a stack of halves, and {2/3, 2/4, 5/6, 1/1} are the target fractions, but we constrain the
pieces so that some fractions must be made in "interesting" ways.  2/3 could just be made with 2 third pieces, but 5/6 would need to be made of
a 1/2 and a 1/3.
-- It seems the sets that would work well for pieces would be, {1/2, 1/3}, {1/2, 1/4}, {1/3, 1/4}, {1/2, 1/6}, {1/3, 1/6}, {1/4, 1/8}, {1/2, 1/8}
--the constraint should be such that only enough pieces exist to complete the targets.

    But keep the values less than 1 by trial and error.*/
    private PictureLevel level6() {
        PictureLevel level = sampleLevel6();
        if ( !level.hasValuesGreaterThanOne() ) {
            return level;
        }
        else {
            return level6();
        }
    }

    private PictureLevel sampleLevel6() {//let's implement this my making each solution as na + mb, where a and b are the fractions from pairs above
        final List<Integer> cardSizes = chooseOne( list( list( 2, 3 ), list( 2, 4 ), list( 3, 4 ), list( 2, 6 ), list( 3, 6 ), list( 4, 8 ), list( 2, 8 ) ) );

        List<Coefficients> nmPairs = list( new Coefficients( 0, 1 ), new Coefficients( 1, 0 ),
                                           new Coefficients( 1, 1 ), new Coefficients( 1, 2 ), new Coefficients( 2, 1 ), new Coefficients( 2, 2 ),
                                           new Coefficients( 3, 1 ), new Coefficients( 1, 3 ) );

        List<Coefficients> selectedCoefficients = choose( 4, nmPairs );

        List<Fraction> selectedTargets = selectedCoefficients.map( new F<Coefficients, Fraction>() {
            @Override public Fraction f( final Coefficients c ) {
                Fraction a = new Fraction( c.n, cardSizes.index( 0 ) );
                Fraction b = new Fraction( c.m, cardSizes.index( 1 ) );
                return a.plus( b ).reduce();
            }
        } );

        List<Integer> selectedShapes = nil();
        for ( Coefficients c : selectedCoefficients ) {
            selectedShapes = selectedShapes.append( replicate( c.n, cardSizes.index( 0 ) ) );
            selectedShapes = selectedShapes.append( replicate( c.m, cardSizes.index( 1 ) ) );
        }

        ShapeType x = random.nextBoolean() ? ShapeType.PIE : ShapeType.BAR;
        return pictureLevel( selectedShapes, selectedTargets, colors[5], selectedTargets.exists( new F<Fraction, Boolean>() {
            @Override public Boolean f( final Fraction fraction ) {
                return fraction.denominator >= 7;
            }
        } ) ? x : ShapeType.BAR );
    }

    /*Level 7:
--Top two targets, and bottom 2 targets are equivalent but still numbers less than 1
-- A built in check to draw a different fraction for the top 2 and the bottom 2
-- Possible fractions sets from which to draw 2 each {1/2, 1/3, 2/3, 1/4, 3/4, 5/6, 3/8, 5/8}
-- Shape pieces constrained so that for instance if 1/2 and 1/2 appears for the top targets, a 1/2 piece might be available but the other one
will need to be made with a 1/4 and 1/4, or a 1/3 and a 1/6 or such.
-- If 3/8 or 5/8 are drawn circles should be used, if not circles or tiles will work fine*/
    public PictureLevel level7() {
        List<Fraction> available = list( new Fraction( 1, 2 ), new Fraction( 1, 3 ), new Fraction( 2, 3 ), new Fraction( 1, 4 ),
                                         new Fraction( 3, 4 ), new Fraction( 5, 6 ), new Fraction( 3, 8 ), new Fraction( 5, 8 ) );

        P2<Fraction, Fraction> selected = chooseTwo( available );
        List<Fraction> targets = list( selected._1(), selected._1(), selected._2(), selected._2() );

        //Could find all solutions to an underconstrained system.
        //But we just need any solution that is different from previous.

        //Use JAMA to solve matrix system?
        Fraction result = selected._1();

        //result = a/1 + b/2 + c/3 + d/4 + e/5 + f/6 + g/7 + h/8
        //constrain variables to be integer, and several of them to be zero.

        //also a = {0 or 1} b= {0,1,2} c = {0,1,2,3}
        getCoefficientSets( result );
        for ( Fraction a : available ) {
            System.out.println( a + " => " + getCoefficientSets( a ) );
        }
        return null;
    }

    private ArrayList<List<Integer>> lookupCoefficientSets( final Fraction fraction ) {
        if ( fraction.reduce().equals( new Fraction( 1, 2 ) ) ) { return parse( "[<0,0,0,0,0,0,0,4>, <0,0,0,0,0,3,0,0>, <0,0,0,1,0,0,0,2>, <0,0,0,2,0,0,0,0>, <0,0,1,0,0,1,0,0>, <0,1,0,0,0,0,0,0>]" ); }
        if ( fraction.reduce().equals( new Fraction( 1, 3 ) ) ) { return parse( "[<0,0,0,0,0,2,0,0>, <0,0,1,0,0,0,0,0>]" ); }
        if ( fraction.reduce().equals( new Fraction( 2, 3 ) ) ) { return parse( "[<0,0,0,0,0,1,0,4>, <0,0,0,0,0,4,0,0>, <0,0,0,1,0,1,0,2>, <0,0,0,2,0,1,0,0>, <0,0,1,0,0,2,0,0>, <0,0,2,0,0,0,0,0>, <0,1,0,0,0,1,0,0>]" ); }
        if ( fraction.reduce().equals( new Fraction( 1, 4 ) ) ) { return parse( "[<0,0,0,0,0,0,0,2>, <0,0,0,1,0,0,0,0>]" ); }
        if ( fraction.reduce().equals( new Fraction( 3, 4 ) ) ) { return parse( "[<0,0,0,0,0,0,0,6>, <0,0,0,0,0,3,0,2>, <0,0,0,1,0,0,0,4>, <0,0,0,1,0,3,0,0>, <0,0,0,2,0,0,0,2>, <0,0,0,3,0,0,0,0>, <0,0,1,0,0,1,0,2>, <0,0,1,1,0,1,0,0>, <0,1,0,0,0,0,0,2>, <0,1,0,1,0,0,0,0>]" ); }
        if ( fraction.reduce().equals( new Fraction( 5, 6 ) ) ) { return parse( "[<0,0,0,0,0,2,0,4>, <0,0,0,0,0,5,0,0>, <0,0,0,1,0,2,0,2>, <0,0,0,2,0,2,0,0>, <0,0,1,0,0,0,0,4>, <0,0,1,0,0,3,0,0>, <0,0,1,1,0,0,0,2>, <0,0,1,2,0,0,0,0>, <0,0,2,0,0,1,0,0>, <0,1,0,0,0,2,0,0>, <0,1,1,0,0,0,0,0>]" ); }
        if ( fraction.reduce().equals( new Fraction( 3, 8 ) ) ) { return parse( "[<0,0,0,0,0,0,0,3>, <0,0,0,1,0,0,0,1>]" ); }
        if ( fraction.reduce().equals( new Fraction( 5, 8 ) ) ) { return parse( "[<0,0,0,0,0,0,0,5>, <0,0,0,0,0,3,0,1>, <0,0,0,1,0,0,0,3>, <0,0,0,2,0,0,0,1>, <0,0,1,0,0,1,0,1>, <0,1,0,0,0,0,0,1>]" ); }
        throw new RuntimeException( "not in table: " + fraction );
    }

    //Brute force search for solutions.
    //note: only works for fractions < 1
    private ArrayList<List<Integer>> getCoefficientSets( final Fraction fraction ) {

        List<Integer> a1 = range( 0, 2 );
        List<Integer> a2 = range( 0, 3 );
        List<Integer> a3 = range( 0, 4 );
        List<Integer> a4 = range( 0, 5 );
        List<Integer> a5 = range( 0, 6 );
        List<Integer> a6 = range( 0, 7 );
        List<Integer> a7 = range( 0, 8 );
        List<Integer> a8 = range( 0, 9 );

        ArrayList<List<Integer>> coefficients = new ArrayList<List<Integer>>();
        for ( int a : a1 ) {
            final Fraction aa = new Fraction( a, 1 );
            for ( int b : a2 ) {
                final Fraction bb = new Fraction( b, 2 );
                for ( int c : a3 ) {
                    final Fraction cc = new Fraction( c, 3 );
                    for ( int d : a4 ) {
                        final Fraction dd = new Fraction( d, 4 );
                        for ( int e : a5 ) {
                            final Fraction ee = new Fraction( e, 5 );
                            for ( int f : a6 ) {
                                final Fraction ff = new Fraction( f, 6 );
                                for ( int g : a7 ) {
                                    final Fraction gg = new Fraction( g, 7 );
                                    for ( int h : a8 ) {
                                        final Fraction hh = new Fraction( h, 8 );
                                        Fraction sum = Fraction.sum( list( aa, bb,
                                                                           cc, dd,
                                                                           ee, ff,
                                                                           gg, hh ) );
                                        if ( sum.equals( fraction ) ) {
                                            coefficients.add( list( a, b, c, d, e, f, g, h ) );
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return coefficients;
    }

    private ArrayList<List<Integer>> parse( final String s ) {
        ArrayList<List<Integer>> result = new ArrayList<List<Integer>>();
        StringTokenizer st = new StringTokenizer( s, ">" );
        while ( st.hasMoreTokens() ) {
            String list = st.nextToken();
            StringTokenizer st2 = new StringTokenizer( list, "[]<>, " );
            List<Integer> values = nil();
            while ( st2.hasMoreTokens() ) {
                int number = Integer.parseInt( st2.nextToken() );
                values = values.snoc( number );
            }
            result.add( values );
        }
        return result;
    }

    public static void main( String[] args ) {
        java.util.List<Permutation> x = Permutation.permutations( 3 );
        System.out.println( "x = " + x );

        new PictureLevelList();
//        System.out.println( "seven = " + seven );
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