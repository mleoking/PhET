/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.menus.menuitems;

import edu.colorado.phet.simlauncher.SimContainer;
import edu.colorado.phet.simlauncher.actions.UninstallSimAction;

import javax.swing.*;

/**
 * SimLaunchMenuItem
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimUninstallMenuItem extends JMenuItem {

    public SimUninstallMenuItem( SimContainer simContainer ) {
        super( "Remove" );
        addActionListener( new UninstallSimAction( simContainer, this ) );
    }
}
