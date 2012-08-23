// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model.numbers;

import fj.F;
import fj.data.List;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

import edu.colorado.phet.fractions.buildafraction.model.MixedFraction;
import edu.colorado.phet.fractions.common.math.Fraction;
import edu.colorado.phet.fractions.fractionmatcher.view.FilledPattern;

import static edu.colorado.phet.fractions.buildafraction.model.MixedFraction.mixedFraction;
import static edu.colorado.phet.fractions.buildafraction.model.numbers.NumberLevelList.*;
import static edu.colorado.phet.fractions.buildafraction.model.numbers.NumberTarget.target;
import static edu.colorado.phet.fractions.common.math.Fraction.fraction;
import static edu.colorado.phet.fractions.common.util.Sampling.choose;
import static edu.colorado.phet.fractions.common.util.Sampling.chooseOne;
import static fj.data.List.iterableList;
import static fj.data.List.list;

/**
 * List of levels used for the "mixed fractions" tab, for the levels in which number cards are used.
 *
 * @author Sam Reid
 */
public class MixedNumbersNumberLevelList extends ArrayList<NumberLevel> {
    public static final Random random = new Random();

    public MixedNumbersNumberLevelList() {
        add( level1() );
        add( level2() );
        add( level3() );
        add( level4() );
        add( level5() );
        while ( size() < 10 ) { add( levelX() ); }
    }

    /*Level 1:
    -- Circles as targets
    -- {1:1/2, 2:1/2, 3:1/4} as the challenges
    --just enough cards to complete targets
    -- as before refreshing will randomly reorder, recolor
    */
    private NumberLevel level1() {
        RandomColors3 colors = new RandomColors3();
        return new NumberLevel( shuffle( list( target( 1, 1, 2, colors.next(), pie.sequential() ),
                                               target( 2, 1, 2, colors.next(), pie.sequential() ),
                                               target( 3, 1, 4, colors.next(), pie.sequential() ) ) ) );
    }

    /* Level 2:
    -- Circles or Rectangles as targets, but all targets same shape
    -- 1, 2, or 3, as whole number
    -- Fractional portion from the set {1/2, 1/3, 2/3, ¼, ¾} */
    private NumberLevel level2() {
        RandomColors3 colors = new RandomColors3();
        final F<MixedFraction, FilledPattern> shape = random.nextBoolean() ? pie.sequential() : horizontalBar.sequential();
        List<Integer> wholes = list( 1, 2, 3 );
        List<Fraction> fractionParts = list( fraction( 1, 2 ), fraction( 1, 3 ), fraction( 2, 3 ), fraction( 1, 4 ), fraction( 3, 4 ) );
        List<MixedFraction> mixedFractions = getMixedFractions( wholes, fractionParts );

        List<MixedFraction> selected = choose( 3, mixedFractions );
        return new NumberLevel( selected, colors, shape );
    }

    //Generate all combinations of mixed fractions from the given wholes and fraction parts
    private List<MixedFraction> getMixedFractions( final List<Integer> wholes, final List<Fraction> fractionParts ) {//Do not let any MixedFraction be selected twice
        ArrayList<MixedFraction> _mixedFractions = new ArrayList<MixedFraction>();
        for ( Integer whole : wholes ) {
            for ( Fraction fractionPart : fractionParts ) {
                _mixedFractions.add( mixedFraction( whole, fractionPart ) );
            }
        }
        return iterableList( _mixedFractions );
    }

    /*Level 3:
    -- All targets “six flowers”
    -- 1, 2, or 3, as whole number
    -- Fractional portion from the set {1/2, 1/3, 2/3, 1/6, 5/6}
    -- So, if a “six flower” is showing 3/6, we will want a 1 and 2 card in the deck*/
    private NumberLevel level3() {
        List<MixedFraction> mixedFractions = getMixedFractions( list( 1, 2, 3 ), list( fraction( 1, 2 ), fraction( 1, 3 ), fraction( 2, 3 ), fraction( 1, 6 ), fraction( 5, 6 ) ) );
        return new NumberLevel( choose( 3, mixedFractions ), new RandomColors3(), flower.sequential() );
    }

