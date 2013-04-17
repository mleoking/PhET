// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.model.waves;

import edu.colorado.phet.quantumwaveinterference.model.Wave;
import edu.colorado.phet.quantumwaveinterference.model.math.Complex;


/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 8:23:28 AM
 */

public class ZeroWave implements Wave {

    public Complex getValue( int xmesh, int j, double simulationTime ) {
        return new Complex();
    }
}
