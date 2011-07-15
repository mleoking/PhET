// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Beaker;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.SodiumNitrateLattice;

/**
 * This shaker adds sodium nitrate to the model when shaken
 *
 * @author Sam Reid
 */
public class MicroSodiumNitrateShaker extends MicroShaker {
    public MicroSodiumNitrateShaker( double x, double y, Beaker beaker, ObservableProperty<Boolean> moreAllowed, String name, double distanceScale, ObservableProperty<DispenserType> selectedType, DispenserType type ) {
        super( x, y, beaker, moreAllowed, name, distanceScale, selectedType, type );
    }

    @Override protected void addCrystal( MicroModel model, ImmutableVector2D outputPoint, double volumePerSolidMole, ImmutableVector2D crystalVelocity ) {
        model.addSodiumNitrateCrystal( new SodiumNitrateCrystal( outputPoint, (SodiumNitrateLattice) new SodiumNitrateLattice().grow( 20 ), MicroModel.sizeScale ) );
    }
}