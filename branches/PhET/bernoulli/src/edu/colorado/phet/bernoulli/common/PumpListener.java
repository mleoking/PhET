/**
 * Class: PumpListener
 * Package: edu.colorado.phet.bernoulli.common
 * Author: Another Guy
 * Date: Sep 26, 2003
 */
package edu.colorado.phet.bernoulli.common;

import edu.colorado.phet.bernoulli.pump.Pump;

public interface PumpListener {
    void waterExpelledFromPump( Pump p, double volume );
}
