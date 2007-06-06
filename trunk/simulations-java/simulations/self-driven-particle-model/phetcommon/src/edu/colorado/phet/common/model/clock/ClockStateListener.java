/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: C:/Java/cvs/root/SelfDrivenParticles/phetcommon/src/edu/colorado/phet/common/model/clock/ClockStateListener.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: Sam Reid $
 * Revision : $Revision: 1.1.1.1 $
 * Date modified : $Date: 2005/08/10 08:22:02 $
 */
package edu.colorado.phet.common.model.clock;

import java.util.EventListener;


/**
 * ClockStateListener
 *
 * @author Ron LeMaster
 * @version $Revision: 1.1.1.1 $
 */
public interface ClockStateListener extends EventListener {

    void stateChanged( ClockStateEvent event );
}
