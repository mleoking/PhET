/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: C:/Java/cvs/root/SelfDrivenParticles/phetcommon/src/edu/colorado/phet/common/model/clock/ClockStateEvent.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: Sam Reid $
 * Revision : $Revision: 1.1.1.1 $
 * Date modified : $Date: 2005/08/10 08:22:02 $
 */
package edu.colorado.phet.common.model.clock;

import java.util.EventObject;


/**
 * ClockStateEvent
 *
 * @author Ron LeMaster
 * @version $Revision: 1.1.1.1 $
 */
public class ClockStateEvent extends EventObject {

    public ClockStateEvent( Object source) {
        super( source );
    }

    public double getDt() {
        return ((AbstractClock)getSource()).getDt();
    }

    public int getDelay() {
        return (int)((AbstractClock)getSource()).getDelay();
    }

    public boolean getIsPaused() {
        return ((AbstractClock)getSource()).isPaused();
    }

    public boolean getIsRunning() {
        return ((AbstractClock)getSource()).isRunning();
    }

    /**
     * Returns the priority of the clock thread if the clock is a ThreadedClock.
     * Otherwise, returns the priority of the current thread.
     * @return
     */
    public int getThreadPriority() {
        if( getSource() instanceof ThreadedClock ) {
            return ((ThreadedClock)getSource()).getThreadPriority();
        }
        else {
            return Thread.currentThread().getPriority();
        }
    }
}
