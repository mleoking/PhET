// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Beaker;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Shaker;

/**
 * A shaker for the "micro tab" emits crystals less frequently than in the macro tab.  This class keeps track of when and what to emit.
 *
 * @author Sam Reid
 */
public abstract class MicroShaker extends Shaker<MicroModel> {
    //Keep track of how many times the user has tried to create macro salt, so that we can (less frequently) create corresponding micro crystals
    private final Property<Integer> stepsAdding = new Property<Integer>( 0 );

    public MicroShaker( double x, double y, Beaker beaker, ObservableProperty<Boolean> moreAllowed, String name, double distanceScale, ObservableProperty<DispenserType> selectedType, DispenserType type ) {
        super( x, y, beaker, moreAllowed, name, distanceScale, selectedType, type );
    }

    @Override protected void addSalt( MicroModel model, ImmutableVector2D outputPoint, double volumePerSolidMole, ImmutableVector2D crystalVelocity ) {
        //Only add a crystal every N steps, otherwise there are too many
        stepsAdding.set( stepsAdding.get() + 1 );
        if ( stepsAdding.get() % 30 == 0 ) {
            addCrystal( model, outputPoint, volumePerSolidMole, crystalVelocity );
        }
    }

    //This method actually adds the crystal
    protected abstract void addCrystal( MicroModel model, ImmutableVector2D outputPoint, double volumePerSolidMole, ImmutableVector2D crystalVelocity );

    //Randomly generate crystals until we attain one that matches the desired criteria, such as having a perfect ratio of constituents (1:1 for NaCl or 2:1 for CaCl)
    public static <T> T generateRandomLattice( Function0<T> newCrystal, Function1<T, Boolean> matches ) {
        while ( true ) {

            //Number of particles must be odd here so that the sum will be even, to balance out the crystal and make Na = Cl counts
            T crystal = newCrystal.apply();
            if ( matches.apply( crystal ) ) {
                return crystal;
            }
        }
    }
}