/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/phetcommon/src/edu/colorado/phet/common/application/ModuleObserver.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.13 $
 * Date modified : $Date: 2006/01/03 23:37:17 $
 */

package edu.colorado.phet.common.application;

import java.util.EventListener;

/**
 * Observes additions and removals of Modules, change in the active Module.
 *
 * @author Ron LeMaster
 * @version $Revision: 1.13 $
 */
public interface ModuleObserver extends EventListener {
    /**
     * Invoked when a Module is added to a PhetApplication.
     *
     * @param event
     */
    public void moduleAdded( ModuleEvent event );

    /**
     * Invoked when the active Module changed..
     *
     * @param event
     */
    public void activeModuleChanged( ModuleEvent event );

    /**
     * Invoked when a Module is removed from the PhetApplication.
     *
     * @param event
     */
    public void moduleRemoved( ModuleEvent event );
}