    /*Level 4:
    -- All pyramids
    -- 1, 2, or 3, as whole number
    -- Fractional portion from the set {1/2, 1/3, 2/3, ¼, ¾, 1/9, 2/9, 4/9, 5/9, 7/9, 8/9}*/
    private NumberLevel level4() {
        List<MixedFraction> mixedFractions = getMixedFractions( list( 1, 2, 3 ), list( fraction( 1, 2 ), fraction( 1, 3 ), fraction( 2, 3 ),
                                                                                       fraction( 1, 4 ), fraction( 3, 4 ),
                                                                                       fraction( 1, 9 ), fraction( 2, 9 ), fraction( 4, 9 ), fraction( 5, 9 ), fraction( 7, 9 ), fraction( 8, 9 )
        ) );
        return new NumberLevel( choose( 3, mixedFractions ), new RandomColors3(), new F<MixedFraction, FilledPattern>() {
            @Override public FilledPattern f( final MixedFraction mixedFraction ) {
                final int d = mixedFraction.getFractionPart().denominator;

                //These next two lines are tricky, let me explain them.
                //We want to use the reduced fractions in the fraction declarations above so when the levels are generated they will have a perfect match for the cards
                //However, some of the values don't have a direct representation in the pyramid shape.  For example: 1/2 appears as a 2/4 pyramid.
                //So we have to make the mapping here, which is used for making the "wholes" as well as the fraction parts.
                //For d==4 and d=9, the regular representations can be used
                return d == 2 ? pyramid4.sequential().f( mixedFraction.scaleNumeratorAndDenominator( 2 ) ) :
                       d == 3 ? pyramid9.sequential().f( mixedFraction.scaleNumeratorAndDenominator( 3 ) ) :
                       d == 4 ? pyramid4.sequential().f( mixedFraction ) :
                       d == 9 ? pyramid9.sequential().f( mixedFraction ) :
                       null;
            }
        } );
    }

    /*Level 5:
    --All representations possible, but each target is only one type of representation
    -- 1, 2, or 3, as whole number
    -- Fractional portion from the set {1/2, 1/3, 2/3, 1/4, 3/4, 1/5, 2/5, 3/5, 4/5, 1/6, 5/6, 1/7, 2/7, 3/7, 4/7, 5/7, 6/7, 1/8, 3/8, 5/8, 7/8, 1/9, 2/9, 4/9, 5/9, 7/9, 8/9}
    --2 of the representations match cards exactly, 1 of the representations requires simplifying to a solution*/
    public NumberLevel level5() {
        List<Fraction> expandable = parse( "1/2, 1/3, 2/3, 1/4, 3/4" );
        List<Fraction> full = parse( "1/2, 1/3, 2/3, 1/4, 3/4, 1/5, 2/5, 3/5, 4/5, 1/6, 5/6, 1/7, 2/7, 3/7, 4/7, 5/7, 6/7, 1/8, 3/8, 5/8, 7/8, 1/9, 2/9, 4/9, 5/9, 7/9, 8/9" );
        List<Integer> wholes = list( 1, 2, 3 );
        List<MixedFraction> mixedFractions = getMixedFractions( wholes, full );
        final List<MixedFraction> targets = choose( 2, mixedFractions );
        RandomColors3 colors = new RandomColors3();
        final MixedFraction target1 = targets.index( 0 );
        final MixedFraction target2 = targets.index( 1 );
        final MixedFraction target3 = chooseOne( getMixedFractions( wholes, expandable ) );
        return new NumberLevel( list( target( target1, colors.next(), chooseMatchingPattern( target1.denominator ).sequential() ),
                                      target( target2, colors.next(), chooseMatchingPattern( target2.denominator ).sequential() ),

                                      //For the third target, scale it up so that it is shown as a non-reduced picture
                                      target( target3, colors.next(), new F<MixedFraction, FilledPattern>() {
                                          @Override public FilledPattern f( final MixedFraction mixedFraction ) {
                                              int d = mixedFraction.getFractionPart().denominator;
                                              List<Integer> scaleFactors = d == 2 ? list( 2, 3, 4 ) :
                                                                           d == 3 ? list( 2, 3 ) :
                                                                           d == 4 ? list( 2 ) :
                                                                           null;
                                              Integer scaleFactor = chooseOne( scaleFactors );
                                              return chooseMatchingPattern( d * scaleFactor ).sequential().f( mixedFraction.scaleNumeratorAndDenominator( scaleFactor ) );
                                          }
                                      } ) ) );
    }

    private PatternMaker chooseMatchingPattern( final int denominator ) { return chooseOne( matching( denominator ) ); }

    private List<PatternMaker> matching( final int denominator ) {
        return NumberLevelList.allTypes.filter( new F<PatternMaker, Boolean>() {
            @Override public Boolean f( final PatternMaker patternMaker ) {
                return patternMaker.acceptedDenominators.exists( equalsInt( denominator ) );
            }
        } );
    }

    private F<Integer, Boolean> equalsInt( final int denominator ) {
        return new F<Integer, Boolean>() {
            @Override public Boolean f( final Integer integer ) {
                return integer == denominator;
            }
        };
    }

    //Parse a list of fractions.
    private List<Fraction> parse( final String s ) {
        StringTokenizer st = new StringTokenizer( s, ", " );
        ArrayList<Fraction> f = new ArrayList<Fraction>();
        while ( st.hasMoreTokens() ) {
            String fraction = st.nextToken();
            StringTokenizer st2 = new StringTokenizer( fraction, "/" );
            f.add( fraction( Integer.parseInt( st2.nextToken() ), Integer.parseInt( st2.nextToken() ) ) );
        }
        return iterableList( f );
    }

    private NumberLevel levelX() {
        return new NumberLevel( List.list( 1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 5 ), List.replicate( 3, target( 1, 2, 3, Color.red, pie.sequential() ) ) );
    }
}