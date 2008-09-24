/**
 * Class: CompositeClockTickListener
 * Package: edu.colorado.phet.common.model
 * Author: Another Guy
 * Date: Jun 10, 2003
 */
package edu.colorado.phet.semiconductor.phetcommon.model.clock;

import java.util.ArrayList;

public class CompositeClockTickListener implements ClockTickListener {
    ArrayList list = new ArrayList();

    public void addClockTickListener( ClockTickListener cl ) {
        list.add( cl );
    }

    public void clockTicked( AbstractClock c, double dt ) {
        for ( int i = 0; i < list.size(); i++ ) {
            ClockTickListener clockListener = (ClockTickListener) list.get( i );
            clockListener.clockTicked( c, dt );
        }
    }

}
