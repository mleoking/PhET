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
import edu.colorado.phet.simlauncher.SimContainer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * UninstallSimAction
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class UninstallSimAction extends AbstractAction {
    private Component component;
    private Action action;


    public UninstallSimAction( Component component, SimContainer simContainer ) {
        this.component = component;
        action = new SimulationContainerAction( simContainer );
    }

    public UninstallSimAction( Component component, Simulation simulation ) {
        this.component = component;
        action = new SimulationAction( simulation );
    }

    public void actionPerformed( ActionEvent e ) {
        int choice = JOptionPane.showConfirmDialog( component, "Are you sure?", "Confirm", JOptionPane.OK_CANCEL_OPTION );
        if( choice == JOptionPane.OK_OPTION ) {
            action.doIt( component );
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Wrapped actions
    //--------------------------------------------------------------------------------------------------

    private static interface Action {
        void doIt( Component component );
    }

    private static class SimulationContainerAction implements Action {
        SimContainer simContainer;

        public SimulationContainerAction( SimContainer simContainer ) {
            this.simContainer = simContainer;
        }

        public void doIt( Component component ) {
            simContainer.getSimulation().uninstall();
        }
    }

    private static class SimulationAction implements Action {
        Simulation simulation;

        public SimulationAction( Simulation simulation ) {
            this.simulation = simulation;
        }

        public void doIt( Component component ) {
            simulation.uninstall();
        }
    }
}
