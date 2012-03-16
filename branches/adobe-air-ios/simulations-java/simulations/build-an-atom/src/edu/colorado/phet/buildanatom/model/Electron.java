// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.model;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * Class that represents an electron in the model.
 *
 * @author John Blanco
 */
public class Electron extends SphericalParticle {
    public static final double RADIUS = 3;

    public Electron( ConstantDtClock clock, double x, double y ) {
        super( RADIUS, x, y, clock );
    }

    public Electron( ConstantDtClock clock ) {
        this( clock, 0, 0 );
    }
}
