/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src/edu/colorado/phet/simlauncher/menus/InstalledSimPopupMenu.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.12 $
 * Date modified : $Date: 2006/07/25 18:00:17 $
 */
package edu.colorado.phet.simlauncher.menus;

import edu.colorado.phet.simlauncher.menus.menuitems.SimLaunchMenuItem;
import edu.colorado.phet.simlauncher.model.Simulation;

import javax.swing.*;

/**
 * SimulationPopupMenu
 * <p/>
 * The context menu that is displayed when the user right-clicks on a simulation
 *
 * @author Ron LeMaster
 * @version $Revision: 1.12 $
 */
public class InstalledSimPopupMenu extends JPopupMenu {

    /**
     * @param simulation
     */
    public InstalledSimPopupMenu( Simulation simulation ) {
        add( new SimLaunchMenuItem( simulation ) );
    }
}