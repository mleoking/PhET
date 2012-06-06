package edu.colorado.phet.fractionsintro.buildafraction.model;

import fj.Equal;
import fj.F;
import fj.data.List;

import java.awt.Color;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.colorado.phet.fractionsintro.matchinggame.model.Pattern.Pyramid;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.FilledPattern;

import static edu.colorado.phet.fractionsintro.buildafraction.view.Target.target;
import static edu.colorado.phet.fractionsintro.matchinggame.model.Pattern.pie;
import static edu.colorado.phet.fractionsintro.matchinggame.model.Pattern.sixFlower;
import static edu.colorado.phet.fractionsintro.matchinggame.view.fractions.FilledPattern.sequentialFill;
import static fj.data.List.list;
import static fj.data.List.nil;
import static java.awt.Color.green;
import static java.awt.Color.red;

/**
 * Model for the Build a Fraction tab.
 *
 * @author Sam Reid
 */
public class BuildAFractionModel {

    public void nextLevel() {
        level.set( level.get() + 1 );
    }

    public void removeCreatedValue( final Fraction value ) {
        final Property<List<Fraction>> fractions = levels.get( level.get() ).createdFractions;
        fractions.set( fractions.get().delete( value, Equal.<Fraction>anyEqual() ) );
    }

    public final ConstantDtClock clock = new ConstantDtClock();
    public final Property<Scene> selectedScene = new Property<Scene>( Scene.numbers );

    public static F<Fraction, FilledPattern> pie = new F<Fraction, FilledPattern>() {
        @Override public FilledPattern f( final Fraction f ) {
            return sequentialFill( pie( f.denominator ), f.numerator );
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
            return sequentialFill( sixFlower(), f.numerator );
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

    public final ArrayList<Level> levels = new ArrayList<Level>() {{
        for ( int i = 0; i < 10; i++ ) {
            final Color lightBlue = new Color( 100, 100, 255 );
            add( i == 0 ? new Level( list( 1, 1, 2, 2, 3, 3 ), list( target( 1, 2, red, pie ),
                                                                     target( 1, 3, green, pie ),
                                                                     target( 2, 3, lightBlue, pie ) ) ) :
                 i == 1 ? new Level( list( 1, 1, 2, 2, 3, 3, 4, 4, 5, 5 ), list( target( 2, 3, red, pie ),
                                                                                 target( 3, 4, green, pie ),
                                                                                 target( 4, 5, lightBlue, pie ) ) ) :
                 i == 2 ? new Level( list( 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5 ), list( target( 2, 3, red, pie ),
                                                                                    target( 3, 4, green, pie ),
                                                                                    target( 4, 5, lightBlue, pie ) ) ) :
                 i == 3 ? new Level( list( 0, 1, 2, 3, 3, 3, 4, 5, 6, 7, 8, 9 ), list( target( 2, 6, red, flower ),
                                                                                       target( 3, 6, green, flower ),
                                                                                       target( 4, 6, lightBlue, flower ) ) ) :
                 i == 4 ? new Level( list( 0, 1, 1, 2, 3, 3, 3, 4, 5, 6, 7, 8, 9 ), list( target( 1, 1, red, pyramid1 ),
                                                                                          target( 3, 4, green, pyramid4 ),
                                                                                          target( 5, 9, lightBlue, pyramid9 ) ) ) :
                 new Level( list( 4, 3, 3, 2, 2, 1, 0, 5, 6, 7, 8, 9 ), list( target( 4, 3, red, pie ),
                                                                              target( 3, 2, green, pie ),
                                                                              target( 2, 1, lightBlue, pie ) ) )
            );
        }
    }};
    public final Property<Integer> level = new Property<Integer>( 4 );

    public void resetAll() {
        selectedScene.reset();
        clock.resetSimulationTime();
    }

    public void addCreatedValue( final Fraction value ) {
        final Property<List<Fraction>> fractions = levels.get( level.get() ).createdFractions;
        fractions.set( fractions.get().snoc( value ) );
    }

    public Property<List<Fraction>> getCreatedFractions( final int level ) {
        return levels.get( level ).createdFractions;
    }

    public Level getLevel( final int level ) { return levels.get( level ); }
}