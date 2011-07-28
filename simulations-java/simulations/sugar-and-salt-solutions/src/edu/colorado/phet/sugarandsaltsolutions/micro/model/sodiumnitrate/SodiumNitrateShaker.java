// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumnitrate;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Beaker;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroShaker;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Sodium;

import static edu.colorado.phet.sugarandsaltsolutions.micro.model.RandomUtil.randomAngle;

/**
 * This shaker adds sodium nitrate to the model when shaken
 *
 * @author Sam Reid
 */
public class SodiumNitrateShaker extends MicroShaker {
    public SodiumNitrateShaker( double x, double y, Beaker beaker, ObservableProperty<Boolean> moreAllowed, String name, double distanceScale, ObservableProperty<DispenserType> selectedType, DispenserType type ) {
        super( x, y, beaker, moreAllowed, name, distanceScale, selectedType, type );
    }

    @Override protected void addCrystal( MicroModel model, ImmutableVector2D outputPoint, double volumePerSolidMole, ImmutableVector2D crystalVelocity ) {
        model.addSodiumNitrateCrystal( new SodiumNitrateCrystal( outputPoint,
                                                                 generateRandomLattice( new Function0<SodiumNitrateLattice>() {
                                                                                            public SodiumNitrateLattice apply() {

                                                                                                //TODO: can we get rid of this cast?
                                                                                                return (SodiumNitrateLattice) new SodiumNitrateLattice().grow( 19 );
                                                                                            }
                                                                                        }, new Function1<SodiumNitrateLattice, Boolean>() {
                                                                     public Boolean apply( SodiumNitrateLattice lattice ) {
                                                                         return lattice.count( Sodium.class ) == lattice.count( Nitrate.class );
                                                                     }
                                                                 }
                                                                 ), randomAngle() ) );
    }
}