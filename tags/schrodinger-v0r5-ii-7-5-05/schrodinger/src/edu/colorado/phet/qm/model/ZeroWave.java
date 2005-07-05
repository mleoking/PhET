/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;


/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 8:23:28 AM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class ZeroWave implements Wave {
//    public void setValue( Wavefunction w, int i, int j, double simulationTime ) {
//        w.valueAt( i, j ).zero();
//    }

    public Complex getValue( int xmesh, int j, double simulationTime ) {
        return new Complex();
    }
}
