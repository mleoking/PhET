/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.clock;

import java.util.EventListener;


/**
 * QTClockChangeListener listens for changes that are specific to QTClock.
 */
public interface QTClockChangeListener extends EventListener {
    
    /**
     * Called when the clock's running time has been reset.
     * 
     * @param event
     */
    public void clockReset( QTClockChangeEvent event );
}