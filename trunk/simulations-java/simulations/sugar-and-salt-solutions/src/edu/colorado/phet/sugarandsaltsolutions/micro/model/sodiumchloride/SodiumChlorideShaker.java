// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumchloride;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Beaker;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Component.ChlorideIon;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Component.SodiumIon;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroShaker;

/**
 * This salt shaker adds salt (NaCl) crystals to the model when shaken
 *
 * @author Sam Reid
 */
public class SodiumChlorideShaker extends MicroShaker {

    public SodiumChlorideShaker( double x, double y, Beaker beaker, ObservableProperty<Boolean> moreAllowed, String name, double distanceScale, ObservableProperty<DispenserType> selectedType, DispenserType type ) {
        super( x, y, beaker, moreAllowed, name, distanceScale, selectedType, type );
    }

    //Create a random salt crystal and add it to the model
    @Override protected void addCrystal( MicroModel model, ImmutableVector2D outputPoint, double volumePerSolidMole, ImmutableVector2D crystalVelocity ) {
        model.addSaltCrystal( new SodiumChlorideCrystal( outputPoint,
                                                         generateRandomLattice( new Function0<SodiumChlorideLattice>() {
                                                                                    public SodiumChlorideLattice apply() {

                                                                                        //TODO: can we get rid of this cast?
                                                                                        return (SodiumChlorideLattice) new SodiumChlorideLattice().grow( 19 );
                                                                                    }
                                                                                }, new Function1<SodiumChlorideLattice, Boolean>() {
                                                             public Boolean apply( SodiumChlorideLattice lattice ) {
                                                                 return lattice.count( SodiumIon.class ) == lattice.count( ChlorideIon.class );
                                                             }
                                                         }
                                                         ) ) );
    }
}