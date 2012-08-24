// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model.numbers;

import fj.Equal;
import fj.F;
import fj.P2;
import fj.data.List;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import edu.colorado.phet.fractions.buildafraction.model.MixedFraction;
import edu.colorado.phet.fractions.common.math.Fraction;
import edu.colorado.phet.fractions.common.util.Distribution;
import edu.colorado.phet.fractions.fractionmatcher.model.Pattern;
import edu.colorado.phet.fractions.fractionmatcher.view.FilledPattern;

import static edu.colorado.phet.fractions.common.math.Fraction.*;
import static edu.colorado.phet.fractions.common.util.Sampling.*;
import static edu.colorado.phet.fractions.common.view.Colors.*;
import static edu.colorado.phet.fractions.fractionmatcher.view.FilledPattern.randomFill;
import static edu.colorado.phet.fractions.fractionmatcher.view.FilledPattern.sequentialFill;
import static fj.data.List.*;
import static java.awt.Color.orange;

/**
 * List of levels for the number games (where the user creates fractions with numbers to match given shapes).
 *
 * @author Sam Reid
 */
public class NumberLevelList extends ArrayList<NumberLevel> {
    private static final Random random = new Random();

    public static abstract class PatternMaker extends F<MixedFraction, Pattern> {
        public final List<Integer> acceptedDenominators;

        protected PatternMaker( int... values ) {
            List<Integer> c = nil();
            for ( int value : values ) {
                c = c.snoc( value );
            }
            acceptedDenominators = c;
        }

        public F<MixedFraction, FilledPattern> sequential() {
            return new F<MixedFraction, FilledPattern>() {
                @Override public FilledPattern f( final MixedFraction fraction ) {
                    return sequentialFill( PatternMaker.this.f( fraction ), fraction.numerator );
                }
            };
        }

        public F<MixedFraction, FilledPattern> random() {
            return new F<MixedFraction, FilledPattern>() {
                @Override public FilledPattern f( final MixedFraction fraction ) {
                    return randomFill( PatternMaker.this.f( fraction ), fraction.numerator, random.nextLong() );
                }
            };
        }
    }

    //Create the pattern makers for all the different shape types. The following reads better with closure folding.
    public static final PatternMaker pie = new PatternMaker( 1, 2, 3, 4, 5, 6, 7, 8, 9 ) {
        @Override public Pattern f( final MixedFraction fraction ) {
            return Pattern.pie( fraction.denominator );
        }
    };
    public static final PatternMaker horizontalBar = new PatternMaker( 1, 2, 3, 4, 5, 6, 7, 8 ) {
        @Override public Pattern f( final MixedFraction fraction ) {
            return Pattern.horizontalBars( fraction.denominator );
        }
    };
    public static final PatternMaker verticalBar = new PatternMaker( 1, 2, 3, 4, 5, 6, 7, 8 ) {
        @Override public Pattern f( final MixedFraction fraction ) {
            return Pattern.verticalBars( fraction.denominator );
        }
    };
    public static final PatternMaker pyramid1 = new PatternMaker( 1 ) {
        @Override public Pattern f( final MixedFraction fraction ) {
            return Pattern.pyramidSingle();
        }
    };
    public static final PatternMaker pyramid4 = new PatternMaker( 4 ) {
        @Override public Pattern f( final MixedFraction fraction ) {
            return Pattern.pyramidFour();
        }
    };
    public static final PatternMaker pyramid9 = new PatternMaker( 9 ) {
        @Override public Pattern f( final MixedFraction fraction ) {
            return Pattern.pyramidNine();
        }
    };
    public static final PatternMaker grid1 = new PatternMaker( 1 ) {
        @Override public Pattern f( final MixedFraction fraction ) {
            return Pattern.grid( 1 );
        }
    };
    public static final PatternMaker grid4 = new PatternMaker( 4 ) {
        @Override public Pattern f( final MixedFraction fraction ) {
            return Pattern.grid( 2 );
        }
    };
    public static final PatternMaker grid9 = new PatternMaker( 9 ) {
        @Override public Pattern f( final MixedFraction fraction ) {
            return Pattern.grid( 3 );
        }
    };
    public static final PatternMaker flower = new PatternMaker( 6 ) {
        @Override public Pattern f( final MixedFraction fraction ) {
            return Pattern.sixFlower();
        }
    };
    public static final PatternMaker polygon = new PatternMaker( 1, 2, 3, 4, 5, 6, 7, 8, 9 ) {
        @Override public Pattern f( final MixedFraction fraction ) {
            return Pattern.polygon( 60, fraction.denominator );
        }
    };

