package edu.colorado.phet.fractionsintro.buildafraction.model;

import fj.data.List;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;

/**
 * Model for the Build a Fraction tab.
 *
 * @author Sam Reid
 */
public class BuildAFractionModel {

    public void nextLevel() {
        level.set( level.get() + 1 );
    }

    static class Level {
        public final Property<List<Fraction>> createdFractions = new Property<List<Fraction>>( List.<Fraction>nil() );
    }

    public final ConstantDtClock clock = new ConstantDtClock();
    public final Property<Scene> selectedScene = new Property<Scene>( Scene.numbers );
    public final ArrayList<Level> levels = new ArrayList<Level>() {{
        for ( int i = 0; i < 10; i++ ) {
            add( new Level() );
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
}