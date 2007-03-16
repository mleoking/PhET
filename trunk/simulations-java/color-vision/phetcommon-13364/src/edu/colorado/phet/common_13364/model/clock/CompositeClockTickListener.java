/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common_13364.model.clock;

import java.util.ArrayList;

/**
 * CompositeClockTickListener
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CompositeClockTickListener implements ClockTickListener {
    ArrayList list = new ArrayList();

    public void addClockTickListener( ClockTickListener cl ) {
        list.add( cl );
    }

    public ClockTickListener clockTickListenerAt( int i ) {
        return (ClockTickListener)list.get( i );
    }

    public int numClockTickListeners() {
        return list.size();
    }

    public void clockTicked( AbstractClock c, double dt ) {
        for( int i = 0; i < list.size(); i++ ) {
            ClockTickListener clockListener = (ClockTickListener)list.get( i );
            clockListener.clockTicked( c, dt );
        }
    }

    public void removeClockTickListener( ClockTickListener cl ) {
        list.remove( cl );
    }
}
