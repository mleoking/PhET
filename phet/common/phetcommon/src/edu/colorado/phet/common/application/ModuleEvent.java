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
 * An event class sent to ModuleObservers.
 *
 * @author Ron LeMaster
 * @version $Revision$
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
