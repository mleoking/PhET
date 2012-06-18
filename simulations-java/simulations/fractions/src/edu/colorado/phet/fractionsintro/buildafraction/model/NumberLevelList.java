package edu.colorado.phet.fractionsintro.buildafraction.model;

import fj.F;
import fj.P2;
import fj.data.List;
import lombok.Data;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import edu.colorado.phet.fractionsintro.common.util.DefaultP2;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.colorado.phet.fractionsintro.matchinggame.model.Pattern;
import edu.colorado.phet.fractionsintro.matchinggame.model.Pattern.Polygon;
import edu.colorado.phet.fractionsintro.matchinggame.view.FilledPattern;

import static edu.colorado.phet.fractionsintro.common.view.Colors.*;
import static edu.colorado.phet.fractionsintro.intro.model.Fraction.*;
import static edu.colorado.phet.fractionsintro.matchinggame.view.FilledPattern.randomFill;
import static edu.colorado.phet.fractionsintro.matchinggame.view.FilledPattern.sequentialFill;
import static fj.Equal.intEqual;
import static fj.data.List.*;
import static java.awt.Color.orange;

/**
 * @author Sam Reid
 */
public class NumberLevelList extends ArrayList<NumberLevel> {
    static final Random random = new Random();

    public static abstract class PatternMaker extends F<Fraction, Pattern> {
        public F<Fraction, FilledPattern> sequential() {
            return new F<Fraction, FilledPattern>() {
                @Override public FilledPattern f( final Fraction fraction ) {
                    return sequentialFill( PatternMaker.this.f( fraction ), fraction.numerator );
                }
            };
        }

        public F<Fraction, FilledPattern> random() {
            return new F<Fraction, FilledPattern>() {
                @Override public FilledPattern f( final Fraction fraction ) {
                    return randomFill( PatternMaker.this.f( fraction ), fraction.numerator, random.nextLong() );
                }
            };
        }
    }

    public static PatternMaker pie = new PatternMaker() {
        @Override public Pattern f( final Fraction fraction ) {
            return Pattern.pie( fraction.denominator );
        }
    };
    public static PatternMaker horizontalBar = new PatternMaker() {
        @Override public Pattern f( final Fraction fraction ) {
            return Pattern.horizontalBars( fraction.denominator );
        }
    };
    public static PatternMaker verticalBar = new PatternMaker() {
        @Override public Pattern f( final Fraction fraction ) {
            return Pattern.verticalBars( fraction.denominator );
        }
    };
    public static PatternMaker pyramid1 = new PatternMaker() {
        @Override public Pattern f( final Fraction fraction ) {
            return Pattern.pyramidSingle();
        }
    };
    public static PatternMaker pyramid4 = new PatternMaker() {
        @Override public Pattern f( final Fraction fraction ) {
            return Pattern.pyramidFour();
        }
    };
    public static PatternMaker pyramid9 = new PatternMaker() {
        @Override public Pattern f( final Fraction fraction ) {
            return Pattern.pyramidNine();
        }
    };
    public static PatternMaker grid1 = new PatternMaker() {
        @Override public Pattern f( final Fraction fraction ) {
            return Pattern.grid( 1 );
        }
    };
    public static PatternMaker grid4 = new PatternMaker() {
        @Override public Pattern f( final Fraction fraction ) {
            return Pattern.grid( 2 );
        }
    };
    public static PatternMaker grid9 = new PatternMaker() {
        @Override public Pattern f( final Fraction fraction ) {
            return Pattern.grid( 3 );
        }
    };
    public static PatternMaker flower = new PatternMaker() {
        @Override public Pattern f( final Fraction fraction ) {
            return Pattern.sixFlower();
        }
    };
    public static PatternMaker polygon = new PatternMaker() {
        @Override public Pattern f( final Fraction fraction ) {
            return Polygon.createPolygon( 60, fraction.denominator );
        }
    };

    //Only choose from the universal patterns (that accept all denominators)
    public static final List<PatternMaker> universalTypes = list( pie, horizontalBar, verticalBar );
    public static final List<PatternMaker> all = list( pie, horizontalBar, verticalBar, pyramid1, pyramid4, pyramid9, grid1, grid4, grid9, flower, polygon );

    public NumberLevelList() {
        add( level0() );
        add( level1() );
        add( level2() );
        add( level3() );
        add( level4() );
        add( level5() );
        add( level6() );
        add( level7() );
        add( level8() );
        add( level9() );
    }

