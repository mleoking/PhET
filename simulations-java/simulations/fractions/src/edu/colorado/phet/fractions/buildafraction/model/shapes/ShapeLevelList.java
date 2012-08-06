// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model.shapes;

import fj.F;
import fj.Ord;
import fj.P2;
import fj.data.List;
import lombok.Data;

import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.fractions.common.math.Fraction;
import edu.colorado.phet.fractions.common.util.Distribution;

import static edu.colorado.phet.fractions.buildafraction.model.numbers.NumberLevelList.shuffle;
import static edu.colorado.phet.fractions.buildafraction.model.shapes.ShapeLevel.shapeLevel;
import static edu.colorado.phet.fractions.buildafraction.view.LevelSelectionNode.colors;
import static edu.colorado.phet.fractions.common.math.Fraction._numerator;
import static edu.colorado.phet.fractions.common.math.Fraction.fraction;
import static edu.colorado.phet.fractions.common.util.Sampling.*;
import static fj.data.List.*;

/**
 * List of levels for "Build a Fraction: Shapes".  These levels are created on init or during refresh of individual levels.
 *
 * @author Sam Reid
 */
public class ShapeLevelList extends ArrayList<ShapeLevel> {
    private final Random random = new Random();

    public ShapeLevelList() {

        //For levels 1-4, either tiles or pies will work
        //SR: Let's make level 1 = pies/tiles then level 2 opposite of level 1, then same for 3-4
        boolean level1Pies = random.nextBoolean();
        boolean level3Pies = random.nextBoolean();
        add( level1( level1Pies ) );
        add( level2( !level1Pies ) );
        add( level3( level3Pies ) );
        add( level4( !level3Pies ) );
        add( level5() );
        add( level6() );
        add( level7() );
        add( level8() );
        add( level9() );
        add( level10() );
    }

    /* Level 1:
    --I like the idea of starting with this simple set of fractions
    - Can we make two draws, one target should be from the set  {1/1, 2/2, 3/3} and the second draw for the next two targets from the set {1/2, 1/3, 2/3} */
    private ShapeLevel level1( final boolean pies ) {
        Distribution<Fraction> set1 = new Distribution<Fraction>( list( fraction( 1, 1 ),
                                                                        fraction( 2, 2 ),
                                                                        fraction( 3, 3 ) ) );
        Distribution<Fraction> set2 = new Distribution<Fraction>( list( fraction( 1, 2 ),
                                                                        fraction( 1, 3 ),
                                                                        fraction( 2, 3 ) ) );
        Fraction a = set1.draw();
        Fraction b = set2.drawAndRemove();
        Fraction c = set2.draw();
        return new ShapeLevel( list( 1, 1, 2, 2, 3, 3, 3 ),
                               shuffle( list( a,
                                              b,
                                              c ) ), colors[0], booleanToShape( pies ) );
    }

    /* Level 2:
   --Here I would begin choosing from a distribution of fractions ranging from 1/2 to 4/5.
   As in the numerator could be 1, 2, 3, or 4 and the denominator could be 2, 3, 4, or 5 with the stipulation that the fraction is always less than 1.
   So I would remove the "wholes" from the shapes pile.
    */
    private ShapeLevel level2( final boolean pies ) {
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
        return shapeLevel( straightforwardCards( selected ), selected, colors[1], booleanToShape( pies ) );
    }

