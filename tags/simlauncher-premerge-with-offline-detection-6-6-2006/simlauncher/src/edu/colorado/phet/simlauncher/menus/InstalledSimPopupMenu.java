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
import edu.colorado.phet.simlauncher.actions.*;

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

        // Description menu item
        JMenuItem descriptionMI = new JMenuItem( "Show description");
        descriptionMI.addActionListener( new DisplaySimDescriptionAction( simulation, this ) );
        add( descriptionMI );

        // Update check menu item
        JMenuItem updateCheckMI = new JMenuItem( "Check for update");
        updateCheckMI.addActionListener( new CheckForUpdateSimAction( simulation, this ));
        add( updateCheckMI );

        // Update menu item
        JMenuItem updateMI = new JMenuItem( "Update simulation");
        updateMI .addActionListener( new UpdateSimAction( simulation ) );
        add( updateMI );

        // Uninstall menu item
        JMenuItem uninstallMI = new JMenuItem( "Uninstall");
        uninstallMI.addActionListener( new UninstallSimAction( simulation, this ));
        add( uninstallMI);
    }
}
