/**
 * Class: ClockTickListener
 * Package: edu.colorado.phet.common.model
 * Author: Another Guy
 * Date: Jun 10, 2003
 */
package edu.colorado.phet.common.model.clock;

import edu.colorado.phet.common.model.clock.AbstractClock;

public interface ClockTickListener {
    public void clockTicked( AbstractClock c, double dt );
}
