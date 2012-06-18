package edu.colorado.phet.fractionsintro.buildafraction.model;

import fj.Equal;
import fj.F;
import fj.data.List;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.colorado.phet.fractionsintro.matchinggame.model.Pattern;
import edu.colorado.phet.fractionsintro.matchinggame.view.FilledPattern;

import static edu.colorado.phet.fractionsintro.buildafraction.model.NumberTarget.target;
import static edu.colorado.phet.fractionsintro.matchinggame.view.FilledPattern.sequentialFill;
import static fj.data.List.*;
import static java.awt.Color.*;

/**
 * Model for the Build a Fraction tab.
 *
 * @author Sam Reid
 */
public class BuildAFractionModel {
    public static final Color lightBlue = new Color( 100, 100, 255 );

    public final IntegerProperty numberLevel = new IntegerProperty( 0 );
    public final IntegerProperty pictureLevel = new IntegerProperty( 0 );

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
    public final IntegerProperty numberScore = new IntegerProperty( 0 );

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

    public final ArrayList<NumberLevel> numberLevels = createNumberLevels();

    private ArrayList<NumberLevel> createNumberLevels() {
        return new ArrayList<NumberLevel>() {{
            for ( int i = 0; i < 10; i++ ) {
                add( i == 0 ? numberLevel0() :
                     i == 1 ? numberLevel1() :
                     i == 2 ? new NumberLevel( true, list( 1, 2, 3, 3, 3, 4, 4, 5, 6, 6, 7, 8, 9 ), shuffle( list( target( 2, 6, red, flower ),
                                                                                                                   target( 3, 6, green, flower ),
                                                                                                                   target( 4, 6, lightBlue, flower ) ) ) ) :
                     i == 3 ? new NumberLevel( true, list( 1, 1, 2, 3, 3, 3, 4, 5, 6, 7, 8, 9 ), list( target( 1, 1, red, pyramid1 ),
                                                                                                       target( 3, 4, green, pyramid4 ),
                                                                                                       target( 5, 9, lightBlue, pyramid9 ) ) ) :
                     i == 4 ? new NumberLevel( true, list( 4, 3, 3, 2, 2, 1, 5, 6, 7, 8, 9 ), shuffle( list( target( 4, 3, red, pie ),
                                                                                                             target( 3, 2, green, pie ),
                                                                                                             target( 2, 1, lightBlue, pie ) ) ) ) :
                     numberLevel5()
                );
            }
        }};
    }

    public final ArrayList<PictureLevel> pictureLevels = new ArrayList<PictureLevel>() {{
        for ( int i = 0; i < 10; i++ ) {
            add( i == 0 ? pictureLevel0() :
                 pictureLevel0()
            );
        }
    }};

    private PictureLevel pictureLevel0() {
        return new PictureLevel( list( 1, 2, 3, 4, 5, 6 ), list( new PictureTarget( new Fraction( 1, 1 ) ), new PictureTarget( new Fraction( 1, 2 ) ), new PictureTarget( new Fraction( 2, 3 ) ) ) );
    }

    public void removeCreatedValueFromNumberLevel( final Fraction value ) {
        final Property<List<Fraction>> fractions = numberLevels.get( numberLevel.get() ).createdFractions;
        fractions.set( fractions.get().delete( value, Equal.<Fraction>anyEqual() ) );
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

    private List<Color> shuffledColors() {return shuffle( list( red, green, lightBlue, orange ) );}

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
        numberLevels.clear();
        numberLevels.addAll( createNumberLevels() );
    }

    public void addCreatedValue( final Fraction value ) {
        final Property<List<Fraction>> fractions = numberLevels.get( numberLevel.get() ).createdFractions;
        fractions.set( fractions.get().snoc( value ) );
    }

    public Property<List<Fraction>> getCreatedFractions( final int level ) {
        return numberLevels.get( level ).createdFractions;
    }

    public NumberLevel getNumberLevel( final int level ) { return numberLevels.get( level ); }

    public PictureLevel getPictureLevel( final int level ) { return pictureLevels.get( level ); }

    public void goToNumberLevel( final int level ) { numberLevel.set( level ); }
}