// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.model;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * Class that represents a neutron in the model.
 *
 * @author John Blanco
 */
public class Neutron extends SphericalParticle {
    public static final double RADIUS = 5;

    public Neutron( ConstantDtClock clock, double x, double y ) {
        super( RADIUS, x, y, clock );
    }

    public Neutron( ConstantDtClock clock ) {
        this( clock, 0, 0 );
    }
}
