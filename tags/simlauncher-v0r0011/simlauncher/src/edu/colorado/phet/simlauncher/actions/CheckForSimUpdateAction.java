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
import edu.colorado.phet.simlauncher.SimLauncher;

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

        JFrame frame = SimLauncher.instance().getFrame();
        Cursor orgCursor = frame.getCursor();
        frame.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );

        Simulation[] sims = simContainer.getSimulations();

        if( sims.length == 1 ) {
            Simulation sim = sims[0];

            // Ask the sim to check if it's current
            sim.checkForUpdate();

            if( !sim.isInstalled() ) {
                showResult( "The simulation is not installed" );
            }
            else if( !sim.isCurrent() ) {
                showResult( "An update is available" );
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

                // Ask the sim to check if it's current
                sim.checkForUpdate();

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

        frame.setCursor( orgCursor );
    }

    private void showResult( String message ) {
        JOptionPane.showMessageDialog( parent,
                                       message,
                                       "Check for updates",
                                       JOptionPane.INFORMATION_MESSAGE );
    }
}
