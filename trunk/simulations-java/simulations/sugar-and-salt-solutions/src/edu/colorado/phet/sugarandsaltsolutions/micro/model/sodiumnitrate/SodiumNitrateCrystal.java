// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumnitrate;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Crystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.FreeOxygenIonParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.NitrogenIonParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.SodiumIonParticle;

/**
 * This crystal for Sodium Chloride salt updates the positions of the molecules to ensure they move as a crystal
 *
 * @author Sam Reid
 */
public class SodiumNitrateCrystal extends Crystal<Particle> {

    //The distance between nitrogen and oxygen should be the sum of their radii, but the blue background makes it hard to tell that N and O are bonded.
    //Therefore we bring the outer O's closer to the N so there is some overlap.
    public static final double NITROGEN_OXYGEN_SPACING = ( new NitrogenIonParticle().radius + new FreeOxygenIonParticle().radius ) * 0.85;

    public SodiumNitrateCrystal( ImmutableVector2D position, SodiumNitrateLattice lattice ) {
        super( position, new SodiumIonParticle().radius * 2 + NITROGEN_OXYGEN_SPACING, lattice );
    }
}