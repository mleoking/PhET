// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumchloride;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Beaker;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroShaker;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Chloride;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Sodium;

import static edu.colorado.phet.sugarandsaltsolutions.micro.model.RandomUtil.randomAngle;

/**
 * This salt shaker adds salt (NaCl) crystals to the model when shaken
 *
 * @author Sam Reid
 */
public class SodiumChlorideShaker extends MicroShaker {

    public SodiumChlorideShaker( double x, double y, Beaker beaker, ObservableProperty<Boolean> moreAllowed, String name, double distanceScale, ObservableProperty<DispenserType> selectedType, DispenserType type, MicroModel model ) {
        super( x, y, beaker, moreAllowed, name, distanceScale, selectedType, type, model );
    }

    //Create a random salt crystal and add it to the model
    @Override protected void addCrystal( MicroModel model, ImmutableVector2D outputPoint, double volumePerSolidMole, ImmutableVector2D crystalVelocity ) {

        //Attempt 100 times to randomly create a crystal with a correct balance of components
        //If no success after 100 random tries, just take the last attempt
        //This tends to work in much less than 100 tries, such as 3-4 tries
        SodiumChlorideCrystal crystal = null;
        int count = 0;
        while ( crystal == null || crystal.count( Sodium.class ) != crystal.count( Chloride.class ) && count++ < 100 ) {
            crystal = new SodiumChlorideCrystal( outputPoint, randomAngle() ) {{ grow( 10 ); }};
        }
        model.addSodiumChlorideCrystal( crystal );
    }
}