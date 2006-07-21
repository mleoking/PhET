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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * UpdateSimAction
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CheckForUpdateSimAction extends AbstractAction {
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

    public CheckForUpdateSimAction( SimContainer simContainer, Component parent ) {
        this.simContainer = simContainer;
        this.parent = parent;
    }

    public void actionPerformed( ActionEvent e ) {

        Cursor orgCursor = SwingUtilities.getRoot( parent ).getCursor();
        SwingUtilities.getRoot( parent ).setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));
        Simulation[] sims = simContainer.getSimulations();

        if( sims.length == 1 ) {
            Simulation sim = sims[0];

            // Tell the sim if it's up to date or not
            setFlagOnSimulation( sim );

            if( !sim.isInstalled() ) {
                showResult( "The simulation is not installed" );
            }
            else if( !sim.isCurrent() ) {
                showResult( "An update is available" );
//                sim.setUpdateAvailable( true );
            }
            else {
                showResult( "The installed simulation is current" );
            }
        }
        else {
            boolean aSimIsUpToDate = false;
            boolean aSimIsNotUpToDate = false;

            for( int i = 0; i < sims.length; i++ ) {
                Simulation sim = sims[i];

                // Tell the sim if it's up to date or not
                setFlagOnSimulation( sim );

                aSimIsUpToDate |= sim.isInstalled() && sim.isCurrent();
                aSimIsNotUpToDate |= sim.isInstalled() && !sim.isCurrent();
            }

            if( aSimIsUpToDate && !aSimIsNotUpToDate ) {
                showResult( "All installed simulations are up to date" );
            }
            else if( !aSimIsUpToDate && aSimIsNotUpToDate ) {
                showResult( "Updates are available for all installed simulations" );
            }
            else {
                showResult( "Updates are available for some installed simulations" );
            }
        }
        
        SwingUtilities.getRoot( parent ).setCursor( orgCursor );
    }

    private void setFlagOnSimulation( Simulation sim ) {
        // Set the flag on the simulation
        if( sim.isInstalled() && !sim.isCurrent() ) {
            sim.setUpdateAvailable( true );
        }
        else {
            sim.setUpdateAvailable( false );
        }
    }

    private void showResult( String message ) {
        JOptionPane.showMessageDialog( parent,
                                       message,
                                       "Check for updates",
                                       JOptionPane.INFORMATION_MESSAGE );
    }
}
