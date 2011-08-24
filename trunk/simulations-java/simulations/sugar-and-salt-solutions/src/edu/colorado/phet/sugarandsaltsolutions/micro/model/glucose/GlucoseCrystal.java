// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.glucose;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Crystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Formula;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;

import static edu.colorado.phet.sugarandsaltsolutions.common.util.Units.nanometersToMeters;

/**
 * This crystal for sugar updates the positions of the molecules to ensure they move together
 *
 * @author Sam Reid
 */
public class GlucoseCrystal extends Crystal<Glucose> {

    public GlucoseCrystal( ImmutableVector2D position, double angle ) {

        //Glucose is about half as big as sucrose and hence should be half as far away on the lattice
        super( new Formula( Glucose.class ), position,

               //Spacing between adjacent sucrose molecules, in meters
               nanometersToMeters( 0.5 ) * SugarAndSaltSolutionsApplication.sizeScale.get() / 2,

               angle );
    }

    //Create a new Glucose to be added to the crystal
    @Override public Glucose createPartner( Glucose original ) {
        return new Glucose();
    }

    //Create a single Glucose molecule to begin the crystal
    @Override protected Glucose createConstituentParticle( Class<? extends Particle> type ) {
        return new Glucose();
    }

    @Override public Class<? extends Particle> getMinorityType() {
        return Glucose.class;
    }
}