/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src/edu/colorado/phet/simlauncher/actions/InstallOrUpdateSimAction.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.7 $
 * Date modified : $Date: 2006/07/25 18:00:17 $
 */
package edu.colorado.phet.simlauncher.actions;

import edu.colorado.phet.simlauncher.SimListContainer;
import edu.colorado.phet.simlauncher.model.SimContainer;
import edu.colorado.phet.simlauncher.model.Simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * InstallOrUpdateSimAction
 * <p/>
 * Installs a simulation if it's not installed. If it is installed, updates the simulation.
 *
 * @author Ron LeMaster
 * @version $Revision: 1.7 $
 */
public class InstallOrUpdateSimAction extends AbstractAction {
    private SimContainer simContainer;
    private Component component;
    private JDialog waitDlg;

    public InstallOrUpdateSimAction( SimContainer simContainer, Component component ) {
        this.component = component;
        this.simContainer = simContainer;
    }

    public void actionPerformed( ActionEvent e ) {

        // Figure out which simulations need to be installed and which updated
        Simulation[] sims = simContainer.getSimulations();
        List simsToUpdate = new ArrayList();
        List simsToInstall = new ArrayList();
        for( int i = 0; i < sims.length; i++ ) {
            Simulation sim = sims[i];
            if( sim.isInstalled() && !sim.isCurrent() ) {
                simsToUpdate.add( sim );
            }
            else if( !sim.isInstalled() ){
                simsToInstall.add( sim );
            }
        }

        // If any sims are to be installed, do it now
        if( simsToInstall.size() > 0 ) {
            SimContainer simsToInstallContainer = new SimListContainer( simsToInstall );
            InstallSimAction installAction = new InstallSimAction( simsToInstallContainer, component );
            installAction.actionPerformed( e );
        }

        // If any sims need to be updated, do it now
        if( simsToUpdate.size() > 0 ) {
            SimContainer simsForUpdateContainer = new SimListContainer( simsToUpdate );
            AbstractAction updateAction = new UpdateSimAction( simsForUpdateContainer, component );
            updateAction.actionPerformed( e );
        }
    }
}
