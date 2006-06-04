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
import edu.colorado.phet.simlauncher.SimulationContainer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * LaunchSimulationAction
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class LaunchSimulationAction extends AbstractAction {
    private Component component;
    private Action action;

    public LaunchSimulationAction( Component component, SimulationContainer simulationContainer ) {
        this.component = component;
        action = new SimulationContainerAction( simulationContainer );
    }

    public LaunchSimulationAction( Component component, Simulation simulation ) {
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
        SimulationContainer simulationContainer;

        public SimulationContainerAction( SimulationContainer simulationContainer ) {
            this.simulationContainer = simulationContainer;
        }

        public void doIt() {
            simulationContainer.getSimulation().launch();
        }
    }
}
