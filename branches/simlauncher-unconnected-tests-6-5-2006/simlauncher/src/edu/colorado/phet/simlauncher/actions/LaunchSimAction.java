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
 * LaunchSimAction
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class LaunchSimAction extends AbstractAction {
    private Component component;
    private Action action;

    public LaunchSimAction( Component component, SimContainer simContainer ) {
        this.component = component;
        action = new SimulationContainerAction( simContainer );
    }

    public LaunchSimAction( Component component, Simulation simulation ) {
        this.component = component;
        action = new SimulationAction( simulation );
    }

    public void actionPerformed( ActionEvent e ) {
        action.doIt();
    }

    private static interface Action {
        void doIt();
    }

    private class SimulationAction implements Action {
        Simulation simulation;

        public SimulationAction( Simulation simulation ) {
            this.simulation = simulation;
        }

        public void doIt() {
            simulation.launch();
        }
    }

    private class SimulationContainerAction implements Action {
        SimContainer simContainer;

        public SimulationContainerAction( SimContainer simContainer ) {
            this.simContainer = simContainer;
        }

        public void doIt() {
            simContainer.getSimulation().launch();
        }
    }
}