    //Just the exact cards necessary to fit the selected fractions
    private List<Integer> straightforwardCards( final List<Fraction> targets ) {
        List<Integer> cards = nil();
        for ( Fraction fraction : targets ) {
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
    private ShapeLevel level3( final boolean pies ) {
        List<Fraction> targets = list( fraction( 1, 1 ), fraction( 1, 2 ), fraction( 1, 3 ), fraction( 1, 4 ), fraction( 1, 5 ), fraction( 1, 6 ),
                                       fraction( 2, 2 ), fraction( 2, 3 ), fraction( 2, 4 ), fraction( 2, 5 ), fraction( 2, 6 ),
                                       fraction( 3, 3 ), fraction( 3, 4 ), fraction( 3, 5 ), fraction( 3, 6 ),
                                       fraction( 4, 4 ), fraction( 4, 5 ), fraction( 4, 6 ),
                                       fraction( 5, 5 ), fraction( 5, 6 ),
                                       fraction( 6, 6 ) );

        List<Fraction> selected = choose( 3, targets );
        return shapeLevel( createCardsSameNumberEachType( selected ), selected, colors[2], booleanToShape( pies ) );
    }

    /* Level 4:
    -- I like the idea of trying to build the same targets with constrained pieces.
    -- let's make the 2 possible targets be {1/2, 1/1}.  For 1/2, I like the constraints you have chosen, but take away the "whole" piece. For 1/1,
    again take away the "whole" piece, and constrain one of the targets so they must use two different sizes.  For instance, only enough halves and
    quarters so they must do 1 half piece and 2 quarter pieces.  Or 2 third pieces and 2 sixth pieces.*/
    private ShapeLevel level4( final boolean pies ) { return random.nextBoolean() ? halfLevel4( pies ) : wholesLevel4( pies ); }

    private ShapeLevel wholesLevel4( final boolean pies ) {
        return shapeLevel( list( 2, 2, 2, 3, 3, 3, 4, 4, 4, 6, 6, 6 ), replicate( 3, fraction( 1, 1 ) ), colors[3], booleanToShape( pies ) );
    }

    private ShapeLevel halfLevel4( final boolean pies ) {
        return shapeLevel( list( 2, 2, 2, 3, 3, 3, 4, 4, 4, 6, 6, 6 ), replicate( 3, fraction( 1, 2 ) ), colors[3], booleanToShape( pies ) );
    }

    /*Level 5:
    --At this point I think we should switch to 4 bins for all future levels
    -SR: Let's make this one have 3 challenges so it can fit on the page with 3 stars per level
    -- Let's use pie shapes for this level
    - numerator able to range from 1-8, and denominator able to range from 1-8, with the number less than or equal to 1
    - all pieces available to fulfill targets in the most straightforward way (so for instance if 3/8 appears there will 3 1/8 pieces)*/
    private ShapeLevel level5() {
        List<Integer> values = range( 1, 9 );
        List<Integer> denominators = choose( 3, values );
        List<Fraction> selected = denominators.map( new F<Integer, Fraction>() {
            @Override public Fraction f( final Integer denominator ) {
                return new Fraction( chooseOne( range( 1, denominator + 1 ) ), denominator );
            }
        } );
        return shapeLevel( straightforwardCards( selected ), selected, colors[4], ShapeType.PIE );
    }

    public static @Data class CoefficientPair {
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
    private ShapeLevel level6() {
        ShapeLevel level = sampleLevel6();
        if ( !level.hasValuesGreaterThanOne() ) {
            return level;
        }
        else {
            return level6();
        }
    }

    private ShapeLevel sampleLevel6() {
        //let's implement this my making each solution as na + mb, where a and b are the fractions from pairs above
        final List<Integer> cardSizes = chooseOne( list( list( 2, 3 ), list( 2, 4 ), list( 3, 4 ), list( 2, 6 ), list( 3, 6 ), list( 4, 8 ), list( 2, 8 ) ) );

        List<CoefficientPair> nmPairs = list( new CoefficientPair( 0, 1 ), new CoefficientPair( 1, 0 ),
                                              new CoefficientPair( 1, 1 ), new CoefficientPair( 1, 2 ), new CoefficientPair( 2, 1 ), new CoefficientPair( 2, 2 ),
                                              new CoefficientPair( 3, 1 ), new CoefficientPair( 1, 3 ) );

        List<CoefficientPair> selectedCoefficients = choose( 4, nmPairs );

        List<Fraction> targets = selectedCoefficients.map( new F<CoefficientPair, Fraction>() {
            @Override public Fraction f( final CoefficientPair c ) {
                Fraction a = new Fraction( c.n, cardSizes.index( 0 ) );
                Fraction b = new Fraction( c.m, cardSizes.index( 1 ) );
                return a.plus( b ).reduce();
            }
        } );

        List<Integer> pieces = nil();
        for ( CoefficientPair c : selectedCoefficients ) {
            pieces = pieces.append( replicate( c.n, cardSizes.index( 0 ) ) );
            pieces = pieces.append( replicate( c.m, cardSizes.index( 1 ) ) );
        }

        return shapeLevel( pieces, targets, colors[5], choosePiesOrBars( targets ) );
    }

    /*Level 7:
--Top two targets, and bottom 2 targets are equivalent but still numbers less than 1
-- A built in check to draw a different fraction for the top 2 and the bottom 2
-- Possible fractions sets from which to draw 2 each {1/2, 1/3, 2/3, 1/4, 3/4, 5/6, 3/8, 5/8}
-- Shape pieces constrained so that for instance if 1/2 and 1/2 appears for the top targets, a 1/2 piece might be available but the other one
will need to be made with a 1/4 and 1/4, or a 1/3 and a 1/6 or such.
-- If 3/8 or 5/8 are drawn circles should be used, if not circles or tiles will work fine*/
    ShapeLevel level7() {
        List<Fraction> available = list( new Fraction( 1, 2 ), new Fraction( 1, 3 ), new Fraction( 2, 3 ), new Fraction( 1, 4 ),
                                         new Fraction( 3, 4 ), new Fraction( 5, 6 ), new Fraction( 3, 8 ), new Fraction( 5, 8 ) );

        P2<Fraction, Fraction> selected = chooseTwo( available );
        List<Fraction> targets = list( selected._1(), selected._1(), selected._2(), selected._2() );

        ArrayList<List<Integer>> coefficientsTop = getCoefficientSets( selected._1() );
        List<List<Integer>> chosenTop = choose( 2, iterableList( coefficientsTop ) );

        ArrayList<List<Integer>> coefficientsBottom = getCoefficientSets( selected._2() );
        List<List<Integer>> chosenBottom = choose( 2, iterableList( coefficientsBottom ) );

        List<Integer> shapes = coefficientsToShapes( chosenTop.append( chosenBottom ) );

        return shapeLevel( shapes, targets, colors[6], ShapeType.PIE );
    }

    private List<Integer> coefficientsToShapes( final List<List<Integer>> allCoefficients ) {
        List<Integer> shapes = nil();
        for ( List<Integer> coefficients : allCoefficients ) {
            for ( int i = 0; i < coefficients.length(); i++ ) {
                int size = i + 1;
                int numberToGet = coefficients.index( i );
                for ( int k = 0; k < numberToGet; k++ ) {
                    shapes = shapes.snoc( size );
                }
            }
        }
        return shapes;
    }

    //Brute force search for coefficients that make n*1/1 + m * 1/2 + o * 1/3 + ... solve the given fraction.
    private ArrayList<List<Integer>> getCoefficientSets( final Fraction target ) {

        List<Integer> range = range( 0, 9 );

        //Try all combinations of coefficients, but bail if it gets too large to make it efficient to run in real-time.
        ArrayList<List<Integer>> coefficients = new ArrayList<List<Integer>>();
        for ( int i1 : range ) {
            Fraction sum1 = new Fraction( i1, 1 );
            if ( sum1.lessThanOrEqualTo( target ) ) {
                for ( int i2 : range ) {
                    Fraction sum2 = sum1.plus( new Fraction( i2, 2 ) );
                    if ( sum2.lessThanOrEqualTo( target ) ) {
                        for ( int i3 : range ) {
                            Fraction sum3 = sum2.plus( new Fraction( i3, 3 ) );
                            if ( sum3.lessThanOrEqualTo( target ) ) {
                                for ( int i4 : range ) {
                                    Fraction sum4 = sum3.plus( new Fraction( i4, 4 ) );
                                    if ( sum4.lessThanOrEqualTo( target ) ) {
                                        for ( int i5 : range ) {
                                            Fraction sum5 = sum4.plus( new Fraction( i5, 5 ) );
                                            if ( sum5.lessThanOrEqualTo( target ) ) {
                                                for ( int i6 : range ) {
                                                    Fraction sum6 = sum5.plus( new Fraction( i6, 6 ) );
                                                    if ( sum6.lessThanOrEqualTo( target ) ) {
                                                        for ( int i7 : range ) {
                                                            Fraction sum7 = sum6.plus( new Fraction( i7, 7 ) );
                                                            if ( sum6.lessThanOrEqualTo( target ) ) {
                                                                for ( int i8 : range ) {
                                                                    Fraction sum = sum7.plus( new Fraction( i8, 8 ) );
                                                                    if ( sum.equals( target ) ) {
                                                                        coefficients.add( list( i1, i2, i3, i4, i5, i6, i7, i8 ) );
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
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

    /*
   Level 8 :
   -- Introduce numbers larger than 1 at this level
   -- On this level lets have at least 2 numbers larger than 1 as targets
   -- Enough pieces available to match targets in "obvious ways"...so if 5/4 is a target a whole piece is available and a 1/4 piece available
    */
    ShapeLevel level8() {
        List<Fraction> targets = level8Targets();
        return shapeLevel( straightforwardCards( targets ), shuffle( targets ), colors[7], choosePiesOrBars( targets ) );
    }

    private List<Fraction> level8Targets() {
        final List<Fraction> large = choose( 2, list( new Fraction( 4, 3 ), new Fraction( 3, 2 ), new Fraction( 5, 4 ),
                                                      new Fraction( 5, 3 ), new Fraction( 7, 4 ) ) );
        final List<Fraction> small = choose( 2, list( new Fraction( 2, 3 ), new Fraction( 3, 4 ), new Fraction( 2, 5 ), new Fraction( 3, 5 ), new Fraction( 4, 5 ) ) );
        return large.append( small );
    }

    /*
   Level 9
   --Same as level 8 but now some targets only allow "non-obvious" matches with pieces.  For instance, if the target is greater than one, no
   "wholes" should be available.  So if 5/4 is a target it would need to be built from something like 2 half pieces and a quarter piece
    */
    ShapeLevel level9() {
        List<Fraction> targets = level8Targets();
        //TODO: make cards be non-straightforward.  Possibly by consolidating into pieces as large as possible, then doing random subdivisions.
        return shapeLevel( straightforwardCards( targets ), shuffle( targets ), colors[8], choosePiesOrBars( targets ) );
    }

    /*
   Level 10
   --Same as level 7 but now all targets are greater than one.
   --Still top two targets same, and bottom two targets the same
   --No whole pieces available, and targets must be built in interesting ways.  We could say something like the target must be built from 3 or
   more pieces as a way to constrain the pieces given. So for instance something like 4/3 would have to be built by something like 1(half) +
   2(quarters) + (1/3)
    */
    ShapeLevel level10() {
        List<Fraction> available = list( new Fraction( 3, 2 ),
                                         new Fraction( 4, 3 ), new Fraction( 5, 3 ),
                                         new Fraction( 5, 4 ), new Fraction( 7, 4 ),
                                         new Fraction( 6, 5 ), new Fraction( 7, 5 ), new Fraction( 8, 5 ), new Fraction( 9, 5 ),
                                         new Fraction( 7, 6 ) );

        P2<Fraction, Fraction> two = chooseTwo( available );
        List<Fraction> targets = list( two._1(), two._1(), two._2(), two._2() );
        List<Integer> shapes1 = interestingShapes( targets.index( 0 ) );
        List<Integer> shapes2 = interestingShapes( targets.index( 1 ) );
        List<Integer> shapes3 = interestingShapes( targets.index( 2 ) );
        List<Integer> shapes4 = interestingShapes( targets.index( 3 ) );

        return shapeLevel( shapes1.append( shapes2 ).append( shapes3 ).append( shapes4 ), targets, colors[9], choosePiesOrBars( targets ) );
    }

    //Get some interesting(non straightforward) shapes for making the fraction.
    private List<Integer> interestingShapes( final Fraction fraction ) {
        List<List<Integer>> coefficients = iterableList( getCoefficientSets( fraction ) );
        List<List<Integer>> filtered = coefficients.filter( new F<List<Integer>, Boolean>() {
            @Override public Boolean f( final List<Integer> integers ) {
                return numberOfNonzeroElements( integers ) > 1;
            }
        } );

        final List<Integer> chosenCoefficients = chooseOne( filtered.length() == 0 ? coefficients : filtered );
        return coefficientsToShapes( single( chosenCoefficients ) );
    }

    private int numberOfNonzeroElements( final List<Integer> integers ) {
        return integers.filter( new F<Integer, Boolean>() {
            @Override public Boolean f( final Integer integer ) {
                return integer != 0;
            }
        } ).length();
    }

    //if denominator large, use pies otherwise random between pies and bars
    private ShapeType choosePiesOrBars( final List<Fraction> targets ) {
        ShapeType x = random.nextBoolean() ? ShapeType.PIE : ShapeType.BAR;
        return targets.exists( new F<Fraction, Boolean>() {
            @Override public Boolean f( final Fraction fraction ) {
                return fraction.denominator >= 7;
            }
        } ) ? ShapeType.BAR : x;
    }

    private ShapeType booleanToShape( final boolean pies ) {return pies ? ShapeType.PIE : ShapeType.BAR;}
}