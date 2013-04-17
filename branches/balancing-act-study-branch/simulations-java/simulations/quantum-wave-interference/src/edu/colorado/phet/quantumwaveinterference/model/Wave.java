// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.model;

import edu.colorado.phet.quantumwaveinterference.model.math.Complex;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 8:22:46 AM
 */

public interface Wave {
    Complex getValue( int i, int j, double simulationTime );
}
