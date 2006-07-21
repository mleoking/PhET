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

import edu.colorado.phet.simlauncher.SimContainer;
import edu.colorado.phet.simlauncher.Simulation;
import edu.colorado.phet.simlauncher.Catalog;
import edu.colorado.phet.simlauncher.PhetSiteConnection;

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
    public static class Result {
        private Result() {
        }
    }

    private SimContainer simContainer;
    private Component parent;

    public CheckForCatalogUpdateAction( SimContainer simContainer, Component parent ) {
        this.simContainer = simContainer;
        this.parent = parent;
    }

    public void actionPerformed( ActionEvent e ) {

        if( !PhetSiteConnection.instance().isConnected() ) {
            JOptionPane.showMessageDialog( parent, "<html>Connetction to PhET site is note available." +
                                                     "<br>Please check later</html");
        }
        else if( !Catalog.instance().isCurrent() ) {
            int choice = JOptionPane.showConfirmDialog( parent,
                                           "<html>An updated simulation catalog is available<br>" +
                                           "Would you like to download it?",
                                           "Confirm", JOptionPane.YES_NO_OPTION );
            if( choice == JOptionPane.YES_OPTION ) {
                Catalog.instance().update();
            }
        }

        Cursor orgCursor = SwingUtilities.getRoot( parent ).getCursor();
        SwingUtilities.getRoot( parent ).setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));

        SwingUtilities.getRoot( parent ).setCursor( orgCursor );
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
