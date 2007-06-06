/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: C:/Java/cvs/root/SelfDrivenParticles/phetcommon/src/edu/colorado/phet/common/model/clock/CompositeClockTickListener.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: Sam Reid $
 * Revision : $Revision: 1.1.1.1 $
 * Date modified : $Date: 2005/08/10 08:22:02 $
 */
package edu.colorado.phet.common.model.clock;

import java.util.ArrayList;

/**
 * CompositeClockTickListener
 *
 * @author ?
 * @version $Revision: 1.1.1.1 $
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

    public void clockTicked( ClockTickEvent event ) {
        for( int i = 0; i < list.size(); i++ ) {
            ClockTickListener clockListener = (ClockTickListener)list.get( i );
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
