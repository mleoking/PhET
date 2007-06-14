/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src/edu/colorado/phet/simlauncher/actions/UpdateSimAction.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.12 $
 * Date modified : $Date: 2006/07/25 18:00:17 $
 */
package edu.colorado.phet.simlauncher.actions;

import edu.colorado.phet.simlauncher.model.SimContainer;
import edu.colorado.phet.simlauncher.model.Simulation;
import edu.colorado.phet.simlauncher.model.resources.SimResource;
import edu.colorado.phet.simlauncher.model.resources.SimResourceException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * UpdateSimAction
 *
 * @author Ron LeMaster
 * @version $Revision: 1.12 $
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
        Simulation[] sims = simContainer.getSimulations();
        for( int i = 0; i < sims.length; i++ ) {
            Simulation sim = sims[i];
            if( sim.isInstalled() ) {
                if( !sim.isCurrent() ) {
                    update( sim );
                }
            }            
        }
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