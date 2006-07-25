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

import edu.colorado.phet.simlauncher.SimLauncher;
import edu.colorado.phet.simlauncher.model.PhetSiteConnection;
import edu.colorado.phet.simlauncher.model.SimContainer;
import edu.colorado.phet.simlauncher.model.Simulation;
import edu.colorado.phet.simlauncher.model.resources.SimResourceException;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * LaunchSimAction
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class LaunchSimAction extends AbstractAction {
    private SimContainer simContainer;

    public LaunchSimAction( SimContainer simContainer ) {
        this.simContainer = simContainer;
    }

    public void actionPerformed( ActionEvent e ) {
        Simulation sim = simContainer.getSimulation();
        try {
            sim.getSimulation().launch();
        }
        catch( Simulation.LaunchException le ) {
            if( PhetSiteConnection.instance().isConnected() ) {
                int choice = JOptionPane.showConfirmDialog( SimLauncher.instance().getFrame(),
                                                            "<html>A component of the simulation is either<br>" +
                                                            "missing or corrupted.<br><br>" +
                                                            "Would you like to attempt to repair it?",
                                                            "Problem launching simulation",
                                                            JOptionPane.YES_NO_OPTION );
                if( choice == JOptionPane.YES_OPTION ) {
                    sim.uninstall();
                    try {
                        sim.install();
                    }
                    catch( SimResourceException e1 ) {
                        e1.printStackTrace();
                    }
                }
                else {
                    JOptionPane.showMessageDialog( SimLauncher.instance().getFrame(),
                                                   "<html>Please remove and re-install the simulation<br><br>" +
                                                   "If this does not solve the problem, try " +
                                                   "clearing the cache (File>Clear cache)" );
                }
            }
            // If not online
            else {
                JOptionPane.showMessageDialog( SimLauncher.instance().getFrame(),
                                               "<html>A component of the simulation is either<br>" +
                                               "missing or corrupted.<br><br>" +
                                               "<html>Please connect the computer to the Internet.<br>" +
                                               "Then remove and re-install the simulation<br><br>" +
                                               "If this does not solve the problem, try clearing the cache (File>Clear cache)",
                                               "Problem launching simulation",
                                               JOptionPane.OK_OPTION );

            }
        }
    }
}