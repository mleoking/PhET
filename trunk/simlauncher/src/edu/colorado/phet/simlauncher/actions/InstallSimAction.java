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
import edu.colorado.phet.simlauncher.model.resources.SimResourceException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * InstallSimAction
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class InstallSimAction extends AbstractAction {
    private SimContainer simContainer;
    private Component component;
    private JDialog waitDlg;
    private boolean cancelInstallation;
    private Simulation simBeingInstalled;
    private boolean stopInstallation;

    public InstallSimAction( SimContainer simContainer, Component component ) {
        this.component = component;
        this.simContainer = simContainer;
    }

    public void actionPerformed( ActionEvent e ) {
        install( simContainer.getSimulations() );
    }

    /**
     * Puts up a dialog, then kicks off the installation in a separate thread. When the
     * simulation is installed, the dialog goes away.
     *
     * @param simulations
     */
    private void install( final Simulation[] simulations ) {
        stopInstallation = false;
        for( int i = 0; !stopInstallation && i < simulations.length; i++ ) {
            simBeingInstalled = simulations[i];
            Thread installerThread = new InstallerThread( simBeingInstalled );
            installerThread.start();
            showWaitDialog( simBeingInstalled );
        }
    }

    /**
     * Shows a dialog with an indefinite progress bar
     */
    private void showWaitDialog( Simulation simulation ) {
        JFrame frame = (JFrame)SwingUtilities.getRoot( component );
        waitDlg = new JDialog( frame, "Installing...", true );
        if( simulation != null ) {
            JLabel message = new JLabel( simulation.getName() + " is being installed..." );
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

            JButton cancelButton = new JButton( "Cancel" );
            cancelButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    stopInstallation();
                }
            } );
            contentPane.add( cancelButton, gbc );

            waitDlg.addWindowListener( new WindowAdapter() {
                public void windowClosing( WindowEvent e ) {
                    stopInstallation();
                }
            } );
            waitDlg.pack();
            waitDlg.setLocationRelativeTo( frame );
            waitDlg.setVisible( true );
        }
    }

    /**
     *
     */
    private void stopInstallation() {
        simBeingInstalled.stopInstallation();
        stopInstallation = true;
        hideWaitDialog();
    }

    /**
     * Hides the wait dialog
     */
    private void hideWaitDialog() {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                waitDlg.setVisible( false );
            }
        } );
    }


    //--------------------------------------------------------------------------------------------------
    // Inner classes
    //--------------------------------------------------------------------------------------------------

    /**
     * A Thread that installs a specified simulation
     */
    private class InstallerThread extends Thread {
        Simulation simulation;

        public InstallerThread( Simulation simulation ) {
            this.simulation = simulation;
        }

        public void run() {
            try {
                simulation.install();
            }
            catch( SimResourceException e ) {
                RemoteUnavaliableMessagePane.show( null );
            }
            hideWaitDialog();
        }
    }
}