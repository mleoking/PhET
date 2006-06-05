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

import edu.colorado.phet.simlauncher.actions.LaunchSimAction;
import edu.colorado.phet.simlauncher.actions.UpdateSimAction;
import edu.colorado.phet.simlauncher.actions.UninstallSimAction;
import edu.colorado.phet.simlauncher.actions.InstallSimAction;
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
        launchMI.addActionListener( new LaunchSimAction( this, TopLevelPane.getInstance().getInstalledSimsPane() ) );
        add( launchMI );
        updateMI = new JMenuItem( "Check for updates" );
        updateMI.addActionListener( new UpdateSimAction( this, TopLevelPane.getInstance().getInstalledSimsPane() ) );
        add( updateMI );
        uninstallMI = new JMenuItem( "Uninstall" );
        uninstallMI.addActionListener( new UninstallSimAction( this, TopLevelPane.getInstance().getInstalledSimsPane() ) );
        add( uninstallMI );

        //Menu items for uninstalled simulations
        add( new JPopupMenu.Separator() );
        installMI = new JMenuItem( "Install" );
        installMI.addActionListener( new InstallSimAction( this,
                                                           TopLevelPane.getInstance().getUninstalledSimsPane() ) );
        add( installMI );

        // Set up listeners that will cause the proper menu items to be enabled and disabled when
        // certain things happen in the UI
        TopLevelPane.getInstance().getInstalledSimsPane().addChangeListener( new ChangeEventChannel.ChangeListener() {
            public void stateChanged( ChangeEventChannel.ChangeEvent event ) {
                enableMenuItems();
            }
        } );

        TopLevelPane.getInstance().getUninstalledSimsPane().addChangeListener( new ChangeEventChannel.ChangeListener() {
            public void stateChanged( ChangeEventChannel.ChangeEvent event ) {
                enableMenuItems();
            }
        } );

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
