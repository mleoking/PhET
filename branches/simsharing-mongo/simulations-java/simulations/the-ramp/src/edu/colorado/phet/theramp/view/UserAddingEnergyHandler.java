// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.theramp.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import edu.colorado.phet.theramp.RampModule;

/**
 * User: Sam Reid
 * Date: May 10, 2005
 * Time: 12:37:03 AM
 */

public class UserAddingEnergyHandler extends MouseAdapter {
    private RampModule module;

    public UserAddingEnergyHandler( RampModule module ) {
        this.module = module;
    }

    public void mousePressed( MouseEvent e ) {
        module.getRampPhysicalModel().setUserIsAddingEnergy( true );
    }

    public void mouseReleased( MouseEvent e ) {
        module.getRampPhysicalModel().setUserIsAddingEnergy( false );
    }
}
