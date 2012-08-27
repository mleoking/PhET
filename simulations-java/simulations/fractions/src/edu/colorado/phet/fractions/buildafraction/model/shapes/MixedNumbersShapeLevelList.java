// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model.shapes;

import fj.Equal;
import fj.F;
import fj.data.List;

import java.awt.Color;
import java.util.ArrayList;

import edu.colorado.phet.fractions.buildafraction.model.MixedFraction;
import edu.colorado.phet.fractions.buildafraction.model.numbers.NumberLevelList;
import edu.colorado.phet.fractions.buildafraction.view.MixedNumbersLevelSelectionNode;
import edu.colorado.phet.fractions.common.math.Fraction;
import edu.colorado.phet.fractions.common.util.Sampling;

import static edu.colorado.phet.fractions.buildafraction.model.shapes.ShapeLevelList.choosePiesOrBars;
import static fj.data.List.list;
import static fj.data.List.nil;

/**
 * This class enumerates the different levels for "Build a Mixed Fraction: Shapes"
 *
 * @author Sam Reid
 */
public class MixedNumbersShapeLevelList extends ArrayList<ShapeLevel> {

    //To avoid big piles, we probably want to do a check so that on any given draw, only a single "3" is drawn for a target whole number.  So there will never be a level where two targets appear with 3 as the whole number.
    public MixedNumbersShapeLevelList() {
        add( level1() );
        while ( size() < 10 ) { add( testLevel() ); }
    }

    /*Level 1:
    -- {1:1/2, 2:1/2, 2:1/4} as the targets
    -- Wholes, 1/2's, and 1/4's to complete targets
    -- as before refreshing will randomly reorder targets, and choose between circles/rectangles
    -- a few extra pieces to allow multiple pathways to a solution (for instance, 2 halves that could form a whole)*/
    private ShapeLevel level1() {
        List<MixedFraction> targets = List.list( new MixedFraction( 1, 1, 2 ), new MixedFraction( 2, 1, 2 ), new MixedFraction( 2, 1, 4 ) );
        return ShapeLevel.shapeLevelMixed( subdivideOne( straightforwardCards( targets ) ), NumberLevelList.shuffle( targets ), MixedNumbersLevelSelectionNode.colors[0], choosePiesOrBars() );
    }

    //Take any of the cards, and subdivide it into smaller cards
    private List<Integer> subdivideOne( final List<Integer> integers ) {

        //Find cards that can be divided into smaller cards
        List<Integer> allowed = integers.filter( new F<Integer, Boolean>() {
            @Override public Boolean f( final Integer integer ) {
                return integer <= 4;
            }
        } );

        //Choose one to subdivide
        Integer oneToSubdivide = Sampling.chooseOne( allowed );

        //Split into two smaller cards that add to the same thing
        List<Integer> subdivided = list( oneToSubdivide * 2, oneToSubdivide * 2 );

        //Remove the old and add the new cards.
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