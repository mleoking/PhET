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
 * UpdateSimAction
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class UpdateSimAction extends AbstractAction {
    private Component component;
    private UpdateSimAction.Action action;

    public UpdateSimAction( Component component, SimContainer simContainer ) {
        this.component = component;
        action = new UpdateSimAction.SimContainerAction( simContainer );
    }

    public UpdateSimAction( Component component, Simulation simulation ) {
        this.component = component;
        action = new UpdateSimAction.SimAction( simulation );
    }

    public void actionPerformed( ActionEvent e ) {
        action.doIt();
    }

    private static interface Action {
        void doIt();
    }

    private class SimAction implements UpdateSimAction.Action {
        Simulation simulation;

        public SimAction( Simulation simulation ) {
            this.simulation = simulation;
        }

        public void doIt() {
            JOptionPane.showMessageDialog( component,
                                           "Checking for update for " + simulation.getName() );
        }
    }

    private class SimContainerAction implements UpdateSimAction.Action {
        SimContainer simContainer;

        public SimContainerAction( SimContainer simContainer ) {
            this.simContainer = simContainer;
        }

        public void doIt() {
            JOptionPane.showMessageDialog( component,
                                           "Checking for update for " + simContainer.getSimulation().getName() );
        }
    }
}
