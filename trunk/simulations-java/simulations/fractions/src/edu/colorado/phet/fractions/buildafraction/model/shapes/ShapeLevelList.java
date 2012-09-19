// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model.shapes;

import fj.F;
import fj.Ord;
import fj.P2;
import fj.data.List;
import lombok.Data;

import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.functionaljava.FJUtils;
import edu.colorado.phet.fractions.buildafraction.model.ShapeLevelFactory;
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
 * List of levels for when the user is using shapes to create fractions on the "Build a Fraction" tab.
 * These levels are created on init or during refresh of individual levels.
 *
 * @author Sam Reid
 */
public class ShapeLevelList implements ShapeLevelFactory {
    private static final Random random = new Random();
    private final ArrayList<Function0<ShapeLevel>> levels = new ArrayList<Function0<ShapeLevel>>();

    public ShapeLevelList() {

        //For levels 1-4, either tiles or pies will work
        //SR: Let's make level 1 = pies/tiles then level 2 opposite of level 1, then same for 3-4
        final boolean level1Pies = random.nextBoolean();
        final boolean level3Pies = random.nextBoolean();

        //Please read this with closure folding.
        addWithPostprocessing( new Function0<ShapeLevel>() {
            public ShapeLevel apply() {
                return level1( level1Pies );
            }
        } );
        addWithPostprocessing( new Function0<ShapeLevel>() {
            public ShapeLevel apply() {
                return level2( !level1Pies );
            }
        } );
        addWithPostprocessing( new Function0<ShapeLevel>() {
            public ShapeLevel apply() {
                return level3( level3Pies );
            }
        } );
        addWithPostprocessing( new Function0<ShapeLevel>() {
            public ShapeLevel apply() {
                return level4( !level3Pies );
            }
        } );
        addWithPostprocessing( new Function0<ShapeLevel>() {
            public ShapeLevel apply() {
                return level5();
            }
        } );
        addWithPostprocessing( new Function0<ShapeLevel>() {
            public ShapeLevel apply() {
                return level6();
            }
        } );
        addWithPostprocessing( new Function0<ShapeLevel>() {
            public ShapeLevel apply() {
                return level7();
            }
        } );
        addWithPostprocessing( new Function0<ShapeLevel>() {
            public ShapeLevel apply() {
                return level8();
            }
        } );
        addWithPostprocessing( new Function0<ShapeLevel>() {
            public ShapeLevel apply() {
                return level9();
            }
        } );
        addWithPostprocessing( new Function0<ShapeLevel>() {
            public ShapeLevel apply() {
                return level10();
            }
        } );
    }

    public ShapeLevel createLevel( final int level ) {
        return levels.get( level ).apply();
    }

    //Keep resampling levels until they match our constraints, such as not having too many stacks or too many cards.
    //It is difficult to constrain the creation of levels as they are being created, because making challenge creation inter-dependent on each other would be very complex.
    //Instead individual challenges within a level are created independently, and this code checks to make sure that it didn't create any stacks too high.
    private void addWithPostprocessing( final Function0<ShapeLevel> levelMaker ) {
        levels.add( withProcessing( levelMaker ) );
    }

    private Function0<ShapeLevel> withProcessing( final Function0<ShapeLevel> levelMaker ) {
        return new Function0<ShapeLevel>() {
            public ShapeLevel apply() {
                for ( int i = 0; i < 100; i++ ) {
                    ShapeLevel level = levelMaker.apply();
                    if ( level.getNumberOfStacks() < 6 && level.getNumberOfCardsInHighestStack() < 8 ) {
                        return level;
                    }
                }
                return levelMaker.apply();
            }
        };
    }

