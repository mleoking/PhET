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

import edu.colorado.phet.simlauncher.PhetSiteConnection;
import edu.colorado.phet.simlauncher.Simulation;
import edu.colorado.phet.simlauncher.menus.menuitems.*;

import javax.swing.*;

/**
 * UninstalledSimPopupMenu
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CatalogPopupMenu extends JPopupMenu {

    /**
     * @param simulation
     */
    public CatalogPopupMenu( Simulation simulation ) {

        JMenuItem checkUpdateMI = new SimUpdateCheckMenuItem( simulation, PhetSiteConnection.instance() );
        JMenuItem updateMI = new SimUpdateMenuItem( simulation, PhetSiteConnection.instance() );
        JMenuItem descriptionMI = new SimDescriptionMenuItem( simulation );
        JMenuItem uninstallMI = new SimUninstallMenuItem( simulation );
        JMenuItem installMI = new SimInstallMenuItem( simulation, PhetSiteConnection.instance() );

//        add( descriptionMI );
        add( checkUpdateMI );
        add( updateMI );
        add( uninstallMI );
        add( installMI );

        // Enable/disable menu items that are dependent on whether the simulation is installed
        checkUpdateMI.setEnabled( simulation.isInstalled() );
        updateMI.setEnabled( simulation.isInstalled() );
        uninstallMI.setEnabled( simulation.isInstalled() );
        installMI.setEnabled( !simulation.isInstalled() );
    }
}