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
package edu.colorado.phet.common.model.clock;

import java.util.EventObject;

public class ClockTickEvent extends EventObject {
    double dt;

    public ClockTickEvent( Object source, double dt ) {
        super( source );
        this.dt = dt;
    }

    public double getDt() {
        return dt;
    }
}
