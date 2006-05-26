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
public class UpdateSimulationAction extends AbstractAction {
    private Component component;
    private UpdateSimulationAction.Action action;

    public UpdateSimulationAction( Component component, SimulationContainer simulationContainer ) {
        this.component = component;
        action = new UpdateSimulationAction.SimulationContainerAction( simulationContainer );
    }

    public UpdateSimulationAction( Component component, Simulation simulation ) {
        this.component = component;
        action = new UpdateSimulationAction.SimulationAction( simulation );
    }

    public void actionPerformed( ActionEvent e ) {
        action.doIt();
    }

    private static interface Action {
        void doIt();
    }

    private class SimulationAction implements UpdateSimulationAction.Action {
        Simulation simulation;

        public SimulationAction( Simulation simulation ) {
            this.simulation = simulation;
        }

        public void doIt() {
            JOptionPane.showMessageDialog( component,
                                           "Checking for update for " + simulation.getName() );
        }
    }

    private class SimulationContainerAction implements UpdateSimulationAction.Action {
        SimulationContainer simulationContainer;

        public SimulationContainerAction( SimulationContainer simulationContainer ) {
            this.simulationContainer = simulationContainer;
        }

        public void doIt() {
            JOptionPane.showMessageDialog( component,
                                           "Checking for update for " + simulationContainer.getSimulation().getName() );
        }
    }
}