    //Only choose from the universal patterns (that accept all denominators)
    private static final List<PatternMaker> universalTypes = list( pie, horizontalBar, verticalBar );

    public static final List<PatternMaker> allTypes = list( pie, horizontalBar, verticalBar, pyramid1, pyramid4, pyramid9, grid1, grid4, grid9, flower, polygon );

    //Create all of the levels
    public NumberLevelList() {
        add( level1() );
        add( level2() );
        add( level3() );
        add( level4() );
        add( level5() );
        add( level6() );
        add( level7() );
        add( level8() );
        add( level9() );
        add( level10() );
    }

    //Choose a representation, pies or bars, but use the same representation for all things
    private NumberLevel level1() {
        RandomColors3 colors = new RandomColors3();
        return new NumberLevel( list( 1, 1, 2, 2, 3, 3 ), shuffle( list( NumberTarget.target( 1, 2, colors.next(), pie.sequential() ),
                                                                         NumberTarget.target( 1, 3, colors.next(), pie.sequential() ),
                                                                         NumberTarget.target( 2, 3, colors.next(), pie.sequential() ) ) ) );
    }

    /*
    Level 2:
    --Draw from a distribution of fractions ranging from 1/2 to 4/5.  As in the numerator could be 1, 2, 3, or 4 and the denominator could be 2, 3, 4, or 5 with the stipulation that the fraction is always less than 1.
    -- Percentage chance for choosing the bar representations at 30 percent
    -- Just use a single representation
     */
    private NumberLevel level2() {
        final F<MixedFraction, FilledPattern> representation = new Distribution<F<MixedFraction, FilledPattern>>() {{
            put( pie.sequential(), 40 );
            put( horizontalBar.sequential(), 30 );
            put( verticalBar.sequential(), 30 );
        }}.draw();
        final ArrayList<Fraction> selected = new ArrayList<Fraction>();
        for ( int i = 0; i < 3; i++ ) {
            final Fraction fraction = chooseFraction( rangeInclusive( 1, 4 ), rangeInclusive( 2, 5 ), new F<Fraction, Boolean>() {
                @Override public Boolean f( final Fraction fraction ) {
                    return fraction.toDouble() <= 1 + 1E-6 && !selected.contains( fraction );
                }
            } );
            selected.add( fraction );
        }
        final List<Fraction> selectedList = iterableList( selected );
        final ArrayList<Color> colors = new ArrayList<Color>( shuffledColors().toCollection() );
        return new NumberLevel( shuffle( selectedList.map( new F<Fraction, NumberTarget>() {
            @Override public NumberTarget f( final Fraction fraction ) {
                return NumberTarget.target( fraction, colors.remove( 0 ), representation );
            }
        } ) ) );
    }

    private NumberLevel level3() {
        ArrayList<Integer> numerators = new ArrayList<Integer>( Arrays.asList( 1, 2, 3, 4, 5 ) );
        Collections.shuffle( numerators );

        //Guaranteed to be solvable, but not enough sixes to just use those as all denominators
        RandomColors3 colors = new RandomColors3();
        return new NumberLevel( list( 1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 7, 7, 7, 8, 8, 8, 9, 9, 9 ), shuffle( list( NumberTarget.target( numerators.get( 0 ), 6, colors.next(), flower.sequential() ),
                                                                                                                                     NumberTarget.target( numerators.get( 1 ), 6, colors.next(), flower.sequential() ),
                                                                                                                                     NumberTarget.target( numerators.get( 2 ), 6, colors.next(), flower.sequential() ) ) ) );
    }

