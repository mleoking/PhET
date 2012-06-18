package edu.colorado.phet.fractionsintro.buildafraction.model;

import fj.F;
import fj.data.List;
import lombok.Data;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.colorado.phet.fractionsintro.matchinggame.model.Pattern;
import edu.colorado.phet.fractionsintro.matchinggame.model.Pattern.Polygon;
import edu.colorado.phet.fractionsintro.matchinggame.view.FilledPattern;

import static edu.colorado.phet.fractionsintro.buildafraction.model.NumberTarget.target;
import static edu.colorado.phet.fractionsintro.common.view.Colors.*;
import static edu.colorado.phet.fractionsintro.matchinggame.view.FilledPattern.sequentialFill;
import static fj.data.List.*;
import static java.awt.Color.orange;

/**
 * @author Sam Reid
 */
public class NumberLevelList extends ArrayList<NumberLevel> {
    static final Random random = new Random();
    public static F<Fraction, FilledPattern> pie = new F<Fraction, FilledPattern>() {
        @Override public FilledPattern f( final Fraction f ) {
            return sequentialFill( Pattern.pie( f.denominator ), f.numerator );
        }
    };
    public static F<Fraction, FilledPattern> horizontalBar = new F<Fraction, FilledPattern>() {
        @Override public FilledPattern f( final Fraction f ) {
            return sequentialFill( Pattern.horizontalBars( f.denominator ), f.numerator );
        }
    };
    public static F<Fraction, FilledPattern> verticalBar = new F<Fraction, FilledPattern>() {
        @Override public FilledPattern f( final Fraction f ) {
            return sequentialFill( Pattern.verticalBars( f.denominator ), f.numerator );
        }
    };
    public static F<Fraction, FilledPattern> pyramid1 = new F<Fraction, FilledPattern>() {
        @Override public FilledPattern f( final Fraction f ) {
            return sequentialFill( Pattern.pyramidSingle(), f.numerator );
        }
    };
    public static F<Fraction, FilledPattern> pyramid4 = new F<Fraction, FilledPattern>() {
        @Override public FilledPattern f( final Fraction f ) {
            return sequentialFill( Pattern.pyramidFour(), f.numerator );
        }
    };
    public static F<Fraction, FilledPattern> pyramid9 = new F<Fraction, FilledPattern>() {
        @Override public FilledPattern f( final Fraction f ) {
            return sequentialFill( Pattern.pyramidNine(), f.numerator );
        }
    };

    public static F<Fraction, FilledPattern> grid1 = new F<Fraction, FilledPattern>() {
        @Override public FilledPattern f( final Fraction f ) {
            return sequentialFill( Pattern.grid( 1 ), f.numerator );
        }
    };
    public static F<Fraction, FilledPattern> grid4 = new F<Fraction, FilledPattern>() {
        @Override public FilledPattern f( final Fraction f ) {
            return sequentialFill( Pattern.grid( 2 ), f.numerator );
        }
    };
    public static F<Fraction, FilledPattern> grid9 = new F<Fraction, FilledPattern>() {
        @Override public FilledPattern f( final Fraction f ) {
            return sequentialFill( Pattern.grid( 3 ), f.numerator );
        }
    };
    public static F<Fraction, FilledPattern> flower = new F<Fraction, FilledPattern>() {
        @Override public FilledPattern f( final Fraction f ) {
            return sequentialFill( Pattern.sixFlower(), f.numerator );
        }
    };
    public static F<Fraction, FilledPattern> polygon = new F<Fraction, FilledPattern>() {
        @Override public FilledPattern f( final Fraction f ) {
            return sequentialFill( Polygon.createPolygon( 60, f.denominator ), f.numerator );
        }
    };

    public NumberLevelList() {
        for ( int i = 0; i < 10; i++ ) {
            add( i == 0 ? level0() :
                 i == 1 ? level1() :
                 i == 2 ? level2() :
                 i == 3 ? level3() :
                 i == 4 ? level4() :
                 numberLevel5()
            );
        }
    }

    //Choose a representation, pies or bars, but use the same representation for all things
    private NumberLevel level0() {
        RandomColors3 colors = new RandomColors3();
        return new NumberLevel( true, list( 1, 1, 2, 2, 3, 3 ), shuffle( list( target( 1, 2, colors.next(), pie ),
                                                                               target( 1, 3, colors.next(), pie ),
                                                                               target( 2, 3, colors.next(), pie ) ) ) );
    }

