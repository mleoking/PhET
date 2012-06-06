package edu.colorado.phet.fractionsintro.buildafraction.model;

import fj.Equal;
import fj.data.List;

import java.awt.Color;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.fractionsintro.buildafraction.view.Target;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.colorado.phet.fractionsintro.matchinggame.model.Pattern;

import static edu.colorado.phet.fractionsintro.matchinggame.view.fractions.FilledPattern.sequentialFill;
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
    public final ArrayList<Level> levels = new ArrayList<Level>() {{
        for ( int i = 0; i < 10; i++ ) {
            final Color lightBlue = new Color( 100, 100, 255 );
            add( i == 0 ? new Level( List.list( 1, 1, 2, 2, 3, 3 ), List.list( new Target( new Fraction( 1, 2 ), red, sequentialFill( Pattern.pie( 2 ), 1 ) ),
                                                                               new Target( new Fraction( 1, 3 ), green, sequentialFill( Pattern.pie( 3 ), 1 ) ),
                                                                               new Target( new Fraction( 2, 3 ), lightBlue, sequentialFill( Pattern.pie( 3 ), 2 ) ) ) ) :
                 i == 1 ? new Level( List.list( 1, 1, 2, 2, 3, 3, 4, 4, 5, 5 ), List.list( new Target( new Fraction( 2, 3 ), red, sequentialFill( Pattern.pie( 3 ), 2 ) ),
                                                                                           new Target( new Fraction( 3, 4 ), green, sequentialFill( Pattern.pie( 4 ), 3 ) ),
                                                                                           new Target( new Fraction( 4, 5 ), lightBlue, sequentialFill( Pattern.pie( 5 ), 4 ) ) ) ) :
                 i == 2 ? new Level( List.list( 0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 9 ), List.list( new Target( new Fraction( 2, 6 ), red, sequentialFill( Pattern.sixFlower(), 2 ) ),
                                                                                              new Target( new Fraction( 3, 6 ), green, sequentialFill( Pattern.sixFlower(), 3 ) ),
                                                                                              new Target( new Fraction( 4, 6 ), lightBlue, sequentialFill( Pattern.sixFlower(), 4 ) ) ) ) :
                 new Level( List.list( 0, 1, 2, 3, 3, 4, 5, 6, 7, 8, 9 ), List.list( new Target( new Fraction( 2, 6 ), red, sequentialFill( Pattern.sixFlower(), 2 ) ),
                                                                                     new Target( new Fraction( 3, 6 ), green, sequentialFill( Pattern.sixFlower(), 3 ) ),
                                                                                     new Target( new Fraction( 4, 6 ), lightBlue, sequentialFill( Pattern.sixFlower(), 4 ) ) ) )
            );
        }
    }};
    public final Property<Integer> level = new Property<Integer>( 0 );

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