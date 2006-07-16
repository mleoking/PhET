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
import edu.colorado.phet.simlauncher.resources.SimResource;
import edu.colorado.phet.simlauncher.resources.SimResourceException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * UpdateSimAction
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class UpdateSimAction extends AbstractAction {
    private SimContainer simContainer;
    private Component parent;
    private JDialog waitDlg;

    public UpdateSimAction( SimContainer simContainer, Component parent ) {
        this.simContainer = simContainer;
        this.parent = parent;
    }

    public void actionPerformed( ActionEvent e ) {
        Simulation sim = simContainer.getSimulation();
//        try {
            if( sim.isInstalled() ) {
                if( !sim.isCurrent() ) {
                    update( sim );
                }
                else {
                    JOptionPane.showMessageDialog( parent,
                                                   "The simulation is already up to date.");
                }
            }
//        }
//        catch( SimResourceException e1 ) {
//            e1.printStackTrace();
//        }
    }

    /**
     * Puts up a dialog, then kicks off the installation in a separate thread. When the
     * simulation is installed, the dialog goes away.
     *
     * @param simulation
     */
    private void update( final Simulation simulation ) {
        final boolean orgFlag = SimResource.isUpdateEnabled();
        SimResource.setUpdateEnabled( true );
        Thread installerThread = new Thread( new Runnable() {
            public void run() {
                try {
                    simulation.update();
                }
                catch( SimResourceException e ) {
                    RemoteUnavaliableMessagePane.show( null );
                }
                SimResource.setUpdateEnabled( orgFlag );
                hideWaitDialog();
            }
        } );
        installerThread.start();
        showWaitDialog();
    }

    private void showWaitDialog() {
        JFrame frame = (JFrame)SwingUtilities.getRoot( parent );
        waitDlg = new JDialog( frame, "Updating...", true );
        JLabel message = new JLabel( "Please wait while the simulation is being updated..." );
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
}