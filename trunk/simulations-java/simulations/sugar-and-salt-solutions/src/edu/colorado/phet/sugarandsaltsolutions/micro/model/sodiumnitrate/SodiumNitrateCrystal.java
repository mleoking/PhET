// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumnitrate;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Crystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Formula;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.FreeOxygen;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Nitrogen;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Sodium;

/**
 * This crystal for Sodium Chloride salt updates the positions of the molecules to ensure they move as a crystal
 *
 * @author Sam Reid
 */
public class SodiumNitrateCrystal extends Crystal<Particle> {

    //The distance between nitrogen and oxygen should be the sum of their radii, but the blue background makes it hard to tell that N and O are bonded.
    //Therefore we bring the outer O's closer to the N so there is some overlap.
    public static final double NITROGEN_OXYGEN_SPACING = ( new Nitrogen().radius + new FreeOxygen().radius ) * 0.85;

    public SodiumNitrateCrystal( ImmutableVector2D position, double angle ) {
        super( new Formula( Sodium.class, Nitrate.class ), position, new Sodium().radius * 2 + NITROGEN_OXYGEN_SPACING, angle );
    }

    //Create the bonding partner for growing the crystal
    @Override public Particle createPartner( Particle original ) {
        return original instanceof Sodium ? new Nitrate() : new Sodium();
    }

    //Randomly choose an initial particle for the crystal lattice
    @Override protected Particle createSeed() {
        return random.nextBoolean() ? new Sodium() : new Nitrate();
    }

    //Determine whether Na or NO3 should be added to a crystal when growing to maintain the ionic balance
    @Override public Class<? extends Particle> getMinorityType() {
        return getMajorityType( Sodium.class, Nitrate.class );
    }
}