    private NumberLevel level4() {
        RandomColors3 colors = new RandomColors3();
        return new NumberLevel( list( NumberTarget.target( chooseOne( rangeInclusive( 1, 1 ) ), 1, colors.next(), pyramid1.sequential() ),
                                      NumberTarget.target( chooseOne( rangeInclusive( 1, 4 ) ), 4, colors.next(), pyramid4.sequential() ),
                                      NumberTarget.target( chooseOne( rangeInclusive( 1, 9 ) ), 9, colors.next(), pyramid9.sequential() ) ) );
    }


    /*Level 5:
    - numerator able to range from 1-9, and denominator able to range from 1-9, with the number less than 1
    - all representations possible (circle, "9 and 4 square", bars, pyramids, 6 flower, perhaps regular polygons)
    - all cards available to fulfill challenges in the most straightforward way, for instance a 4/5 representation has a 4 and a 5 available.*/
    private NumberLevel level5() {
        List<RepresentationType> types = RepresentationType.all;
        RandomColors4 colors = new RandomColors4();
        return new NumberLevel( list( targetLessThanOrEqualTo1( colors, chooseOne( types ), true ),
                                      targetLessThanOrEqualTo1( colors, chooseOne( types ), true ),
                                      targetLessThanOrEqualTo1( colors, chooseOne( types ), true ) ) );
    }

    /* Level 6:
    --Same as level 5, but now random fill is possible (maybe with 50 percent probability of being used)
    --could begin to introduce some card constraints at this point, for instance making sure that one of the representations only has cards available to match it with a "non-obvious fraction".
    --For instance if 3/9 appears, and 5/9 appears, we have 1(5) and 1(9), but not 2(9), so that 1/3 would need to be used to match. */
    private NumberLevel level6() {

        //Use 1000 tries to come up with a level that has multiple stacks
        for ( int i = 0; i < 1000; i++ ) {
            NumberLevel level = level6Impl();
            if ( level.numbers.group( Equal.intEqual ).length() > 2 ) {
                return level;
            }
        }
        return level6Impl();
    }

    private NumberLevel level6Impl() {
        List<RepresentationType> types = RepresentationType.all;
        RandomColors4 colors = new RandomColors4();
        return NumberLevel.numberLevelReduced( list( targetLessThanOrEqualTo1( colors, chooseOne( types ), random.nextBoolean() ),
                                                     targetLessThanOrEqualTo1( colors, chooseOne( types ), random.nextBoolean() ),
                                                     targetLessThanOrEqualTo1( colors, chooseOne( types ), random.nextBoolean() ),
                                                     targetLessThanOrEqualTo1( colors, chooseOne( types ), random.nextBoolean() ) ) );
    }

    /* Level 7:
    --Top two representations are equivalent, and bottom 2 representations are equivalent but still numbers less than 1
    -- A built in check to draw a different fraction for the top 2 and the bottom 2
    -- Possible fractions sets from which to draw 2 each {1/2, 2/4, 3/6} , {1/3, 2/6, 3/9}, {2/3, 4/6, 3/9}, {1/4, 2/8}, {3/4, 6/8}
    -- I think the representations should both be equal, for instance, 2 pies divided the same, and two bars divided the same, so that the learning goal is focused on the same exact picture can be represented by 2 different fractions. Probably always displaying the reduced fraction as the picture.
    -- Cards constrained, so for instance if {1/2, 3/6} is drawn for the top pair and {3/4, 6/8} drawn for the bottom, we would have 1(1), 1(2), 2(3), 1(4), 2(6), 1(8) */
    @SuppressWarnings("unchecked") private NumberLevel level7() {
        List<List<Fraction>> fractionSets = list( list( fraction( 1, 2 ),
                                                        fraction( 2, 4 ),
                                                        fraction( 3, 6 ) ),
                                                  list( fraction( 1, 3 ),
                                                        fraction( 2, 6 ),
                                                        fraction( 3, 9 ) ),
                                                  list( fraction( 2, 3 ),
                                                        fraction( 4, 6 ),
                                                        fraction( 6, 9 ) ),
                                                  list( fraction( 1, 4 ),
                                                        fraction( 2, 8 ) ),
                                                  list( fraction( 3, 4 ),
                                                        fraction( 6, 8 ) ) );

        P2<List<Fraction>, List<Fraction>> sets = chooseTwo( fractionSets );

        //Choose the fractions to use for top and bottom
        P2<Fraction, Fraction> top = chooseTwo( sets._1() );
        P2<Fraction, Fraction> bottom = chooseTwo( sets._2() );

        //Choose a representation for top

        RandomColors3 colors = new RandomColors3();
        Color topColor = colors.next();
        Color bottomColor = colors.next();

        P2<PatternMaker, PatternMaker> selected = chooseTwo( universalTypes );
        Fraction topPrototype = smallerNumerator( top._1(), top._2() );
        Fraction bottomPrototype = smallerNumerator( bottom._1(), bottom._2() );

        List<Fraction> selectedFractions = list( top._1(), top._2(), bottom._1(), bottom._2() );
        final List<NumberTarget> targets = list( NumberTarget.target( topPrototype, topColor, selected._1().sequential() ),
                                                 NumberTarget.target( topPrototype, topColor, selected._1().sequential() ),
                                                 NumberTarget.target( bottomPrototype, bottomColor, selected._2().sequential() ),
                                                 NumberTarget.target( bottomPrototype, bottomColor, selected._2().sequential() ) );

        //But choose numbers from the original fractions
        final List<Integer> cards = selectedFractions.map( _numerator ).append( selectedFractions.map( _denominator ) );
        return new NumberLevel( cards, targets );
    }

