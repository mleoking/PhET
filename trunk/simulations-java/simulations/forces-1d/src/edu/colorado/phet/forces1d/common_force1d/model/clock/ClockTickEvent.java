/**
 * Class: ClockTickEvent
 * Package: edu.colorado.phet.common.model.clock
 * Original Author: Ron LeMaster
 * Creation Date: Dec 12, 2004
 * Creation Time: 4:01:52 PM
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.forces1d.common_force1d.model.clock;

import java.util.EventObject;

public class ClockTickEvent extends EventObject {
    private double dt;
    private AbstractClock clock;

    public ClockTickEvent( AbstractClock source, double dt ) {
        super( source );
        this.clock = source;
        this.dt = dt;
    }

    public double getDt() {
        return dt;
    }

    public AbstractClock getClock() {
        return clock;
    }
}