    /* Level 1:
    --Make two draws, one target should be from the set  {1/1, 2/2, 3/3} and the second draw for the next two targets from the set {1/2, 1/3, 2/3} */
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
        return shapeLevel( list( 1, 1, 2, 2, 3, 3, 3 ),
                           shuffle( list( a,
                                          b,
                                          c ) ), colors[0], booleanToShape( pies ) );
    }

    /* Level 2:
   --Choose from a distribution of fractions ranging from 1/2 to 4/5.
   The numerator could be 1, 2, 3, or 4 and the denominator could be 2, 3, 4, or 5 with the stipulation that the fraction is always less than 1.
   No "wholes" will appear in the shapes pile.
    */
    private ShapeLevel level2( final boolean pies ) {
        List<Fraction> targets = choose( 3, list( fraction( 1, 2 ),
                                                  fraction( 1, 3 ),
                                                  fraction( 1, 4 ),
                                                  fraction( 1, 5 ),
                                                  fraction( 2, 3 ),
                                                  fraction( 2, 4 ),
                                                  fraction( 2, 5 ),
                                                  fraction( 3, 4 ),
                                                  fraction( 3, 5 ),
                                                  fraction( 4, 5 ) ) );

        //Use same number for each card type
        //Let's add a few extra pieces so that at least one of the targets has the option of being completed differently.
        return shapeLevel( straightforwardCards( targets ).append( interestingShapesForOne( chooseOne( targets ) ) ), targets, colors[1], booleanToShape( pies ) );
    }

    //Just the exact cards necessary to fit the selected fractions
    private static List<Integer> straightforwardCards( final List<Fraction> targets ) {
        List<Integer> cards = nil();
        for ( Fraction fraction : targets ) {
            for ( int i = 0; i < fraction.numerator; i++ ) {
                cards = cards.snoc( fraction.denominator );
            }
        }
        return cards;
    }

    private static List<Integer> createCardsSameNumberEachType( final List<Fraction> selected ) {
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
    -- Goal: build the same targets with constrained pieces.
    -- 2 possible targets, which are {1/2, 1/1}.  For 1/1, constrain one of the targets so they must use two different sizes.  For instance, only enough halves and
    quarters so they must do 1 half piece and 2 quarter pieces.  Or 2 third pieces and 2 sixth pieces.*/
    private ShapeLevel level4( final boolean pies ) {
        return random.nextBoolean() ? halfLevel4( pies ) : wholesLevel4( pies );
    }

    private ShapeLevel wholesLevel4( final boolean pies ) {
        return shapeLevel( list( 2, 2, 2, 3, 3, 3, 4, 4, 4, 6, 6, 6 ), replicate( 3, fraction( 1, 1 ) ), colors[3], booleanToShape( pies ) );
    }

    private ShapeLevel halfLevel4( final boolean pies ) {
        return shapeLevel( list( 2, 3, 3, 3, 4, 4, 4, 6, 6, 6 ), replicate( 3, fraction( 1, 2 ) ), colors[3], booleanToShape( pies ) );
    }

    /*Level 5:
    -- Pie shapes for this level
    -- numerator able to range from 1-8, and denominator able to range from 1-8, with the number less than or equal to 1
    -- all pieces available to fulfill targets in the most straightforward way (so for instance if 3/8 appears there will 3 1/8 pieces)*/
    private ShapeLevel level5() {
        List<Integer> values = range( 1, 9 );
        List<Integer> denominators = choose( 3, values );
        List<Fraction> selected = denominators.map( new F<Integer, Fraction>() {
            @Override public Fraction f( final Integer denominator ) {
                return new Fraction( chooseOne( range( 1, denominator + 1 ) ), denominator );
            }
        } );
        return shapeLevel( straightforwardCards( selected ).append( interestingShapesForOne( chooseOne( selected ) ) ), selected, colors[4], ShapeType.PIE );
    }

    public static @Data class CoefficientPair {
        public final int n;
        public final int m;
    }

    /*Level 6:
    --all targets are made from only 2 stacks of the same size pieces
    --So for instance we give a stack of thirds and a stack of halves, and {2/3, 2/4, 5/6, 1/1} are the target fractions, but we constrain the
    pieces so that some fractions must be made in "interesting" ways.  2/3 could just be made with 2 third pieces, but 5/6 would need to be made of a 1/2 and a 1/3.
    -- It seems the sets that would work well for pieces would be, {1/2, 1/3}, {1/2, 1/4}, {1/3, 1/4}, {1/2, 1/6}, {1/3, 1/6}, {1/4, 1/8}, {1/2, 1/8}
    --the constraint should be such that only enough pieces exist to complete the targets.
    Keep the values less than 1 by trial and error.*/
    private ShapeLevel level6() {
        ShapeLevel level = sampleLevel6();
        if ( !level.hasValuesGreaterThanOne() ) {
            return level;
        }
        else {
            return level6();
        }
    }

    @SuppressWarnings("unchecked") private ShapeLevel sampleLevel6() {
        //let's implement this my making each solution as na + mb, where a and b are the fractions from pairs above
        final List<Integer> cardSizes = chooseOne( list( list( 2, 3 ), list( 2, 4 ), list( 3, 4 ), list( 2, 6 ), list( 3, 6 ), list( 4, 8 ), list( 2, 8 ) ) ); //unchecked warning

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

        return shapeLevel( pieces, targets, colors[5], choosePiesOrBars() );
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

    private static List<Integer> coefficientsToShapes( final List<List<Integer>> allCoefficients ) {
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

    public static class Tester {
        public static void main( String[] args ) {
            ArrayList<List<Integer>> result = getCoefficientSets( new Fraction( 20, 3 ) );
            System.out.println( "result = " + result );
        }
    }

    //Brute force search for coefficients that make n*1/1 + m * 1/2 + o * 1/3 + ... solve the given fraction.
    public static ArrayList<List<Integer>> getCoefficientSets( final Fraction target ) {

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
                                                            if ( sum7.lessThanOrEqualTo( target ) ) {
                                                                for ( int i8 : range ) {
                                                                    Fraction sum = sum7.plus( new Fraction( i8, 8 ) );
                                                                    if ( sum.valueEquals( target ) ) {
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
   --Students are first introduced to numbers greater than 1 only with 1/2's and 1/4's.  So if the number is greater than 1 on level 8,
        it should be something like 3/2 or 4/2 or 7/4, since 1/2's and 1/4's are more familiar to students (rather than 1/3's and such).
    */
    ShapeLevel level8() {
        List<Fraction> targets = level8Targets();
        return shapeLevel( interestingShapes( targets, 5 ), shuffle( targets ), colors[7], choosePiesOrBars() );
    }

    private List<Fraction> level8Targets() {
        final List<Fraction> large = choose( 2, list( new Fraction( 3, 2 ),
                                                      new Fraction( 4, 2 ),
                                                      new Fraction( 5, 4 ),
                                                      new Fraction( 7, 4 ) ) );
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
        return shapeLevel( interestingShapes( targets, 5 ), shuffle( targets ), colors[8], choosePiesOrBars() );
    }

    /*
   Level 10
   --Same as level 7 but now all targets are greater than one.
   --Still top two targets same, and bottom two targets the same
   --No whole pieces available, and targets must be built in interesting ways.  E.g., the target must be built from 3 or
   more pieces as a way to constrain the pieces given. So for instance something like 4/3 would have to be built by something like 1(half) + 2(quarters) + (1/3)
    */
    private static ShapeLevel level10() {
        List<Fraction> available = list( new Fraction( 3, 2 ),
                                         new Fraction( 4, 3 ), new Fraction( 5, 3 ),
                                         new Fraction( 5, 4 ), new Fraction( 7, 4 ),
                                         new Fraction( 6, 5 ), new Fraction( 7, 5 ), new Fraction( 8, 5 ), new Fraction( 9, 5 ),
                                         new Fraction( 7, 6 ) );

        P2<Fraction, Fraction> two = chooseTwo( available );
        List<Fraction> targets = list( two._1(), two._1(), two._2(), two._2() );

        return shapeLevel( interestingShapes( targets, 5 ), targets, colors[9], choosePiesOrBars() );
    }

    public static List<Integer> interestingShapes( final List<Fraction> fractions, int numSolutionsToSelectFrom ) {
        List<Integer> list = nil();
        for ( Fraction fraction : fractions ) {
            list = list.append( interestingShapesForOne( fraction, numSolutionsToSelectFrom ) );
        }
        return list;
    }

    private static List<Integer> interestingShapesForOne( final Fraction fraction ) {
        return interestingShapesForOne( fraction, 5 );
    }

    //Get some interesting(non straightforward) shapes for making the fraction.
    private static List<Integer> interestingShapesForOne( final Fraction fraction, int numToTake ) {
        List<List<Integer>> coefficients = iterableList( getCoefficientSets( fraction ) );
        List<List<Integer>> filtered = coefficients.filter( new F<List<Integer>, Boolean>() {
            @Override public Boolean f( final List<Integer> integers ) {
                return numberOfNonzeroElements( integers ) > 1;
            }
        } );

        //favor solutions that have a smaller number of cards
        final List<List<Integer>> listToUse = selectSolutionsWithSmallNumberOfCards( filtered.length() == 0 ? coefficients : filtered, numToTake );
        final List<Integer> chosenCoefficients = chooseOne( listToUse );
        return coefficientsToShapes( single( chosenCoefficients ) );
    }

    //In order to remove the tedium but still require creation of interesting shapes, sort by the number of pieces required to create the fraction
    //and choose one of the solutions with a small number of cards.
    private static List<List<Integer>> selectSolutionsWithSmallNumberOfCards( final List<List<Integer>> lists, int numToTake ) {
        List<List<Integer>> sorted = lists.sort( FJUtils.ord( new F<List<Integer>, Double>() {
            @Override public Double f( final List<Integer> list ) {
                return coefficientsToShapes( single( list ) ).length() + 0.0;
            }
        } ) );
        return sorted.take( numToTake );
    }

    private static int numberOfNonzeroElements( final List<Integer> integers ) {
        return integers.filter( new F<Integer, Boolean>() {
            @Override public Boolean f( final Integer integer ) {
                return integer != 0;
            }
        } ).length();
    }

    //Choose randomly whether a level should be pies or bars
    public static ShapeType choosePiesOrBars() {
        return booleanToShape( random.nextBoolean() );
    }

    private static ShapeType booleanToShape( final boolean pies ) {
        return pies ? ShapeType.PIE : ShapeType.BAR;
    }

    //Test generating levels to make sure they are solvable.  This works since there is a check during construction.
    public static void main( String[] args ) {
        for ( int denominator = 1; denominator <= 8; denominator++ ) {
            for ( int numerator = 1; numerator <= denominator * 2; numerator++ ) {
                interestingShapesForOne( new Fraction( numerator, denominator ) );
            }
        }

        for ( int i = 0; i < 1000; i++ ) {
            ShapeLevelList list = new ShapeLevelList();
            list.toString();//Just a method that makes it so list appears used.
            System.out.println( "checked: " + i );
        }
    }
}