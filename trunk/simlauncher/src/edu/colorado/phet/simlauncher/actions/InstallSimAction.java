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

import edu.colorado.phet.simlauncher.SimContainer;
import edu.colorado.phet.simlauncher.Simulation;
import edu.colorado.phet.simlauncher.resources.SimResourceException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

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
        Thread installerThread = new Thread( new Runnable() {
            public void run() {
                try {
                    for( int i = 0; i < simulations.length; i++ ) {
                        Simulation simulation = simulations[i];
                                                simulation.install();
                    }
                }
                catch( SimResourceException e ) {
                    RemoteUnavaliableMessagePane.show( null );
                }
                hideWaitDialog();
            }
        } );
        installerThread.start();
        showWaitDialog();
    }

    /**
     * Shows a dialog with an indefinite progress bar
     */
    private void showWaitDialog() {
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

//        JButton cancelButton = new JButton( "Cancel");
//        cancelButton.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                hideWaitDialog();
//            }
//        } );
//        contentPane.add( cancelButton, gbc );

        waitDlg.pack();
        waitDlg.setLocationRelativeTo( frame );
        waitDlg.setVisible( true );
    }

    /**
     * Hides the dialog
     */
    private void hideWaitDialog() {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                waitDlg.setVisible( false );
            }
        } );
    }

}