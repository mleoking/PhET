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

import edu.colorado.phet.simlauncher.actions.LaunchSimulationAction;
import edu.colorado.phet.simlauncher.actions.UpdateSimulationAction;
import edu.colorado.phet.simlauncher.actions.UninstallSimulationAction;
import edu.colorado.phet.simlauncher.actions.InstallSimulationAction;
import edu.colorado.phet.simlauncher.TopLevelPane;
import edu.colorado.phet.simlauncher.InstalledSimsPane;
import edu.colorado.phet.simlauncher.UninstalledSimsPane;
import edu.colorado.phet.simlauncher.util.ChangeEventChannel;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;

/**
 * SimulationMenu
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
class SimulationMenu extends JMenu {
    private JMenuItem launchMI;
    private JMenuItem updateMI;
    private JMenuItem uninstallMI;
    private JMenuItem installMI;

    /**
     * Constructor
     */
    public SimulationMenu() {
        super( "Simulation" );

        // Menu items for installed simulations
        launchMI = new JMenuItem( "Launch simulation" );
        launchMI.addActionListener( new LaunchSimulationAction( this, TopLevelPane.getInstance().getInstalledSimsPane() ) );
        add( launchMI );
        updateMI = new JMenuItem( "Check for updates" );
        updateMI.addActionListener( new UpdateSimulationAction( this, TopLevelPane.getInstance().getInstalledSimsPane() ) );
        add( updateMI );
        uninstallMI = new JMenuItem( "Uninstall" );
        uninstallMI.addActionListener( new UninstallSimulationAction( this, TopLevelPane.getInstance().getInstalledSimsPane() ) );
        add( uninstallMI );

        //Menu items for uninstalled simulations
        add( new JPopupMenu.Separator() );
        installMI = new JMenuItem( "Install" );
        installMI.addActionListener( new InstallSimulationAction( this,
                                                                  TopLevelPane.getInstance().getUninstalledSimsPane() ) );
        add( installMI );

        // Set up listeners that will cause the proper menu items to be enabled and disabled when
        // certain things happen in the UI
        TopLevelPane.getInstance().getInstalledSimsPane().addChangeListener( new ChangeEventChannel.ChangeListener() {
            public void stateChanged( ChangeEventChannel.ChangeEvent event ) {
                enableItems();
            }
        } );

        TopLevelPane.getInstance().getUninstalledSimsPane().addChangeListener( new ChangeEventChannel.ChangeListener() {
            public void stateChanged( ChangeEventChannel.ChangeEvent event ) {
                enableItems();
            }
        } );

        TopLevelPane.getInstance().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                enableItems();
            }
        } );

        // Enable/disable the appropriate menu items
        enableItems();
    }

    /**
     * Enables/disables menu items according to the state of the UI
     */
    private void enableItems() {
        InstalledSimsPane isp = TopLevelPane.getInstance().getInstalledSimsPane();
        boolean installedItemsEnabled = isp.getSimulation() != null;
        UninstalledSimsPane usp = TopLevelPane.getInstance().getUninstalledSimsPane();
        boolean uninstalledItemsEnabled = usp.getSimulation() != null;

        JTabbedPane jtp = TopLevelPane.getInstance();
        Component component = jtp.getSelectedComponent();
        boolean installedPaneSelected = component == TopLevelPane.getInstance().getInstalledSimsPane();
        boolean uninstalledPaneSelected = component == TopLevelPane.getInstance().getUninstalledSimsPane();

        installedItemsEnabled &= installedPaneSelected;
        uninstalledItemsEnabled &= uninstalledPaneSelected;

        // Menu items for installed simulations
        launchMI.setEnabled( installedItemsEnabled );
        updateMI.setEnabled( installedItemsEnabled );
        uninstallMI.setEnabled( installedItemsEnabled );

        // Menu items for uninstalled simulations
        installMI.setEnabled( uninstalledItemsEnabled );
    }
}
