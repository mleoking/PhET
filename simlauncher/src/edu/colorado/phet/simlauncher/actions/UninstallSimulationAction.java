/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.actions;

import edu.colorado.phet.simlauncher.Simulation;
import edu.colorado.phet.simlauncher.SimulationContainer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * LaunchSimulationAction
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class UninstallSimulationAction extends AbstractAction {
    private Component component;
    private Action action;


    public UninstallSimulationAction( Component component, SimulationContainer simulationContainer ) {
        this.component = component;
        action = new SimulationContainerAction( simulationContainer );
    }

    public UninstallSimulationAction( Component component, Simulation simulation ) {
        this.component = component;
        action = new SimulationAction( simulation );
    }

    public void actionPerformed( ActionEvent e ) {
        int choice = JOptionPane.showConfirmDialog( component, "Are you sure?", "Confirm", JOptionPane.OK_CANCEL_OPTION );
        if( choice == JOptionPane.OK_OPTION ) {
            action.doIt( component );
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Wrapped actions
    //--------------------------------------------------------------------------------------------------

    private static interface Action {
        void doIt( Component component );
    }

    private static class SimulationContainerAction implements Action {
        SimulationContainer simulationContainer;

        public SimulationContainerAction( SimulationContainer simulationContainer ) {
            this.simulationContainer = simulationContainer;
        }

        public void doIt( Component component ) {
            simulationContainer.getSimulation().uninstall();
        }
    }

    private static class SimulationAction implements Action {
        Simulation simulation;

        public SimulationAction( Simulation simulation ) {
            this.simulation = simulation;
        }

        public void doIt( Component component ) {
            simulation.uninstall();
        }
    }
}
