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

import edu.colorado.phet.simlauncher.Catalog;
import edu.colorado.phet.simlauncher.PhetSiteConnection;
import edu.colorado.phet.simlauncher.Simulation;
import edu.colorado.phet.simlauncher.resources.SimResource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * CheckForCatalogUpdateAction
 * <p>
 * Checks to see if any of the simulations in a SimContainer have updates available
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CheckForCatalogUpdateAction extends AbstractAction {

    private Component parent;
    private boolean notifyOnlyIfUpdateAvailable;

    /**
     *
     * @param parent
     * @param notifyOnlyIfUpdateAvailable   If true, a dialog will be presented to the user only
     * if an update is available. If false, the user will be alerted if the catalog is up to date,
     * also
     */
    public CheckForCatalogUpdateAction( Component parent, boolean notifyOnlyIfUpdateAvailable ) {
//        this.parent = parent;
        this.notifyOnlyIfUpdateAvailable = notifyOnlyIfUpdateAvailable;
        this.parent = SwingUtilities.getRoot( parent );
    }

    public void actionPerformed( ActionEvent e ) {
        if( !PhetSiteConnection.instance().isConnected() && !notifyOnlyIfUpdateAvailable ) {
            JOptionPane.showMessageDialog( parent, "<html>Connetction to PhET site is not available." +
                                                     "<br>Please check later</html");
        }
        else if( !Catalog.instance().isCurrent() ) {
            int choice = JOptionPane.showConfirmDialog( parent,
                                           "<html>An updated simulation catalog is available<br>" +
                                           "Would you like to download it?",
                                           "Confirm", JOptionPane.YES_NO_OPTION );
            if( choice == JOptionPane.YES_OPTION ) {
                boolean orgFlag = SimResource.isUpdateEnabled();
//                SimResource.setUpdateEnabled( true );
//                Catalog.instance().update();
//                SimResource.setUpdateEnabled( orgFlag );
            }
        }
        else if( !notifyOnlyIfUpdateAvailable ) {
            JOptionPane.showMessageDialog( parent, "<html>The catalog is up to date.</html");
        }
    }

    private void setFlagOnSimulation( Simulation sim ) {
        // Set the flag on the simulation
        if( sim.isInstalled() && !sim.isCurrent() ) {
            sim.setUpdateAvailable( true );
        }
        else {
            sim.setUpdateAvailable( false );
        }
    }

    private void showResult( String message ) {
        JOptionPane.showMessageDialog( parent,
                                       message,
                                       "Check for updates",
                                       JOptionPane.INFORMATION_MESSAGE );
    }
}
