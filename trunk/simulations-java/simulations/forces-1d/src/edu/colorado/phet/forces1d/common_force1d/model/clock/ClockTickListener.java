/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.forces1d.common_force1d.model.clock;

import java.util.EventListener;


/**
 * ClockTickListener
 *
 * @author ?
 * @version $Revision$
 */
public interface ClockTickListener extends EventListener {

    public void clockTicked( ClockTickEvent event );
}
