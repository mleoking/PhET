/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src/edu/colorado/phet/simlauncher/menus/OptionsMenu.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.10 $
 * Date modified : $Date: 2006/07/21 19:02:30 $
 */
package edu.colorado.phet.simlauncher.menus;

import edu.colorado.phet.simlauncher.Options;
import edu.colorado.phet.simlauncher.actions.AutoUpdateAction;
import edu.colorado.phet.simlauncher.actions.CheckForCatalogUpdateAction;

import javax.swing.*;

/**
 * OptionsMenu
 *
 * @author Ron LeMaster
 * @version $Revision: 1.10 $
 */
class OptionsMenu extends JMenu {
    public OptionsMenu() {
        super( "Options" );
        JCheckBoxMenuItem autoUpdateOption = new JCheckBoxMenuItem( "Automatically check for updates on startup" );
        add( autoUpdateOption );
        autoUpdateOption.addActionListener( new AutoUpdateAction( this ) );
        autoUpdateOption.setSelected( Options.instance().isCheckForUpdatesOnStartup() );

        JMenuItem checkForCatalogUpdate = new JMenuItem( "Check for catalog update");
        add( checkForCatalogUpdate);
        checkForCatalogUpdate.addActionListener( new CheckForCatalogUpdateAction( this, false ) );
//        JMenuItem checkAllForUpdates = new JMenuItem( "Check all installed simulations for updates now");
//        add(checkAllForUpdates );
//        checkAllForUpdates.addActionListener( new CheckAllForUpdateAction( this ));
    }
}
