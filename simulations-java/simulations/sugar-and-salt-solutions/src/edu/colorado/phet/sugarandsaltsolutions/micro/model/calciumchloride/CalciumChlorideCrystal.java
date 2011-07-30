// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.calciumchloride;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Crystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Calcium;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Chloride;

/**
 * This crystal for Calcium Chloride salt updates the positions of the molecules to ensure they move as a crystal
 *
 * @author Sam Reid
 */
public class CalciumChlorideCrystal extends Crystal<SphericalParticle> {
    public CalciumChlorideCrystal( ImmutableVector2D position, double angle ) {
        super( position, new Calcium().radius + new Chloride().radius, angle );
    }

    //Create the bonding partner for Calcium Chloride
    @Override public SphericalParticle createPartner( SphericalParticle original ) {
        return original instanceof Calcium ? new Chloride() : new Calcium();
    }

    //Randomly choose an initial particle for the crystal lattice
    @Override protected SphericalParticle createSeed() {
        return random.nextBoolean() ? new Calcium() : new Chloride();
    }
}