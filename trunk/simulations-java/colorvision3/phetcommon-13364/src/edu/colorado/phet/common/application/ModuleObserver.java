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

/**
 * Observes additions and removals of Modules, change in the active Module.
 * 
 * @author Ron LeMaster
 * @version $Revision$
 */
public interface ModuleObserver {
    public void moduleAdded( Module m );

    public void activeModuleChanged( Module m );

    public void moduleRemoved( Module m );
}
