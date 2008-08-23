/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.forces1d.phetcommon.model.clock;

import java.util.ArrayList;

/**
 * CompositeClockTickListener
 *
 * @author ?
 * @version $Revision$
 */
public class CompositeClockTickListener implements ClockTickListener {
    ArrayList list = new ArrayList();

    public void addClockTickListener( ClockTickListener cl ) {
        list.add( cl );
    }

    public ClockTickListener clockTickListenerAt( int i ) {
        return (ClockTickListener) list.get( i );
    }

    public int numClockTickListeners() {
        return list.size();
    }

    public void clockTicked( ClockTickEvent event ) {
        for ( int i = 0; i < list.size(); i++ ) {
            ClockTickListener clockListener = (ClockTickListener) list.get( i );
            clockListener.clockTicked( event );
        }
    }

    public void removeClockTickListener( ClockTickListener cl ) {
        list.remove( cl );
    }

    public boolean containsClockTickListener( ClockTickListener tickListener ) {
        return list.contains( tickListener );
    }
}
