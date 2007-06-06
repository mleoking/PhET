/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: C:/Java/cvs/root/SelfDrivenParticles/phetcommon/src/edu/colorado/phet/common/application/ModuleObserver.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: Sam Reid $
 * Revision : $Revision: 1.1.1.1 $
 * Date modified : $Date: 2005/08/10 08:22:02 $
 */

package edu.colorado.phet.common.application;

import java.util.EventListener;
import java.util.EventObject;

/**
 * Observes additions and removals of Modules, change in the active Module.
 *
 * @author Ron LeMaster
 * @version $Revision: 1.1.1.1 $
 */
public interface ModuleObserver extends EventListener {
    public void moduleAdded( ModuleEvent event );

    public void activeModuleChanged(  ModuleEvent event );

    public void moduleRemoved(  ModuleEvent event );
}
