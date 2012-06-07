package edu.colorado.phet.fractionsintro.buildafraction.model;

import fj.Equal;
import fj.F;
import fj.data.List;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.colorado.phet.fractionsintro.matchinggame.model.Pattern;
import edu.colorado.phet.fractionsintro.matchinggame.model.Pattern.Pyramid;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.FilledPattern;

import static edu.colorado.phet.fractionsintro.buildafraction.model.Target.target;
import static edu.colorado.phet.fractionsintro.matchinggame.view.fractions.FilledPattern.sequentialFill;
import static fj.data.List.*;
import static java.awt.Color.green;
import static java.awt.Color.red;

/**
 * Model for the Build a Fraction tab.
 *
 * @author Sam Reid
 */
public class BuildAFractionModel {
    public static final Color lightBlue = new Color( 100, 100, 255 );

    public final Property<Integer> numberLevel = new Property<Integer>( 0 );
    public final Property<Integer> pictureLevel = new Property<Integer>( 0 );

    public final ConstantDtClock clock = new ConstantDtClock();
    public final Property<Scene> selectedScene = new Property<Scene>( Scene.numbers );

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
            return sequentialFill( Pyramid.single(), f.numerator );
        }
    };
    public static F<Fraction, FilledPattern> pyramid4 = new F<Fraction, FilledPattern>() {
        @Override public FilledPattern f( final Fraction f ) {
            return sequentialFill( Pyramid.four(), f.numerator );
        }
    };
    public static F<Fraction, FilledPattern> pyramid9 = new F<Fraction, FilledPattern>() {
        @Override public FilledPattern f( final Fraction f ) {
            return sequentialFill( Pyramid.nine(), f.numerator );
        }
    };
    public static F<Fraction, FilledPattern> flower = new F<Fraction, FilledPattern>() {
        @Override public FilledPattern f( final Fraction f ) {
            return sequentialFill( Pattern.sixFlower(), f.numerator );
        }
    };

    //Create a list of filled patterns from a single pattern type
    public static F<Fraction, List<FilledPattern>> composite( final F<Fraction, FilledPattern> element ) {
        return new F<Fraction, List<FilledPattern>>() {
            @Override public List<FilledPattern> f( final Fraction fraction ) {

                List<FilledPattern> result = nil();
                int numerator = fraction.numerator;
                int denominator = fraction.denominator;
                while ( numerator > denominator ) {
                    result = result.snoc( element.f( new Fraction( denominator, denominator ) ) );
                    numerator = numerator - denominator;
                }
                if ( numerator > 0 ) {
                    result = result.snoc( element.f( new Fraction( numerator, denominator ) ) );
                }
                return result;
            }
        };
    }

    public final ArrayList<NumberLevel> numberLevels = new ArrayList<NumberLevel>() {{
        for ( int i = 0; i < 10; i++ ) {
            add( i == 0 ? level0() :
                 i == 1 ? level1() :
                 i == 2 ? new NumberLevel( list( 0, 1, 2, 3, 3, 3, 4, 4, 5, 6, 6, 7, 8, 9 ), shuffle( list( target( 2, 6, red, flower ),
                                                                                                            target( 3, 6, green, flower ),
                                                                                                            target( 4, 6, lightBlue, flower ) ) ) ) :
                 i == 3 ? new NumberLevel( list( 0, 1, 1, 2, 3, 3, 3, 4, 5, 6, 7, 8, 9 ), list( target( 1, 1, red, pyramid1 ),
                                                                                                target( 3, 4, green, pyramid4 ),
                                                                                                target( 5, 9, lightBlue, pyramid9 ) ) ) :
                 new NumberLevel( list( 4, 3, 3, 2, 2, 1, 0, 5, 6, 7, 8, 9 ), shuffle( list( target( 4, 3, red, pie ),
                                                                                             target( 3, 2, green, pie ),
                                                                                             target( 2, 1, lightBlue, pie ) ) ) )
            );
        }
    }};

    public void nextNumberLevel() { numberLevel.set( numberLevel.get() + 1 ); }

    public void removeCreatedValueFromNumberLevel( final Fraction value ) {
        final Property<List<Fraction>> fractions = numberLevels.get( numberLevel.get() ).createdFractions;
        fractions.set( fractions.get().delete( value, Equal.<Fraction>anyEqual() ) );
    }

    private NumberLevel level1() {
        F<Fraction, FilledPattern> representation = new Distribution<F<Fraction, FilledPattern>>() {{
            put( horizontalBar, 20 );
            put( verticalBar, 20 );
        }}.draw();
        List<Color> colors = shuffledColors();
        return new NumberLevel( list( 1, 1, 2, 2, 3, 3, 4, 4, 5, 5 ), shuffle( list( target( 2, 3, colors.index( 0 ), representation ),
                                                                                     target( 3, 4, colors.index( 1 ), representation ),
                                                                                     target( 4, 5, colors.index( 2 ), representation ) ) ) );
    }

    //Choose a representation, pies or bars, but use the same representation for all things
    private NumberLevel level0() {
        List<Color> colors = shuffledColors();
        return new NumberLevel( list( 1, 1, 2, 2, 3, 3 ), shuffle( list( target( 1, 2, colors.index( 0 ), pie ),
                                                                         target( 1, 3, colors.index( 1 ), pie ),
                                                                         target( 2, 3, colors.index( 2 ), pie ) ) ) );
    }

    private List<Color> shuffledColors() {return shuffle( list( red, green, lightBlue ) );}

    private static <T> List<T> shuffle( final List<T> list ) {
        ArrayList<T> collection = new ArrayList<T>( list.toCollection() );
        Collections.shuffle( collection );
        return iterableList( collection );
    }

    public void resetAll() {
        selectedScene.reset();
        clock.resetSimulationTime();
        numberLevel.reset();
        for ( NumberLevel x : numberLevels ) {
            x.resetAll();
        }
    }

    public void addCreatedValue( final Fraction value ) {
        final Property<List<Fraction>> fractions = numberLevels.get( numberLevel.get() ).createdFractions;
        fractions.set( fractions.get().snoc( value ) );
    }

    public Property<List<Fraction>> getCreatedFractions( final int level ) {
        return numberLevels.get( level ).createdFractions;
    }

    public NumberLevel getNumberLevel( final int level ) { return numberLevels.get( level ); }
}