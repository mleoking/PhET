package edu.colorado.phet.fractionsintro.buildafraction.model;

import fj.F;
import fj.data.List;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;

import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.colorado.phet.fractionsintro.matchinggame.model.Pattern;
import edu.colorado.phet.fractionsintro.matchinggame.view.FilledPattern;

import static edu.colorado.phet.fractionsintro.buildafraction.model.NumberTarget.target;
import static edu.colorado.phet.fractionsintro.common.view.Colors.*;
import static edu.colorado.phet.fractionsintro.matchinggame.view.FilledPattern.sequentialFill;
import static fj.data.List.iterableList;
import static fj.data.List.list;
import static java.awt.Color.orange;

/**
 * @author Sam Reid
 */
public class NumberLevelList extends ArrayList<NumberLevel> {
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
    public static F<Fraction, FilledPattern> flower = new F<Fraction, FilledPattern>() {
        @Override public FilledPattern f( final Fraction f ) {
            return sequentialFill( Pattern.sixFlower(), f.numerator );
        }
    };

    public NumberLevelList() {
        for ( int i = 0; i < 10; i++ ) {
            add( i == 0 ? numberLevel0() :
                 i == 1 ? numberLevel1() :
                 i == 2 ? new NumberLevel( true, list( 1, 2, 3, 3, 3, 4, 4, 5, 6, 6, 7, 8, 9 ), shuffle( list( target( 2, 6, LIGHT_RED, flower ),
                                                                                                               target( 3, 6, LIGHT_GREEN, flower ),
                                                                                                               target( 4, 6, LIGHT_BLUE, flower ) ) ) ) :
                 i == 3 ? new NumberLevel( true, list( 1, 1, 2, 3, 3, 3, 4, 5, 6, 7, 8, 9 ), list( target( 1, 1, LIGHT_RED, pyramid1 ),
                                                                                                   target( 3, 4, LIGHT_GREEN, pyramid4 ),
                                                                                                   target( 5, 9, LIGHT_BLUE, pyramid9 ) ) ) :
                 i == 4 ? new NumberLevel( true, list( 4, 3, 3, 2, 2, 1, 5, 6, 7, 8, 9 ), shuffle( list( target( 4, 3, LIGHT_RED, pie ),
                                                                                                         target( 3, 2, LIGHT_GREEN, pie ),
                                                                                                         target( 2, 1, LIGHT_BLUE, pie ) ) ) ) :
                 numberLevel5()
            );
        }
    }

    //Choose a representation, pies or bars, but use the same representation for all things
    private NumberLevel numberLevel0() {
        List<Color> colors = shuffledColors();
        return new NumberLevel( true, list( 1, 1, 2, 2, 3, 3 ), shuffle( list( target( 1, 2, colors.index( 0 ), pie ),
                                                                               target( 1, 3, colors.index( 1 ), pie ),
                                                                               target( 2, 3, colors.index( 2 ), pie ) ) ) );
    }

    private NumberLevel numberLevel1() {
        F<Fraction, FilledPattern> representation = new Distribution<F<Fraction, FilledPattern>>() {{
            put( horizontalBar, 20 );
            put( verticalBar, 20 );
        }}.draw();
        List<Color> colors = shuffledColors();
        return new NumberLevel( true, list( 1, 1, 2, 2, 3, 3, 4, 4, 5, 5 ), shuffle( list( target( 2, 3, colors.index( 0 ), representation ),
                                                                                           target( 3, 4, colors.index( 1 ), representation ),
                                                                                           target( 4, 5, colors.index( 2 ), representation ) ) ) );
    }

    private NumberLevel numberLevel5() {
        Distribution<F<Fraction, FilledPattern>> representation = new Distribution<F<Fraction, FilledPattern>>() {{
            put( horizontalBar, 20 );
            put( verticalBar, 20 );
            put( pie, 30 );
        }};
        List<Color> colors = shuffledColors();
        return new NumberLevel( false, shuffle( list( target( 3, 2, colors.index( 0 ), representation.draw() ),
                                                      target( 2, 3, colors.index( 1 ), representation.draw() ),
                                                      target( 5, 4, colors.index( 2 ), representation.draw() ),
                                                      target( 4, 5, colors.index( 3 ), representation.draw() ) ) ) );
    }

    private List<Color> shuffledColors() {return shuffle( list( LIGHT_RED, LIGHT_GREEN, LIGHT_BLUE, orange ) );}

    private static <T> List<T> shuffle( final List<T> list ) {
        ArrayList<T> collection = new ArrayList<T>( list.toCollection() );
        Collections.shuffle( collection );
        return iterableList( collection );
    }
}