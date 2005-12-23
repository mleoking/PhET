/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.model;

import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.quantumtunneling.QTConstants;


/**
 * QTClock
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTClock extends SwingClock {

    private static final int DELAY = ( 1000 / QTConstants.CLOCK_FRAME_RATE ); // milliseconds
    private static final double DELTA = QTConstants.CLOCK_TIME_STEP;
    
    public QTClock() {
        super( DELAY, DELTA );
    }
}
