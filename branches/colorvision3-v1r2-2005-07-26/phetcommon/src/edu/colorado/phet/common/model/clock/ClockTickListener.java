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
 * ClockTickListener
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public interface ClockTickListener {
    public void clockTicked( AbstractClock c, double dt );
}
