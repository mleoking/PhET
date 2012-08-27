// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model.shapes;

import fj.Equal;
import fj.F;
import fj.Unit;
import fj.data.List;

import java.awt.Color;
import java.util.ArrayList;

import edu.colorado.phet.fractions.buildafraction.model.MixedFraction;
import edu.colorado.phet.fractions.common.math.Fraction;

import static edu.colorado.phet.fractions.buildafraction.model.numbers.MixedNumbersNumberLevelList.getMixedFractions;
import static edu.colorado.phet.fractions.buildafraction.model.numbers.NumberLevelList.shuffle;
import static edu.colorado.phet.fractions.buildafraction.model.shapes.ShapeLevel.shapeLevelMixed;
import static edu.colorado.phet.fractions.buildafraction.model.shapes.ShapeLevelList.choosePiesOrBars;
import static edu.colorado.phet.fractions.buildafraction.view.MixedNumbersLevelSelectionNode.colors;
import static edu.colorado.phet.fractions.common.math.Fraction.fraction;
import static edu.colorado.phet.fractions.common.util.Sampling.choose;
import static edu.colorado.phet.fractions.common.util.Sampling.chooseOne;
import static fj.data.List.*;

/**
 * This class enumerates the different levels for "Build a Mixed Fraction: Shapes"
 *
 * @author Sam Reid
 */
public class MixedNumbersShapeLevelList extends ArrayList<ShapeLevel> {

    //To avoid big piles, we probably want to do a check so that on any given draw, only a single "3" is drawn for a target whole number.  So there will never be a level where two targets appear with 3 as the whole number.
    public MixedNumbersShapeLevelList() {
        add( level1() );
        add( level2() );
        add( level3() );
        add( level4() );
        while ( size() < 10 ) { add( testLevel() ); }
    }

    /*Level 1:
    -- {1:1/2, 2:1/2, 2:1/4} as the targets
    -- Wholes, 1/2's, and 1/4's to complete targets
    -- as before refreshing will randomly reorder targets, and choose between circles/rectangles
    -- a few extra pieces to allow multiple pathways to a solution (for instance, 2 halves that could form a whole)*/
    private ShapeLevel level1() {
        List<MixedFraction> targets = shuffle( list( new MixedFraction( 1, 1, 2 ), new MixedFraction( 2, 1, 2 ), new MixedFraction( 2, 1, 4 ) ) );
        return shapeLevelMixed( addSubdividedCards( straightforwardCards( targets ), 2 ), targets, colors[0], choosePiesOrBars() );
    }

    /*Level 2:
    -- Targets with 1 or 2 as whole number, fractional portion from the set {1/2, 1/3, 2/3, 1/4, 3/4}
    -- Wholes, 1/2's, 1/3's, and 1/4's
    -- a few extra pieces to allow multiple pathways to a solution*/
    private ShapeLevel level2() {
        List<MixedFraction> targets = choose( 3, getMixedFractions( list( 1, 2 ), list( fraction( 1, 2 ), fraction( 1, 3 ), fraction( 2, 3 ), fraction( 1, 4 ), fraction( 3, 4 ) ) ) );
        return shapeLevelMixed( addSubdividedCards( straightforwardCards( targets ), 2 ), shuffle( targets ), colors[1], choosePiesOrBars() );
    }

    //To avoid big piles, we probably want to do a check so that on any given draw, only a single "3" is drawn for a target whole number.  So there will never be a level where two targets appear with 3 as the whole number.
    public static List<MixedFraction> chooseRestricted( F<Unit, List<MixedFraction>> f ) {
        while ( true ) {
            final List<MixedFraction> solution = f.f( Unit.unit() );
            List<MixedFraction> filtered = solution.filter( new F<MixedFraction, Boolean>() {
                @Override public Boolean f( final MixedFraction mixedFraction ) {
                    return mixedFraction.numerator == 3;
                }
            } );
            if ( filtered.length() <= 1 ) {
                return solution;
            }
        }
    }

