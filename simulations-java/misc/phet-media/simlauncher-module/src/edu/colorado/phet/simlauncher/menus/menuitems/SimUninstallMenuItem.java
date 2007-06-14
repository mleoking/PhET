/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src/edu/colorado/phet/simlauncher/menus/menuitems/SimUninstallMenuItem.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.3 $
 * Date modified : $Date: 2006/07/25 18:00:17 $
 */
package edu.colorado.phet.simlauncher.menus.menuitems;

import edu.colorado.phet.simlauncher.actions.UninstallSimAction;
import edu.colorado.phet.simlauncher.model.SimContainer;

import javax.swing.*;

/**
 * SimLaunchMenuItem
 *
 * @author Ron LeMaster
 * @version $Revision: 1.3 $
 */
public class SimUninstallMenuItem extends JMenuItem {

    public SimUninstallMenuItem( SimContainer simContainer ) {
        super( "Remove" );
        addActionListener( new UninstallSimAction( simContainer, this ) );
    }
}
