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

import edu.colorado.phet.simlauncher.Simulation;
import edu.colorado.phet.simlauncher.SimulationContainer;
import edu.colorado.phet.simlauncher.actions.LaunchSimulationAction;
import edu.colorado.phet.simlauncher.actions.UninstallSimulationAction;
import edu.colorado.phet.simlauncher.actions.UpdateSimulationAction;

import javax.swing.*;

/**
 * SimulationPopupMenu
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class InstalledSimulationPopupMenu extends JPopupMenu {

    /**
     *
     * @param simulation
     */
    public InstalledSimulationPopupMenu( Simulation simulation ) {

        // Launch menu item
        JMenuItem launchMI = new JMenuItem( "Launch");
        launchMI.addActionListener( new LaunchSimulationAction( this, simulation ) );
        add( launchMI );

        // Update check menu item
        JMenuItem updateCheckMI = new JMenuItem( "Check for update");
        updateCheckMI.addActionListener( new UpdateSimulationAction( this, simulation ));
        add( updateCheckMI );

        // Uninstall menu item
        JMenuItem uninstallMI = new JMenuItem( "Uninstall");
        uninstallMI.addActionListener( new UninstallSimulationAction( this, simulation ));
        add( uninstallMI);
    }
}
