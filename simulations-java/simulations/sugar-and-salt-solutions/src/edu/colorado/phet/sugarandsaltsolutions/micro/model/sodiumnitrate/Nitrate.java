// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumnitrate;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Compound;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Constituent;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.FreeOxygen;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Nitrogen;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.ZERO;
import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.parseAngleAndMagnitude;
import static edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumnitrate.SodiumNitrateCrystal.NITROGEN_OXYGEN_SPACING;

/**
 * Data structure for a nitrate (NO3) including references to the particles and the locations relative to the central nitrogen.
 *
 * @author Sam Reid
 */
public class Nitrate extends Compound<Particle> {
    public Nitrate() {
        this( 0, ZERO );
    }

    public Nitrate( double angle, ImmutableVector2D relativePosition ) {
        super( relativePosition, angle );
        constituents.add( new Constituent<Particle>( new FreeOxygen(), parseAngleAndMagnitude( NITROGEN_OXYGEN_SPACING, Math.PI * 2 * 0 / 3.0 + angle ) ) );
        constituents.add( new Constituent<Particle>( new FreeOxygen(), parseAngleAndMagnitude( NITROGEN_OXYGEN_SPACING, Math.PI * 2 * 1 / 3.0 + angle ) ) );
        constituents.add( new Constituent<Particle>( new FreeOxygen(), parseAngleAndMagnitude( NITROGEN_OXYGEN_SPACING, Math.PI * 2 * 2 / 3.0 + angle ) ) );
        constituents.add( new Constituent<Particle>( new Nitrogen(), ZERO ) );
    }
}