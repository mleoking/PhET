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
public class InstallSimulationAction extends AbstractAction {
    private Component component;
    private InstallSimulationAction.Action action;


    public InstallSimulationAction( Component component, SimulationContainer simulationContainer ) {
        this.component = component;
        action = new InstallSimulationAction.SimulationContainerAction( simulationContainer );
    }

    public InstallSimulationAction( Component component, Simulation simulation ) {
        this.component = component;
        action = new InstallSimulationAction.SimulationAction( simulation );
    }

    public void actionPerformed( ActionEvent e ) {
            action.doIt( component );
    }


    private static interface Action {
        void doIt( Component component );
    }

    private static class SimulationContainerAction implements InstallSimulationAction.Action {
        SimulationContainer simulationContainer;

        public SimulationContainerAction( SimulationContainer simulationContainer ) {
            this.simulationContainer = simulationContainer;
        }

        public void doIt( Component component ) {
            System.out.println( "simulationContainer = " + simulationContainer.getSimulation().getName() );            
            simulationContainer.getSimulation().install();
        }
    }

    private static class SimulationAction implements InstallSimulationAction.Action {
        Simulation simulation;

        public SimulationAction( Simulation simulation ) {
            this.simulation = simulation;
        }

        public void doIt( Component component ) {
            simulation.install();
        }
    }
}
