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

import edu.colorado.phet.simlauncher.Simulation;
import edu.colorado.phet.simlauncher.PhetSiteConnection;
import edu.colorado.phet.simlauncher.actions.*;

import javax.swing.*;

/**
 * SimulationPopupMenu
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class InstalledSimPopupMenu extends JPopupMenu {
    private JMenuItem updateMI;
    private JMenuItem updateCheckMI;

    /**
     *
     * @param simulation
     */
    public InstalledSimPopupMenu( Simulation simulation ) {

        // Launch menu item
        JMenuItem launchMI = new JMenuItem( "Launch");
        launchMI.addActionListener( new LaunchSimAction( simulation ) );
        add( launchMI );

        // Description menu item
        JMenuItem descriptionMI = new JMenuItem( "Show description");
        descriptionMI.addActionListener( new DisplaySimDescriptionAction( simulation, this ) );
        add( descriptionMI );

        // Update check menu item
        JMenuItem updateCheckMI = new JMenuItem( "Check for update");
        updateCheckMI.addActionListener( new CheckForUpdateSimAction( simulation, this ));
        this.updateCheckMI = updateCheckMI;
        add( this.updateCheckMI );

        // Update menu item
        updateMI = new JMenuItem( "Update simulation");
        updateMI .addActionListener( new UpdateSimAction( simulation ) );
        add( updateMI );

        // Uninstall menu item
        JMenuItem uninstallMI = new JMenuItem( "Uninstall");
        uninstallMI.addActionListener( new UninstallSimAction( simulation, this ));
        add( uninstallMI);

        PhetSiteConnection.instance().addChangeListener( new PhetSiteConnection.ChangeListener() {
            public void connectionChanged( PhetSiteConnection.ChangeEvent event ) {
                enableDisableMenuItems( event.getPhetSiteConnection() );
            }
        });
        enableDisableMenuItems( PhetSiteConnection.instance() );
    }

    /**
     * Enables/disbled menu items depending on the state of the PHET site connection
     * @param phetSiteConnection
     */
    private void enableDisableMenuItems( PhetSiteConnection phetSiteConnection ) {
        boolean isConneced = phetSiteConnection.isConnected();
        updateCheckMI.setEnabled( isConneced );
        updateMI.setEnabled( isConneced );
    }
}