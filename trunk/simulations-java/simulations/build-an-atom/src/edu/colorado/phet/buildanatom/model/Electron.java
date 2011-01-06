/* Copyright 2010, University of Colorado */

package edu.colorado.phet.buildanatom.model;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * Class that represents an electron in the model.
 *
 * @author John Blanco
 */
public class Electron extends SubatomicParticle {
    public static final double RADIUS = 3;

    public Electron( ConstantDtClock clock, double x, double y ) {
        super( clock, RADIUS, x, y );
    }

    public Electron(ConstantDtClock clock) {
        this( clock, 0, 0 );
    }
}
