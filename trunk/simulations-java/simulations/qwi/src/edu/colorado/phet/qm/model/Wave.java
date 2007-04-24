/*  */
package edu.colorado.phet.qm.model;

import edu.colorado.phet.qm.model.math.Complex;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 8:22:46 AM
 *
 */

public interface Wave {
    Complex getValue( int i, int j, double simulationTime );
}
