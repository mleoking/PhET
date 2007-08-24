/*  */
package edu.colorado.phet.qm.model.waves;

import edu.colorado.phet.qm.model.Wave;
import edu.colorado.phet.qm.model.math.Complex;


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
