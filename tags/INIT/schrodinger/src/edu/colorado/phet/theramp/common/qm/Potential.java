package edu.colorado.phet.theramp.common.qm;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 8:11:23 AM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */
public interface Potential {
    public double getPotential( int x, int y, int timestep );
}
