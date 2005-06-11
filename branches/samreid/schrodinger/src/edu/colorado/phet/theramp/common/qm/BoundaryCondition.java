/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.qm;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 8:22:46 AM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public interface BoundaryCondition {
    void setValue( Complex[][] w, int i, int j, double simulationTime);

    Complex getValue( int xmesh, int j, double simulationTime );
}
