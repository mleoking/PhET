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

import edu.colorado.phet.simlauncher.model.SimContainer;
import edu.colorado.phet.simlauncher.model.Simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * CheckForUpdateSimAction
 * <p/>
 * Checks to see if any of the simulations in a SimContainer have updates available
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CheckForSimUpdateAction extends AbstractAction {
    private JDialog waitDlg;

    public static class Result {
        private Result() {
        }
    }

    public static final Result NO_UPDATE_AVAILABLE = new Result();
    public static final Result UPDATE_AVAILABLE = new Result();
    public static final Result SIM_NOT_INSTALLED = new Result();
    public static final Result CHECK_NOT_PERFORMED = new Result();

    private SimContainer simContainer;
    private Component parent;
    private Result result = CHECK_NOT_PERFORMED;

    public CheckForSimUpdateAction( SimContainer simContainer, Component parent ) {
        this.simContainer = simContainer;
        this.parent = parent;
    }

    public void actionPerformed( ActionEvent e ) {
        Simulation[] sims = simContainer.getSimulations();
        Thread workerThread = new WorkerThread( sims );
        workerThread.start();
        showWaitDialog();
    }

    private void showResult( final String message ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog( parent,
                                               message,
                                               "Check for updates",
                                               JOptionPane.INFORMATION_MESSAGE );
            }
        } );
    }

    private void showWaitDialog() {
        JFrame frame = (JFrame)SwingUtilities.getRoot( parent );
        waitDlg = new JDialog( frame, "Checking for updates...", false );
        JLabel message = new JLabel( "Please wait while the selected simulations are checked for available updates." );
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
     * Thread to perform update check
     */
    private class WorkerThread extends Thread {
        private Simulation[] sims;

        public WorkerThread( Simulation[] sims ) {
            this.sims = sims;
        }

        public void run() {
            boolean aSimIsUpToDate = false;
            boolean aSimIsNotUpToDate = false;

            for( int i = 0; i < sims.length; i++ ) {
                Simulation sim = sims[i];

                // Ask the sim to check if it's current
                sim.checkForUpdate();

                aSimIsUpToDate |= sim.isInstalled() && sim.isCurrent();
                aSimIsNotUpToDate |= sim.isInstalled() && !sim.isCurrent();
            }
            hideWaitDialog();

            if( aSimIsUpToDate && !aSimIsNotUpToDate ) {
                if( sims.length == 1 ) {
                    showResult( "The simulation is up to date" );
                }
                else {
                    showResult( "All selected simulations are up to date" );
                }
            }
            else if( !aSimIsUpToDate && aSimIsNotUpToDate ) {
                if( sims.length == 1 ) {
                    showResult( "An update is available for the simulation" );
                }
                else {
                    showResult( "Updates are available for all selected simulations" );
                }
            }
            else {
                if( sims.length == 1 ) {
                    showResult( "Updates are available for the selected simulation" );
                }
                else {
                    showResult( "Updates are available for some of the selected simulations" );
                }
            }
        }
    }
}
