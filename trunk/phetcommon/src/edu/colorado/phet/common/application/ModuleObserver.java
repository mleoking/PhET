/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.common.application;

import java.util.EventListener;

/**
 * Observes additions and removals of Modules, change in the active Module.
 *
 * @author Ron LeMaster
 * @version $Revision$
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
