// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.model;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * Class that represents a proton in the model.
 *
 * @author John Blanco
 */
public class Proton extends SphericalParticle {
    public static final double RADIUS = 5;

    public Proton( ConstantDtClock clock, double x, double y ) {
        super( RADIUS, x, y, clock );
    }

    public Proton( ConstantDtClock clock ) {
        this( clock, 0, 0 );
    }
}
