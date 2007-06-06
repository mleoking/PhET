/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: C:/Java/cvs/root/SelfDrivenParticles/phetcommon/src/edu/colorado/phet/common/application/ModuleEvent.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: Sam Reid $
 * Revision : $Revision: 1.1.1.1 $
 * Date modified : $Date: 2005/08/10 08:22:02 $
 */
package edu.colorado.phet.common.application;

import java.util.EventObject;

/**
 * ModuleEvent
 *
 * @author Ron LeMaster
 * @version $Revision: 1.1.1.1 $
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
