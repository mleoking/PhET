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

import java.util.EventObject;


/**
 * QTClockChangeEvent indicates changes that are specific to QTClock.
 */
public class QTClockChangeEvent extends EventObject {
    
    public QTClockChangeEvent( QTClock clock ) {
        super( clock );
    }
    
    public QTClock getClock() {
        return (QTClock) getSource();
    }
}