/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/phetcommon/src/edu/colorado/phet/common/application/ModuleEvent.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.5 $
 * Date modified : $Date: 2006/01/03 23:37:17 $
 */
package edu.colorado.phet.common.application;

/**
 * An event class sent to ModuleObservers.
 *
 * @author Ron LeMaster
 * @version $Revision: 1.5 $
 */
public class ModuleEvent {
    private PhetApplication phetApplication;
    private Module module;

    /**
     * Constructs a new ModuleEvent.
     *
     * @param phetApplication
     * @param module
     */
    public ModuleEvent( PhetApplication phetApplication, Module module ) {
        this.phetApplication = phetApplication;
        this.module = module;
    }

    /**
     * Gets the module associated with this ModuleEvent.
     *
     * @return the module associated with this ModuleEvent.
     */
    public Module getModule() {
        return module;
    }

    /**
     * Gets the PhetApplication associated with this ModuleEvent.
     *
     * @return the PhetApplication associated with this ModuleEvent.
     */
    public PhetApplication getPhetApplication() {
        return phetApplication;
    }
}
