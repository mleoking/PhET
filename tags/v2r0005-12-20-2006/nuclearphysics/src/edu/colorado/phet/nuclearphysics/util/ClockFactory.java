/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.nuclearphysics.util;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.clock.SwingClock;

/**
 * ClockFactory
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ClockFactory {

    public static IClock create( int delay, double dt ) {
        return new SwingClock( delay, dt );
    }
}
