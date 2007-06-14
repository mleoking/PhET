/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src/edu/colorado/phet/simlauncher/actions/UninstallSimAction.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.6 $
 * Date modified : $Date: 2006/07/25 18:00:17 $
 */
package edu.colorado.phet.simlauncher.actions;

import edu.colorado.phet.simlauncher.model.SimContainer;
import edu.colorado.phet.simlauncher.model.Simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * UninstallSimAction
 *
 * @author Ron LeMaster
 * @version $Revision: 1.6 $
 */
public class UninstallSimAction extends AbstractAction {
    private SimContainer simContainer;
    private Component component;

    public UninstallSimAction( SimContainer simContainer, Component component ) {
        this.simContainer = simContainer;
        this.component = component;
    }

    public void actionPerformed( ActionEvent e ) {
        int choice = JOptionPane.showConfirmDialog( component, "Are you sure?", "Confirm", JOptionPane.OK_CANCEL_OPTION );
        if( choice == JOptionPane.OK_OPTION ) {
            Simulation[] simulations = simContainer.getSimulations();
            for( int i = 0; i < simulations.length; i++ ) {
                Simulation simulation = simulations[i];
                simulation.uninstall();                
            }
        }
    }
}