/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: C:/Java/cvs/root/SelfDrivenParticles/phetcommon/src/edu/colorado/phet/common/model/clock/ClockTickListener.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: Sam Reid $
 * Revision : $Revision: 1.1.1.1 $
 * Date modified : $Date: 2005/08/10 08:22:02 $
 */
package edu.colorado.phet.common.model.clock;

import java.util.EventListener;


/**
 * ClockTickListener
 *
 * @author ?
 * @version $Revision: 1.1.1.1 $
 */
public interface ClockTickListener extends EventListener {

    public void clockTicked( ClockTickEvent event );
}
