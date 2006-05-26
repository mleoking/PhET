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
import edu.colorado.phet.simlauncher.SimulationReferer;
import edu.colorado.phet.simlauncher.actions.InstallSimulationAction;

import javax.swing.*;

/**
 * SimulationPopupMenu
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class UninstalledSimulationPopupMenu extends JPopupMenu implements SimulationReferer {
    private Simulation simulation;

    public UninstalledSimulationPopupMenu( Simulation simulation ) {

        // Launch menu item
        JMenuItem launchMI = new JMenuItem( "Install");
        launchMI.addActionListener( new InstallSimulationAction( this, simulation ) );
        add( launchMI );
    }


    //--------------------------------------------------------------------------------------------------
    // Implementation of SimulationReferer
    //--------------------------------------------------------------------------------------------------

    public Simulation getSimulation() {
        return simulation;
    }
}
