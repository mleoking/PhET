package edu.colorado.phet.fractionsintro.buildafraction.model;

import fj.Equal;
import fj.F;
import fj.data.List;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.colorado.phet.fractionsintro.matchinggame.view.FilledPattern;

import static fj.data.List.list;
import static fj.data.List.nil;

/**
 * Model for the Build a Fraction tab.
 *
 * @author Sam Reid
 */
public class BuildAFractionModel {

    public final IntegerProperty numberLevel = new IntegerProperty( 9 );
    public final IntegerProperty pictureLevel = new IntegerProperty( 0 );

    public final ConstantDtClock clock = new ConstantDtClock();
    public final Property<Scene> selectedScene = new Property<Scene>( Scene.numbers );

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

    public final ArrayList<NumberLevel> numberLevels = new NumberLevelList();

    public final ArrayList<PictureLevel> pictureLevels = new ArrayList<PictureLevel>() {{
        for ( int i = 0; i <= 10; i++ ) {
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

    public void resetAll() {
        selectedScene.reset();
        clock.resetSimulationTime();
        numberLevel.reset();
        for ( NumberLevel x : numberLevels ) {
            x.resetAll();
        }
        numberLevels.clear();
        numberLevels.addAll( new NumberLevelList() );
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

    public void resample() {
        int n = numberLevel.get();
        Scene scene = selectedScene.get();
        resetAll();
        selectedScene.set( scene );
        numberLevel.set( n );
    }
}