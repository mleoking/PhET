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
import java.awt.event.ContainerAdapter;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

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

    private void showWaitDialog( Thread thread ) {
        JFrame frame = (JFrame)SwingUtilities.getRoot( component );
        waitDlg = new JDialog( frame, "Installing...", true );
        JLabel message = new JLabel( "Please wait while the simulation is being installed..." );
        JPanel contentPane = (JPanel)waitDlg.getContentPane();
        contentPane.setLayout( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                         1, 1, 1, 1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 10, 20, 10, 20 ),
                                                         0, 0 );
        contentPane.add( message, gbc );
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate( true );
        contentPane.add( progressBar, gbc );

        waitDlg.pack();
        waitDlg.setLocationRelativeTo( frame );
        waitDlg.setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
        waitDlg.setVisible( true );
    }

    private void hideWaitDialog() {
        waitDlg.setVisible( false );
    }

    /**
     * Puts up a dialog, then kicks off the installation in a separate thread. When the
     * simulation is installed, the dialog goes away.
     * @param simulation
     */
    private void install( final Simulation simulation ) {
        Thread installerThread = new Thread( new Runnable( ){
            public void run() {
                simulation.install();
                hideWaitDialog();
            }
        });
        installerThread.start();
        showWaitDialog( installerThread );
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
        }
    }

    private class SimulationAction implements InstallSimulationAction.Action {
        Simulation simulation;

        public SimulationAction( Simulation simulation ) {
            this.simulation = simulation;
        }

        public void doIt( Component component ) {
            install( simulation );
        }
    }
}
