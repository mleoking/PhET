// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumnitrate;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Crystal;
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

    public SodiumNitrateCrystal( ImmutableVector2D position, SodiumNitrateLattice lattice, double angle ) {
        super( position, new Sodium().radius * 2 + NITROGEN_OXYGEN_SPACING, lattice, angle );
    }
}