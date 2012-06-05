package edu.colorado.phet.fractionsintro.buildafraction.model;

import fj.data.List;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;

/**
 * Model for the Build a Fraction tab.
 *
 * @author Sam Reid
 */
public class BuildAFractionModel {
    public final ConstantDtClock clock = new ConstantDtClock();
    public final Property<Scene> selectedScene = new Property<Scene>( Scene.numbers );
    public final Property<List<Fraction>> createdFractions = new Property<List<Fraction>>( List.<Fraction>nil() );

    public void resetAll() {
        selectedScene.reset();
        clock.resetSimulationTime();
    }

    public void addCreatedValue( final Fraction value ) {
        createdFractions.set( createdFractions.get().snoc( value ) );
    }
}