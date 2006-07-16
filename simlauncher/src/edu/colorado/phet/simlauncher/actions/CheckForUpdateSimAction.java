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
        Simulation sim = simContainer.getSimulation();
//        try {
            if( !sim.isInstalled() ) {
                result = SIM_NOT_INSTALLED;
                showResult( "The simulation is not installed" );
            }
            else if( !sim.isCurrent() ) {
                result = UPDATE_AVAILABLE;
                showResult( "An update is available" );
            }
            else {
                result = NO_UPDATE_AVAILABLE;
                showResult( "The installed simulation is current" );
            }
//        }
//        catch( SimResourceException e1 ) {
//            e1.printStackTrace();
//        }
    }

    private void showResult( String message ) {
        JOptionPane.showMessageDialog( parent,
                                       message,
                                       "Check for update",
                                       JOptionPane.INFORMATION_MESSAGE );
    }

    public Result getResult() {
        return result;
    }
}
