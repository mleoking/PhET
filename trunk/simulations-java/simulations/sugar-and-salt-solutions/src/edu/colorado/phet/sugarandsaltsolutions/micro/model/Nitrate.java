// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * Data structure for a nitrate (NO3) including references to the particles and the locations relative to the central nitrogen.
 *
 * @author Sam Reid
 */
public class Nitrate {
    public final SphericalParticle o1;
    public final SphericalParticle o2;
    public final SphericalParticle o3;
    public final SphericalParticle nitrogen;
    public final ImmutableVector2D o1Position;
    public final ImmutableVector2D o2Position;
    public final ImmutableVector2D o3Position;

    public Nitrate( SphericalParticle o1, SphericalParticle o2, SphericalParticle o3, SphericalParticle nitrogen, ImmutableVector2D o1Position, ImmutableVector2D o2Position, ImmutableVector2D o3Position ) {
        this.o1 = o1;
        this.o2 = o2;
        this.o3 = o3;
        this.nitrogen = nitrogen;
        this.o1Position = o1Position;
        this.o2Position = o2Position;
        this.o3Position = o3Position;
    }
}
