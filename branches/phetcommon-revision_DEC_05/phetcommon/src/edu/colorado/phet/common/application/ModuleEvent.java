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
 * ModuleEvent
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ModuleEvent {
    private PhetApplication phetApplication;
    private Module module;

    public ModuleEvent( PhetApplication phetApplication, Module module ) {
        this.phetApplication = phetApplication;
        this.module = module;
    }

    public Module getModule() {
        return module;
    }

    public PhetApplication getPhetApplication() {
        return phetApplication;
    }
}
