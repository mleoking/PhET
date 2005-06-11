/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.qm;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 8:23:28 AM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class ZeroBoundaryCondition implements BoundaryCondition {
    public void setValue( Complex[][] w, int i, int j, double simulationTime ) {
        w[i][j].zero();
    }

    public Complex getValue( int xmesh, int j, double simulationTime ) {
        return new Complex();
    }
}
