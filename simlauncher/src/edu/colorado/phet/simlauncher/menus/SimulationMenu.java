/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.menus;

import edu.colorado.phet.simlauncher.menus.menuitems.*;
import edu.colorado.phet.simlauncher.model.PhetSiteConnection;
import edu.colorado.phet.simlauncher.model.SimContainer;
import edu.colorado.phet.simlauncher.model.Simulation;
import edu.colorado.phet.simlauncher.util.ChangeEventChannel;
import edu.colorado.phet.simlauncher.view.InstalledSimsPaneNew;
import edu.colorado.phet.simlauncher.view.TopLevelPane;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * SimulationMenu
 * <p/>
 * Containes menu items
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
class SimulationMenu extends JMenu implements PhetSiteConnection.ChangeListener {
    private JMenuItem launchMI;
    private JMenuItem uninstallMI;
    private JMenuItem installMI;
    private SimUpdateCheckMenuItem updateCheckMI;
    private SimUpdateMenuItem updateMI;

    /**
     * Constructor
     */
    public SimulationMenu() {
        super( "Simulation" );

        // Set up listeners that will cause the proper menu items to be enabled and disabled when
        // certain things happen in the UI
        TopLevelPane.getInstance().getInstalledSimsPane().addChangeListener( new ChangeEventChannel.ChangeListener() {
            public void stateChanged( ChangeEventChannel.ChangeEvent event ) {
                populateMenu();
            }
        } );

        // Listen for changes in the uninstalled simulations panel
        if( TopLevelPane.getInstance().getUninstalledSimsPane() != null ) {
            TopLevelPane.getInstance().getUninstalledSimsPane().addChangeListener( new ChangeEventChannel.ChangeListener() {
                public void stateChanged( ChangeEventChannel.ChangeEvent event ) {
                    populateMenu();
                }
            } );
        }

        TopLevelPane.getInstance().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                populateMenu();
            }
        } );

        TopLevelPane.getInstance().addActivePaneListener( new TopLevelPane.ActivePaneListener() {
            public void activePaneChanged( TopLevelPane.PaneChangeEvent event ) {
                populateMenu();
            }
        } );

        // Add the menu items for the current active panel
        populateMenu();
    }

    /**
     * Enables/disables menu items according to the state of the UI. If, for example, an
     * installed simulation is selected, the Launch menu item in the Simulation menu should
     * be enabled.
     */
    private void populateMenu() {
        SimContainer simContainer = TopLevelPane.getInstance().getActiveSelectedSimsContainer();
        if( simContainer != null ) {

            // Remove all the existing menu items, in case the active panel has changed
            this.removeAll();

            // Create the menu items for the active panel
            launchMI = new SimLaunchMenuItem( simContainer );
            add( launchMI );
            updateCheckMI = new SimUpdateCheckMenuItem( simContainer, PhetSiteConnection.instance() );
            add( updateCheckMI );
            updateMI = new SimUpdateMenuItem( simContainer, PhetSiteConnection.instance() );
            add( updateMI );
            uninstallMI = new SimUninstallMenuItem( simContainer );
            add( uninstallMI );

            //Menu items for uninstalled simulations
            add( new JPopupMenu.Separator() );
            installMI = new SimInstallMenuItem( simContainer, PhetSiteConnection.instance() );
            add( installMI );

            launchMI.setEnabled( false );
            uninstallMI.setEnabled( false );
            installMI.setEnabled( false );
            updateCheckMI.setEnabled( false );
            updateMI.setEnabled( false );

            Simulation[] sims = simContainer.getSimulations();

            for( int i = 0; i < sims.length; i++ ) {
                Simulation sim = sims[i];
                // If the SimContainer that signalled us is the installed sims pane, all we can do
                // is enable launching a selected sim
                if( simContainer instanceof InstalledSimsPaneNew ) {
                    launchMI.setEnabled( true );
                }
                // If the catalog pane signaled us, there are several possibilities...
                else {
                    if( sim.isInstalled() ) {
                        uninstallMI.setEnabled( true );
                        if( sim.isUpdateAvailable() ) {
                            sim.isUpdateAvailable();
                            updateMI.setEnabled( true );
                        }
                    }
                    else {
                        installMI.setEnabled( true );
                    }

                }
            }
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of PhetSiteConnection.ChangeListener
    //--------------------------------------------------------------------------------------------------

    public void connectionChanged( PhetSiteConnection.ChangeEvent event ) {
        populateMenu();
    }

}