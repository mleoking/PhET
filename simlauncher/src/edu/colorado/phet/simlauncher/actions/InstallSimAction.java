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
import edu.colorado.phet.simlauncher.JavaSimulation;
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

    public InstallSimAction( SimContainer simContainer, Component component ) {
        this.component = component;
        this.simContainer = simContainer;
    }

    public void actionPerformed( ActionEvent e ) {
        install( simContainer.getSimulation() );
    }

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
     *
     * @param simulation
     */
    private void install( final JavaSimulation simulation ) {
        Thread installerThread = new Thread( new Runnable() {
            public void run() {
                try {
                    simulation.install();
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
}