    /*
    Level 1:
    --Here I would begin choosing from a distribution of fractions ranging from 1/2 to 4/5.  As in the numerator could be 1, 2, 3, or 4 and the denominator could be 2, 3, 4, or 5 with the stipulation that the fraction is always less than 1.
    -- I might put the percentages for choosing the bar representations each at 30 percent
    -- I like how at these early levels it is all just a single representation
     */
    private NumberLevel level1() {
        final F<Fraction, FilledPattern> representation = new Distribution<F<Fraction, FilledPattern>>() {{
            put( pie, 40 );
            put( horizontalBar, 30 );
            put( verticalBar, 30 );
        }}.draw();
        final ArrayList<Fraction> selected = new ArrayList<Fraction>();
        for ( int i = 0; i < 3; i++ ) {
            final Fraction fraction = chooseFraction( range( 1, 4 + 1 ), range( 2, 5 + 1 ), new F<Fraction, Boolean>() {
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
                return target( fraction, colors.remove( 0 ), representation );
            }
        } ) ) );
    }

    private NumberLevel level2() {
        ArrayList<Integer> numerators = new ArrayList<Integer>( Arrays.asList( 1, 2, 3, 4, 5 ) );
        Collections.shuffle( numerators );

        //TODO: guarantee this level is solvable, possibly by generating cards to make it so
        RandomColors3 colors = new RandomColors3();
        return new NumberLevel( true, list( 1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 7, 7, 7, 8, 8, 8, 9, 9, 9 ), shuffle( list( target( numerators.get( 0 ), 6, colors.next(), flower ),
                                                                                                                                           target( numerators.get( 1 ), 6, colors.next(), flower ),
                                                                                                                                           target( numerators.get( 2 ), 6, colors.next(), flower ) ) ) );
    }

    private NumberLevel level3() {
        RandomColors3 colors = new RandomColors3();
        return new NumberLevel( true, list( target( chooseOne( rangeInclusive( 1, 1 ) ), 1, colors.next(), pyramid1 ),
                                            target( chooseOne( rangeInclusive( 1, 4 ) ), 4, colors.next(), pyramid4 ),
                                            target( chooseOne( rangeInclusive( 1, 9 ) ), 9, colors.next(), pyramid9 ) ) );
    }


    //pie, grid, horizontalBars, verticalBars, flower, polygon
    static @Data abstract class RepresentationType {
        public final List<Integer> denominators;

        public static final RepresentationType pies = new RepresentationType( rangeInclusive( 1, 9 ) ) {
            @Override public F<Fraction, FilledPattern> toPattern( final Fraction fraction ) {
                return pie;
            }
        };
        public static final RepresentationType horizontalBars = new RepresentationType( rangeInclusive( 1, 9 ) ) {
            @Override public F<Fraction, FilledPattern> toPattern( final Fraction fraction ) {
                return horizontalBar;
            }
        };
        public static final RepresentationType verticalBars = new RepresentationType( rangeInclusive( 1, 9 ) ) {
            @Override public F<Fraction, FilledPattern> toPattern( final Fraction fraction ) {
                return verticalBar;
            }
        };
        public static final RepresentationType grid = new RepresentationType( list( 1, 4, 9 ) ) {
            @Override public F<Fraction, FilledPattern> toPattern( final Fraction fraction ) {
                return fraction.denominator == 1 ? grid1 :
                       fraction.denominator == 4 ? grid4 :
                       grid9;
            }
        };
        public static final RepresentationType pyramid = new RepresentationType( list( 1, 4, 9 ) ) {
            @Override public F<Fraction, FilledPattern> toPattern( final Fraction fraction ) {
                return fraction.denominator == 1 ? pyramid1 :
                       fraction.denominator == 4 ? pyramid4 :
                       pyramid9;
            }
        };
        public static final RepresentationType flower = new RepresentationType( list( 6 ) ) {
            @Override public F<Fraction, FilledPattern> toPattern( final Fraction fraction ) {
                return NumberLevelList.flower;
            }
        };
        public static final RepresentationType polygon = new RepresentationType( rangeInclusive( 3, 9 ) ) {
            @Override public F<Fraction, FilledPattern> toPattern( final Fraction fraction ) {
                return NumberLevelList.polygon;
            }
        };

        public static List<RepresentationType> all = list( pies, horizontalBars, verticalBars, grid, pyramid, flower, polygon );

        public abstract F<Fraction, FilledPattern> toPattern( final Fraction fraction );
    }


    /*Level 4:
--At his point I think we should switch to 4 bins for all future levels
- numerator able to range from 1-9, and denominator able to range from 1-9, with the number less than 1
- all representations possible (circle, "9 and 4 square", bars, pyramids, 6 flower, perhaps regular polygons), I don't think we need to get too funky in the representations like we did in the match game
- all cards available to fulfill challenges in the most straightforward way, for instance a 4/5 representation has a 4 and a 5 available.*/
    private NumberLevel level4() {
        List<RepresentationType> types = RepresentationType.all;
        RandomColors4 colors = new RandomColors4();
        return new NumberLevel( true, list( generateFromType( colors, chooseOne( types ) ),
                                            generateFromType( colors, chooseOne( types ) ),
                                            generateFromType( colors, chooseOne( types ) ),
                                            generateFromType( colors, chooseOne( types ) ) ) );
    }

    private NumberTarget generateFromType( final RandomColors4 colors, final RepresentationType representationType ) {
        int denominator = chooseOne( representationType.denominators );
        int numerator = chooseOne( rangeInclusive( 1, denominator ) );
        Fraction fraction = new Fraction( numerator, denominator );
        return target( fraction, colors.next(), representationType.toPattern( fraction ) );
    }

    private static <T> T chooseOne( final List<T> list ) { return list.index( random.nextInt( list.length() ) ); }

    private NumberLevel numberLevel5() {
        Distribution<F<Fraction, FilledPattern>> representation = new Distribution<F<Fraction, FilledPattern>>() {{
            put( horizontalBar, 20 );
            put( verticalBar, 20 );
            put( pie, 30 );
        }};
        RandomColors4 colors = new RandomColors4();
        return new NumberLevel( false, shuffle( list( target( 3, 2, colors.next(), representation.draw() ),
                                                      target( 2, 3, colors.next(), representation.draw() ),
                                                      target( 5, 4, colors.next(), representation.draw() ),
                                                      target( 4, 5, colors.next(), representation.draw() ) ) ) );
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
            Fraction fraction = Fraction.fraction( chooseOne( allowedNumerators ), chooseOne( allowedDenominators ) );
            if ( accept.f( fraction ) ) {
                return fraction;
            }
        }
        throw new RuntimeException( "Couldn't find a match" );
    }

}