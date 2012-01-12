package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Beaker;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarDispenser;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;

/**
 * Dispenser for the micro tab that emits sugar, either sucrose or glucose.
 *
 * @author Sam Reid
 */
public abstract class MicroSugarDispenser extends SugarDispenser<MicroModel> {

    //Keep track of how many times the user has tried to create macro salt, so that we can (less frequently) create corresponding micro crystals
    private final Property<Integer> stepsOfAddingSugar = new Property<Integer>( 0 );

    public MicroSugarDispenser( double x, double y, Beaker beaker, ObservableProperty<Boolean> moreAllowed, final String sugarDispenserName, double distanceScale, ObservableProperty<DispenserType> selectedType, DispenserType type, MicroModel model ) {
        super( x, y, beaker, moreAllowed, sugarDispenserName, distanceScale, selectedType, type, model );
    }

    //Checks to see if a sugar should be emitted, and if so, adds it to the model
    @Override protected void addSugarToModel( ImmutableVector2D outputPoint ) {

        //Only add a crystal every N steps, otherwise there are too many
        stepsOfAddingSugar.set( stepsOfAddingSugar.get() + 1 );
        if ( stepsOfAddingSugar.get() % 10 == 0 ) {

            //Create a random crystal with 4 sucrose molecules
            doAddSugar( outputPoint );
        }
    }

    //Adds a single sugar model item to the model at the specified point
    protected abstract void doAddSugar( ImmutableVector2D outputPoint );
}