    /*Level 8:
    -- Representations both less than 1 and greater than 1
    -- All representations possible
    -- No card constraints (as in straightforward matching of number and picture possible)*/
    private NumberLevel level8() {
        //Choose 4 different patterns
        List<RepresentationType> types = choose( 4, RepresentationType.all );

        RandomColors4 colors = new RandomColors4();
        return new NumberLevel( true, list( targetLessThanOrEqualTo2( colors, types.index( 0 ), random.nextBoolean() ),
                                            targetLessThanOrEqualTo2( colors, types.index( 1 ), random.nextBoolean() ),
                                            targetLessThanOrEqualTo2( colors, types.index( 2 ), random.nextBoolean() ),
                                            targetLessThanOrEqualTo2( colors, types.index( 3 ), random.nextBoolean() ) ) );
    }

    /*Level 9:
-- Representations both less than 1 and greater than 1
-- All representations possible
-- No card constraints (as in straightforward matching of number and picture possible)*/
    private NumberLevel level9() {
        //Choose 4 different patterns
        List<RepresentationType> types = choose( 4, RepresentationType.all );

        RandomColors4 colors = new RandomColors4();
        final List<NumberTarget> targets = list( targetLessThanOrEqualTo1( colors, types.index( 0 ), random.nextBoolean() ),
                                                 targetLessThanOrEqualTo1( colors, types.index( 1 ), random.nextBoolean() ),
                                                 targetLessThanOrEqualTo2( colors, types.index( 2 ), random.nextBoolean() ),
                                                 targetLessThanOrEqualTo2( colors, types.index( 3 ), random.nextBoolean() ) );
        List<Fraction> targetFractions = targets.map( new F<NumberTarget, Fraction>() {
            @Override public Fraction f( final NumberTarget numberTarget ) {
                return numberTarget.fraction;
            }
        } );
        List<Fraction> scaled = targetFractions.zipWith( shuffle( list( 1, 1, 2, 3 ) ), _times );
        return new NumberLevel( true, scaled.map( _denominator ).append( scaled.map( _numerator ) ), targets );
    }

    //Choose a representation, pies or bars, but use the same representation for all things
    private NumberLevel level0() {
        RandomColors3 colors = new RandomColors3();
        return new NumberLevel( true, list( 1, 1, 2, 2, 3, 3 ), shuffle( list( NumberTarget.target( 1, 2, colors.next(), pie.sequential() ),
                                                                               NumberTarget.target( 1, 3, colors.next(), pie.sequential() ),
                                                                               NumberTarget.target( 2, 3, colors.next(), pie.sequential() ) ) ) );
    }

