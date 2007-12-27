/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.movingman.force1d_orig.common_force1d.model.clock;

import java.util.EventListener;


/**
 * ClockStateListener
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public interface ClockStateListener extends EventListener {

    void stateChanged( ClockStateEvent event );
}
