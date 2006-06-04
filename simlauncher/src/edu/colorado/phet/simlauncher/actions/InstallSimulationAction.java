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

import com.sun.java.swing.SwingUtilities2;

/**
 * LaunchSimulationAction
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class InstallSimulationAction extends AbstractAction {
    private Component component;
    private InstallSimulationAction.Action action;
    private JDialog waitDlg;

    public InstallSimulationAction( Component component, SimulationContainer simulationContainer ) {
        this.component = component;
        action = new InstallSimulationAction.SimulationContainerAction( simulationContainer );
    }

    public InstallSimulationAction( Component component, Simulation simulation ) {
        this.component = component;
        action = new InstallSimulationAction.SimulationAction( simulation );
    }

    public void actionPerformed( ActionEvent e ) {
            action.doIt( component );
    }

    private void showWaitDialog() {
        JFrame frame = (JFrame)SwingUtilities.getRoot( component );
        waitDlg = new JDialog( frame, "Installing...", false );
        JLabel message = new JLabel( "Please wait while the simulation is being installed...");
        waitDlg.getContentPane().add( message );
        waitDlg.pack();
        waitDlg.setVisible( true );
        waitDlg.setLocationRelativeTo( frame );
    }

    private void hideWaitDialog() {
        waitDlg.setVisible( false );
    }

    private void install( Simulation simulation ) {
        showWaitDialog();
        simulation.install();
        hideWaitDialog();
    }

    //--------------------------------------------------------------------------------------------------
    // Wrapped actions
    //--------------------------------------------------------------------------------------------------

    private static interface Action {
        void doIt( Component component );
    }

    private class SimulationContainerAction implements InstallSimulationAction.Action {
        SimulationContainer simulationContainer;

        public SimulationContainerAction( SimulationContainer simulationContainer ) {
            this.simulationContainer = simulationContainer;
        }

        public void doIt( Component component ) {
            install( simulationContainer.getSimulation() );
//            simulationContainer.getSimulation().install();
        }
    }

    private class SimulationAction implements InstallSimulationAction.Action {
        Simulation simulation;

        public SimulationAction( Simulation simulation ) {
            this.simulation = simulation;
        }

        public void doIt( Component component ) {
            install( simulation );
//            simulation.install();
        }
    }
}