    /*
    Level 1:
    --Here I would begin choosing from a distribution of fractions ranging from 1/2 to 4/5.  As in the numerator could be 1, 2, 3, or 4 and the denominator could be 2, 3, 4, or 5 with the stipulation that the fraction is always less than 1.
    -- I might put the percentages for choosing the bar representations each at 30 percent
    -- I like how at these early levels it is all just a single representation
     */
    private NumberLevel level1() {
        final F<Fraction, FilledPattern> representation = new Distribution<F<Fraction, FilledPattern>>() {{
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
        return new NumberLevel( true, shuffle( selectedList.map( new F<Fraction, NumberTarget>() {
            @Override public NumberTarget f( final Fraction fraction ) {
                return NumberTarget.target( fraction, colors.remove( 0 ), representation );
            }
        } ) ) );
    }

    private NumberLevel level2() {
        ArrayList<Integer> numerators = new ArrayList<Integer>( Arrays.asList( 1, 2, 3, 4, 5 ) );
        Collections.shuffle( numerators );

        //TODO: guarantee this level is solvable, possibly by generating cards to make it so
        RandomColors3 colors = new RandomColors3();
        return new NumberLevel( true, list( 1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 7, 7, 7, 8, 8, 8, 9, 9, 9 ), shuffle( list( NumberTarget.target( numerators.get( 0 ), 6, colors.next(), flower.sequential() ),
                                                                                                                                           NumberTarget.target( numerators.get( 1 ), 6, colors.next(), flower.sequential() ),
                                                                                                                                           NumberTarget.target( numerators.get( 2 ), 6, colors.next(), flower.sequential() ) ) ) );
    }

    private NumberLevel level3() {
        RandomColors3 colors = new RandomColors3();
        return new NumberLevel( true, list( NumberTarget.target( chooseOne( rangeInclusive( 1, 1 ) ), 1, colors.next(), pyramid1.sequential() ),
                                            NumberTarget.target( chooseOne( rangeInclusive( 1, 4 ) ), 4, colors.next(), pyramid4.sequential() ),
                                            NumberTarget.target( chooseOne( rangeInclusive( 1, 9 ) ), 9, colors.next(), pyramid9.sequential() ) ) );
    }


    //pie, grid, horizontalBars, verticalBars, flower, polygon
    static @Data abstract class RepresentationType {
        public final List<Integer> denominators;

        public static final RepresentationType pies = new RepresentationType( rangeInclusive( 1, 9 ) ) {
            @Override public PatternMaker toPattern( final Fraction fraction ) {
                return NumberLevelList.pie;
            }
        };
        public static final RepresentationType horizontalBars = new RepresentationType( rangeInclusive( 1, 9 ) ) {
            @Override public PatternMaker toPattern( final Fraction fraction ) {
                return horizontalBar;
            }
        };
        public static final RepresentationType verticalBars = new RepresentationType( rangeInclusive( 1, 9 ) ) {
            @Override public PatternMaker toPattern( final Fraction fraction ) {
                return verticalBar;
            }
        };
        public static final RepresentationType grid = new RepresentationType( list( 1, 4, 9 ) ) {
            @Override public PatternMaker toPattern( final Fraction fraction ) {
                return fraction.denominator == 1 ? grid1 :
                       fraction.denominator == 4 ? grid4 :
                       grid9;
            }
        };
        public static final RepresentationType pyramid = new RepresentationType( list( 1, 4, 9 ) ) {
            @Override public PatternMaker toPattern( final Fraction fraction ) {
                return fraction.denominator == 1 ? pyramid1 :
                       fraction.denominator == 4 ? pyramid4 :
                       pyramid9;
            }
        };
        public static final RepresentationType flower = new RepresentationType( list( 6 ) ) {
            @Override public PatternMaker toPattern( final Fraction fraction ) {
                return NumberLevelList.flower;
            }
        };
        public static final RepresentationType polygon = new RepresentationType( rangeInclusive( 3, 9 ) ) {
            @Override public PatternMaker toPattern( final Fraction fraction ) {
                return NumberLevelList.polygon;
            }
        };

        public static List<RepresentationType> all = list( pies, horizontalBars, verticalBars, grid, pyramid, flower, polygon );

        public abstract PatternMaker toPattern( final Fraction fraction );
    }

    /*Level 4:
--At his point I think we should switch to 4 bins for all future levels
- numerator able to range from 1-9, and denominator able to range from 1-9, with the number less than 1
- all representations possible (circle, "9 and 4 square", bars, pyramids, 6 flower, perhaps regular polygons), I don't think we need to get too funky in the representations like we did in the match game
- all cards available to fulfill challenges in the most straightforward way, for instance a 4/5 representation has a 4 and a 5 available.*/
    private NumberLevel level4() {
        List<RepresentationType> types = RepresentationType.all;
        RandomColors4 colors = new RandomColors4();
        return new NumberLevel( true, list( targetLessThanOrEqualTo1( colors, chooseOne( types ), true ),
                                            targetLessThanOrEqualTo1( colors, chooseOne( types ), true ),
                                            targetLessThanOrEqualTo1( colors, chooseOne( types ), true ),
                                            targetLessThanOrEqualTo1( colors, chooseOne( types ), true ) ) );
    }

    /* Level 5:
    --Same as level 4, but now random fill is possible (maybe with 50 percent probability of being used)
    --could begin to introduce some card constraints at this point, for instance making sure that one of the representations
    only has cards available to match it with a "nonobvious fraction".
    For instance if 3/9 appears, and 5/9 appears, we have 1(5) and 1(9), but not 2(9), so that 1/3 would need to be used to match. */
    private NumberLevel level5() {
        List<RepresentationType> types = RepresentationType.all;
        RandomColors4 colors = new RandomColors4();
        return NumberLevel.numberLevelReduced( true, list( targetLessThanOrEqualTo1( colors, chooseOne( types ), random.nextBoolean() ),
                                                           targetLessThanOrEqualTo1( colors, chooseOne( types ), random.nextBoolean() ),
                                                           targetLessThanOrEqualTo1( colors, chooseOne( types ), random.nextBoolean() ),
                                                           targetLessThanOrEqualTo1( colors, chooseOne( types ), random.nextBoolean() ) ) );
    }

    /* Level 6:
    --Top two representations are equivalent, and bottom 2 representations are equivalent but still numbers less than 1
    -- A built in check to draw a different fraction for the top 2 and the bottom 2
    -- Possible fractions sets from which to draw 2 each {1/2, 2/4, 3/6} , {1/3, 2/6, 3/9}, {2/3, 4/6, 3/9}, {1/4, 2/8}, {3/4, 6/8}
    -- I think the representations should both be equal, for instance, 2 pies divided the same, and two bars divided the same, so that the learning goal is focused on the same exact picture can be represented by 2 different fractions. Probably always displaying the reduced fraction as the picture.
    -- Cards constrained, so for instance if {1/2, 3/6} is drawn for the top pair and {3/4, 6/8} drawn for the bottom, we would have 1(1), 1(2), 2(3), 1(4), 2(6), 1(8) */
    private NumberLevel level6() {
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
        Fraction topPrototype = moreReduced( top._1(), top._2() );
        Fraction bottomPrototype = moreReduced( bottom._1(), bottom._2() );

        List<Fraction> selectedFractions = list( top._1(), top._2(), bottom._1(), bottom._2() );
        final List<NumberTarget> targets = list( NumberTarget.target( topPrototype, topColor, selected._1().sequential() ),
                                                 NumberTarget.target( topPrototype, topColor, selected._1().sequential() ),
                                                 NumberTarget.target( bottomPrototype, bottomColor, selected._2().sequential() ),
                                                 NumberTarget.target( bottomPrototype, bottomColor, selected._2().sequential() ) );

        //But choose numbers from the original fractions
        final List<Integer> cards = selectedFractions.map( _numerator ).append( selectedFractions.map( _denominator ) );
        return new NumberLevel( true, cards, targets );
    }

    /*Level 7:
    -- Introduce double representations at this level (numbers greater than 1)
    -- I really like your idea of having 8 cards, 4 each of 2 numbers
    -- Lets randomly choose from  {2/3, 3/2, 2/2, 3/3}, {2/4, 4/2, 2/2, 4/4}, {3/4,4/3, 3/3, 4/4}, {3/5, 5/3, 3/3, 5/5}, {3/6, 6/3, 3/3, 6/6}*/
    private NumberLevel level7() {
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
        return new NumberLevel( true, targets );
    }

    private Fraction moreReduced( final Fraction a, final Fraction b ) { return b.numerator < a.numerator ? b : a; }

    private NumberTarget targetLessThanOrEqualTo1( final RandomColors4 colors, final RepresentationType representationType, boolean sequential ) {
        int denominator = chooseOne( representationType.denominators );
        int numerator = chooseOne( rangeInclusive( 1, denominator ) );
        Fraction fraction = new Fraction( numerator, denominator );
        final PatternMaker patternMaker = representationType.toPattern( fraction );
        return NumberTarget.target( fraction, colors.next(), sequential ? patternMaker.sequential() : patternMaker.random() );
    }

    private NumberTarget targetLessThanOrEqualTo2( final RandomColors4 colors, final RepresentationType representationType, boolean sequential ) {
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

    private static List<Color> shuffledColors() {return shuffle( list( LIGHT_RED, LIGHT_GREEN, LIGHT_BLUE, orange ) );}

    public static <T> List<T> shuffle( final List<T> list ) {
        ArrayList<T> collection = new ArrayList<T>( list.toCollection() );
        Collections.shuffle( collection );
        return iterableList( collection );
    }

    private static List<Integer> rangeInclusive( final int a, final int b ) { return range( a, b + 1 ); }

    private Fraction chooseFraction( final List<Integer> allowedNumerators, final List<Integer> allowedDenominators, F<Fraction, Boolean> accept ) {
        for ( int i = 0; i < 1000; i++ ) {
            Fraction fraction = fraction( chooseOne( allowedNumerators ), chooseOne( allowedDenominators ) );
            if ( accept.f( fraction ) ) {
                return fraction;
            }
        }
        throw new RuntimeException( "Couldn't find a match" );
    }

    private static <T> T chooseOne( final List<T> list ) { return list.index( random.nextInt( list.length() ) ); }

    //Chooses 2 different values (i.e. without replacement)
    private static <T> P2<T, T> chooseTwo( final List<T> list ) {
        final List<Integer> indices = range( 0, list.length() );
        int firstIndexToChoose = chooseOne( indices );
        int secondIndexToChoose = chooseOne( indices.delete( firstIndexToChoose, intEqual ) );
        return new DefaultP2<T, T>( list.index( firstIndexToChoose ), list.index( secondIndexToChoose ) );
    }

    //Select the specified number without replacement
    private static <T> List<T> choose( int num, final List<T> list ) {
        ArrayList<T> mutableList = new ArrayList<T>( list.toCollection() );
        Collections.shuffle( mutableList );
        while ( mutableList.size() > num ) {
            mutableList.remove( 0 );
        }
        return iterableList( mutableList );
    }
}