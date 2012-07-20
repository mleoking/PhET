// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumnitrate;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Compound;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Constituent;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Particle;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SphericalParticle.FreeOxygen;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SphericalParticle.Nitrogen;

import static edu.colorado.phet.common.phetcommon.math.Vector2D.ZERO;
import static edu.colorado.phet.common.phetcommon.math.Vector2D.createPolar;
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

    public Nitrate( double angle, Vector2D relativePosition ) {
        super( relativePosition, angle );
        constituents.add( new Constituent<Particle>( new FreeOxygen(), createPolar( NITROGEN_OXYGEN_SPACING, Math.PI * 2 * 0 / 3.0 + angle ) ) );
        constituents.add( new Constituent<Particle>( new FreeOxygen(), createPolar( NITROGEN_OXYGEN_SPACING, Math.PI * 2 * 1 / 3.0 + angle ) ) );
        constituents.add( new Constituent<Particle>( new FreeOxygen(), createPolar( NITROGEN_OXYGEN_SPACING, Math.PI * 2 * 2 / 3.0 + angle ) ) );
        constituents.add( new Constituent<Particle>( new Nitrogen(), ZERO ) );
    }
}