    /*Level 8:
    -- Introduce double representations at this level (numbers greater than 1)
    -- Have 8 cards, 4 each of 2 numbers
    -- Randomly choose from  {2/3, 3/2, 2/2, 3/3}, {2/4, 4/2, 2/2, 4/4}, {3/4,4/3, 3/3, 4/4}, {3/5, 5/3, 3/3, 5/5}, {3/6, 6/3, 3/3, 6/6}*/
    @SuppressWarnings("unchecked") private NumberLevel level8() {
        List<List<Fraction>> sets = list( list( fraction( 2, 3 ),
                                                fraction( 3, 2 ),
                                                fraction( 2, 2 ),
                                                fraction( 3, 3 ) ),
                                          list( fraction( 2, 4 ),
                                                fraction( 4, 2 ),
                                                fraction( 2, 2 ),
                                                fraction( 4, 4 ) ),
                                          list( fraction( 3, 4 ),
                                                fraction( 4, 3 ),
                                                fraction( 3, 3 ),
                                                fraction( 4, 4 ) ),
                                          list( fraction( 3, 5 ),
                                                fraction( 5, 3 ),
                                                fraction( 3, 3 ),
                                                fraction( 5, 5 ) ),
                                          list( fraction( 3, 6 ),
                                                fraction( 6, 3 ),
                                                fraction( 3, 3 ),
                                                fraction( 6, 6 ) ) );
        List<Fraction> selected = shuffle( chooseOne( sets ) );

        RandomColors4 colors = new RandomColors4();
        final List<NumberTarget> targets = list( NumberTarget.target( selected.index( 0 ), colors.next(), chooseOne( universalTypes ).sequential() ),
                                                 NumberTarget.target( selected.index( 1 ), colors.next(), chooseOne( universalTypes ).sequential() ),
                                                 NumberTarget.target( selected.index( 2 ), colors.next(), chooseOne( universalTypes ).sequential() ),
                                                 NumberTarget.target( selected.index( 3 ), colors.next(), chooseOne( universalTypes ).sequential() ) );
        return new NumberLevel( targets );
    }


    /*Level 9:
    -- Representations both less than 1 and greater than 1
    -- All representations possible
    -- No card constraints (as in straightforward matching of number and picture possible)*/
    private NumberLevel level9() {
        //Choose 4 different patterns
        List<RepresentationType> types = choose( 4, RepresentationType.all );

        RandomColors4 colors = new RandomColors4();
        return new NumberLevel( list( targetLessThanOrEqualTo2( colors, types.index( 0 ), random.nextBoolean() ),
                                      targetLessThanOrEqualTo2( colors, types.index( 1 ), random.nextBoolean() ),
                                      targetLessThanOrEqualTo2( colors, types.index( 2 ), random.nextBoolean() ),
                                      targetLessThanOrEqualTo2( colors, types.index( 3 ), random.nextBoolean() ) ) );
    }

