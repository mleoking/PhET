/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.forces1d.phetcommon.application;

import java.util.EventObject;

/**
 * ModuleEvent
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ModuleEvent extends EventObject {
    private Module module;

    public ModuleEvent( Object source, Module module ) {
        super( source );
        this.module = module;
    }

    public Module getModule() {
        return module;
    }
}
