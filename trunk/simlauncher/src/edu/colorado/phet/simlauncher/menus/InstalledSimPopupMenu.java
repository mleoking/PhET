/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
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
 * @version $Revision$
 */
public class InstalledSimPopupMenu extends JPopupMenu {

    /**
     * @param simulation
     */
    public InstalledSimPopupMenu( Simulation simulation ) {
        add( new SimLaunchMenuItem( simulation ) );
    }
}