    /*Level 10:
    -- Representations both less than 1 and greater than 1
    -- All representations possible
    -- No card constraints (as in straightforward matching of number and picture possible)*/
    public static NumberLevel level10() {
        //Choose 4 different patterns
        List<RepresentationType> types = choose( 4, RepresentationType.all );

        RandomColors4 colors = new RandomColors4();
        final List<NumberTarget> targets = list( targetLessThanOrEqualTo1( colors, types.index( 0 ), random.nextBoolean() ),
                                                 targetLessThanOrEqualTo1( colors, types.index( 1 ), random.nextBoolean() ),
                                                 targetLessThanOrEqualTo2( colors, types.index( 2 ), random.nextBoolean() ),
                                                 targetLessThanOrEqualTo2( colors, types.index( 3 ), random.nextBoolean() ) );
        List<Fraction> targetFractions = targets.map( new F<NumberTarget, Fraction>() {
            @Override public Fraction f( final NumberTarget numberTarget ) {
                return numberTarget.mixedFraction.toFraction();
            }
        } );
        List<Fraction> scaled = targetFractions.zipWith( shuffle( list( 1, 1, 2, 3 ) ), _times );
        return new NumberLevel( scaled.map( _denominator ).append( scaled.map( _numerator ) ), targets );
    }

    private Fraction smallerNumerator( final Fraction a, final Fraction b ) {
        return b.numerator < a.numerator ? b : a;
    }

    private static NumberTarget targetLessThanOrEqualTo1( final IRandomColors colors, final RepresentationType representationType, boolean sequential ) {
        int denominator = chooseOne( representationType.denominators );
        int numerator = chooseOne( rangeInclusive( 1, denominator ) );
        Fraction fraction = new Fraction( numerator, denominator );
        final PatternMaker patternMaker = representationType.toPattern( fraction );
        return NumberTarget.target( fraction, colors.next(), sequential ? patternMaker.sequential() : patternMaker.random() );
    }

    private static NumberTarget targetLessThanOrEqualTo2( final RandomColors4 colors, final RepresentationType representationType, boolean sequential ) {
        int denominator = chooseOne( representationType.denominators );
        int numerator = chooseOne( rangeInclusive( 1, denominator * 2 ).filter( new F<Integer, Boolean>() {
            @Override public Boolean f( final Integer integer ) {
                return integer < 10;
            }
        } ) );
        Fraction fraction = new Fraction( numerator, denominator );
        final PatternMaker patternMaker = representationType.toPattern( fraction );
        return NumberTarget.target( fraction, colors.next(), sequential ? patternMaker.sequential() : patternMaker.random() );
    }

    private static List<Color> shuffledColors() {
        return shuffle( list( LIGHT_RED, LIGHT_GREEN, LIGHT_BLUE, orange ) );
    }

    public static <T> List<T> shuffle( final List<T> list ) {
        ArrayList<T> collection = new ArrayList<T>( list.toCollection() );
        Collections.shuffle( collection );
        return iterableList( collection );
    }

    public static List<Integer> rangeInclusive( final int a, final int b ) {
        return range( a, b + 1 );
    }

    private Fraction chooseFraction( final List<Integer> allowedNumerators, final List<Integer> allowedDenominators, F<Fraction, Boolean> accept ) {
        for ( int i = 0; i < 1000; i++ ) {
            Fraction fraction = fraction( chooseOne( allowedNumerators ), chooseOne( allowedDenominators ) );
            if ( accept.f( fraction ) ) {
                return fraction;
            }
        }
        throw new RuntimeException( "Couldn't find a match" );
    }

    public static void main( String[] args ) {
        for ( int i = 0; i < 1000; i++ ) {
            NumberLevel level6 = new NumberLevelList().level6();
            List<List<Integer>> groups = level6.numbers.group( Equal.intEqual );
            int numGroups = groups.length();
            System.out.println( "groups: " + numGroups + ", level6.numbers = " + level6.numbers );
        }
    }
}