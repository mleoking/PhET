// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Beaker;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Shaker;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.SaltLattice;

/**
 * This salt shaker adds salt (NaCl) crystals to the model when shaken
 *
 * @author Sam Reid
 */
public class MicroSaltShaker extends Shaker<MicroModel> {
    //Keep track of how many times the user has tried to create macro salt, so that we can (less frequently) create corresponding micro crystals
    private final Property<Integer> stepsAdding = new Property<Integer>( 0 );

    public MicroSaltShaker( double x, double y, Beaker beaker, ObservableProperty<Boolean> moreAllowed, String name, double distanceScale, ObservableProperty<DispenserType> selectedType, DispenserType type ) {
        super( x, y, beaker, moreAllowed, name, distanceScale, selectedType, type );
    }

    @Override protected void addSalt( MicroModel model, ImmutableVector2D outputPoint, double volumePerSolidMole, final ImmutableVector2D crystalVelocity ) {
        //Only add a crystal every N steps, otherwise there are too many
        stepsAdding.set( stepsAdding.get() + 1 );
        if ( stepsAdding.get() % 30 == 0 ) {
            //Create a random crystal and add it to the model
            model.addSaltCrystal( new SaltCrystal( outputPoint, (SaltLattice) new SaltLattice().grow( 20 ), MicroModel.sizeScale ) );
        }
    }
}
