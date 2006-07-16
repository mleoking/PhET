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

import edu.colorado.phet.simlauncher.Options;
import edu.colorado.phet.simlauncher.actions.AutoUpdateAction;
import edu.colorado.phet.simlauncher.actions.CheckAllForUpdateAction;

import javax.swing.*;

/**
 * OptionsMenu
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
class OptionsMenu extends JMenu {
    public OptionsMenu() {
        super( "Update Options" );
        JCheckBoxMenuItem autoUpdateOption = new JCheckBoxMenuItem( "Automatically check for updates on startup" );
        add( autoUpdateOption );
        autoUpdateOption.addActionListener( new AutoUpdateAction( this ) );
        autoUpdateOption.setSelected( Options.instance().isCheckForUpdatesOnStartup() );

        JMenuItem checkAllForUpdates = new JMenuItem( "Check all installed simulations for updates");
        add(checkAllForUpdates );
        checkAllForUpdates.addActionListener( new CheckAllForUpdateAction());
    }
}
