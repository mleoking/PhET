/**
 * Class: ClockTickListener
 * Package: edu.colorado.phet.common.model
 * Author: Another Guy
 * Date: Jun 10, 2003
 */
package edu.colorado.phet.common_1200.model.clock;


public interface ClockTickListener {
    public void clockTicked( AbstractClock c, double dt );
}
