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

import edu.colorado.phet.simlauncher.*;
import edu.colorado.phet.simlauncher.menus.menuitems.*;
import edu.colorado.phet.simlauncher.util.ChangeEventChannel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

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

        SimContainer simContainer = TopLevelPane.getInstance().getInstalledSimsPane();

        // Menu items for installed simulations
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

        // Set up listeners that will cause the proper menu items to be enabled and disabled when
        // certain things happen in the UI
        TopLevelPane.getInstance().getInstalledSimsPane().addChangeListener( new ChangeEventChannel.ChangeListener() {
            public void stateChanged( ChangeEventChannel.ChangeEvent event ) {
                enableMenuItems();
            }
        } );

        // Listen for changes in the uninstalled simulations panel
        if( TopLevelPane.getInstance().getUninstalledSimsPane() != null ) {
            TopLevelPane.getInstance().getUninstalledSimsPane().addChangeListener( new ChangeEventChannel.ChangeListener() {
                public void stateChanged( ChangeEventChannel.ChangeEvent event ) {
                    enableMenuItems();
                }
            } );
        }

        TopLevelPane.getInstance().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                enableMenuItems();
            }
        } );

        // Enable/disable the appropriate menu items
        enableMenuItems();
    }

    /**
     * Enables/disables menu items according to the state of the UI. If, for example, an
     * installed simulation is selected, the Launch menu item in the Simulation menu should
     * be enabled.
     */
    private void enableMenuItems() {
        InstalledSimsPane isp = TopLevelPane.getInstance().getInstalledSimsPane();
        boolean installedItemsEnabled = isp.getSimulation() != null;
        CatalogPane usp = TopLevelPane.getInstance().getUninstalledSimsPane();
        boolean uninstalledItemsEnabled = usp != null && usp.getSimulation() != null;

        JTabbedPane jtp = TopLevelPane.getInstance();
        Component component = jtp.getSelectedComponent();
        boolean installedPaneSelected = component == TopLevelPane.getInstance().getInstalledSimsPane();
        boolean uninstalledPaneSelected = component == TopLevelPane.getInstance().getUninstalledSimsPane();

        installedItemsEnabled &= installedPaneSelected;
        uninstalledItemsEnabled &= uninstalledPaneSelected;

        // Menu items for installed simulations
        launchMI.setEnabled( installedItemsEnabled );
        uninstallMI.setEnabled( installedItemsEnabled );
        updateCheckMI.setEnabled( installedItemsEnabled );
        updateMI.setEnabled( installedItemsEnabled );

        // Menu items for uninstalled simulations
        installMI.setEnabled( uninstalledItemsEnabled );
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of PhetSiteConnection.ChangeListener
    //--------------------------------------------------------------------------------------------------

    public void connectionChanged( PhetSiteConnection.ChangeEvent event ) {
        enableMenuItems();
    }
}