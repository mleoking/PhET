/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.common.model.clock;

/**
 * ClockStateListener
 *
 * @author Sam Reid
 * @version $Revision$
 */
public interface ClockStateListener {
    void delayChanged( int waitTime );

    void dtChanged( double dt );

    void threadPriorityChanged( int priority );

    void pausedStateChanged( boolean b );
}
