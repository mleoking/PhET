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
import edu.colorado.phet.simlauncher.actions.LaunchSimAction;
import edu.colorado.phet.simlauncher.actions.UninstallSimAction;
import edu.colorado.phet.simlauncher.actions.UpdateSimAction;

import javax.swing.*;

/**
 * SimulationPopupMenu
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class InstalledSimPopupMenu extends JPopupMenu {

    /**
     *
     * @param simulation
     */
    public InstalledSimPopupMenu( Simulation simulation ) {

        // Launch menu item
        JMenuItem launchMI = new JMenuItem( "Launch");
        launchMI.addActionListener( new LaunchSimAction( simulation ) );
        add( launchMI );

        // Update check menu item
        JMenuItem updateCheckMI = new JMenuItem( "Check for update");
        updateCheckMI.addActionListener( new UpdateSimAction( simulation ));
        add( updateCheckMI );

        // Uninstall menu item
        JMenuItem uninstallMI = new JMenuItem( "Uninstall");
        uninstallMI.addActionListener( new UninstallSimAction( simulation, this ));
        add( uninstallMI);
    }
}