    /*Level 3:
    -- All targets 1, 2, or 3, as whole number, fractional portion from the set {1/2, 1/3, 2/3, 1/4, 3/4, 1/6, 5/6}
    -- a few extra pieces to allow multiple pathways to a solution*/
    private ShapeLevel level3() {
        List<MixedFraction> targets = chooseRestricted( new F<Unit, List<MixedFraction>>() {
            @Override public List<MixedFraction> f( final Unit unit ) {
                return choose( 3, getMixedFractions( list( 1, 2, 3 ), list( fraction( 1, 2 ), fraction( 1, 3 ), fraction( 2, 3 ), fraction( 1, 4 ), fraction( 3, 4 ), fraction( 1, 6 ), fraction( 5, 6 ) ) ) );
            }
        } );
        return shapeLevelMixed( addSubdividedCards( addSubdividedCards( straightforwardCards( targets ), 3 ), 3 ), shuffle( targets ), colors[2], choosePiesOrBars() );
    }

    /*Level 4:
    -- All targets 1, 2, or 3, as whole number, fractional portion from the set {1/2, 1/3, 2/3, 1/4, 3/4, 1/6, 5/6}
    -- a few extra pieces to allow multiple pathways to a solution*/
    private ShapeLevel level4() {
        List<MixedFraction> targets = replicate( 3, chooseOne( getMixedFractions( list( 1, 2 ), list( fraction( 1, 2 ), fraction( 1, 3 ), fraction( 2, 3 ), fraction( 1, 4 ), fraction( 3, 4 ) ) ) ) );
        return shapeLevelMixed( substituteSubdividedCards( substituteSubdividedCards( straightforwardCards( targets ), 1 ), 2 ), shuffle( targets ), colors[3], choosePiesOrBars() );
    }

    //Take any of the cards, and subdivide it into smaller cards
    private List<Integer> addSubdividedCards( final List<Integer> integers, final int smallestPieceToSubdivide ) {

        //Find cards that can be divided into smaller cards
        List<Integer> allowed = integers.filter( new F<Integer, Boolean>() {
            @Override public Boolean f( final Integer integer ) {
                return integer <= smallestPieceToSubdivide;
            }
        } );

        //Choose one to subdivide
        Integer oneToSubdivide = chooseOne( allowed );

        //Split into two smaller cards that add to the same thing
        List<Integer> subdivided = list( oneToSubdivide * 2, oneToSubdivide * 2 );

        //Add the new cards.
        return integers.append( subdivided );
    }

    //Take any of the cards, and subdivide it into smaller cards
    private List<Integer> substituteSubdividedCards( final List<Integer> integers, final int smallestPieceToSubdivide ) {

        //Find cards that can be divided into smaller cards
        List<Integer> allowed = integers.filter( new F<Integer, Boolean>() {
            @Override public Boolean f( final Integer integer ) {
                return integer <= smallestPieceToSubdivide;
            }
        } );

        //Choose one to subdivide
        Integer oneToSubdivide = chooseOne( allowed );

        //Split into two smaller cards that add to the same thing
        List<Integer> subdivided = list( oneToSubdivide * 2, oneToSubdivide * 2 );

        //Add the new cards.
        return integers.delete( oneToSubdivide, Equal.intEqual ).append( subdivided );
    }

    //Just the exact cards necessary to fit the selected fractions
    public static List<Integer> straightforwardCards( final List<MixedFraction> targets ) {
        List<Integer> cards = nil();
        for ( MixedFraction mixedFraction : targets ) {
            for ( int i = 0; i < mixedFraction.whole; i++ ) {
                cards = cards.snoc( 1 );
            }
            Fraction fractionPart = mixedFraction.getFractionPart();
            for ( int i = 0; i < fractionPart.numerator; i++ ) {
                cards = cards.snoc( fractionPart.denominator );
            }
        }
        return cards;
    }

    private ShapeLevel testLevel() {
        return new ShapeLevel( List.list( 1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6 ),
                               List.replicate( 3, new MixedFraction( 1, 2, 3 ) ), Color.blue, ShapeType.PIE );
    }
}