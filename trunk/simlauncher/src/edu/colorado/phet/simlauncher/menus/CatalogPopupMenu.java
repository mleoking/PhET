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

import javax.swing.*;

/**
 * UninstalledSimPopupMenu
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CatalogPopupMenu extends JPopupMenu {

    /**
     * @param simContainer
     */
    public CatalogPopupMenu( SimContainer simContainer ) {

        JMenuItem checkUpdateMI = new SimUpdateCheckMenuItem( simContainer, PhetSiteConnection.instance() );
        JMenuItem updateMI = new SimUpdateMenuItem( simContainer, PhetSiteConnection.instance() );
        JMenuItem descriptionMI = new SimDescriptionMenuItem( simContainer );
        JMenuItem uninstallMI = new SimUninstallMenuItem( simContainer );
        JMenuItem installMI = new SimInstallMenuItem( simContainer, PhetSiteConnection.instance() );

//        add( descriptionMI );
        add( checkUpdateMI );
        add( updateMI );
        add( uninstallMI );
        add( installMI );

        // Enable/disable menu items that are dependent on whether the simContainer is installed
        checkUpdateMI.setEnabled( false  );
        updateMI.setEnabled( false );
        uninstallMI.setEnabled( false );
        installMI.setEnabled( !false );
        Simulation[] sims = simContainer.getSimulations();
        boolean aSimIsInstalled = false;
        boolean aSimIsNotInstalled = false;
        for( int i = 0; i < sims.length; i++ ) {
            aSimIsInstalled |= sims[i].isInstalled();
            aSimIsNotInstalled |= !sims[i].isInstalled();
        }
//        checkUpdateMI.setEnabled( simContainer.isInstalled() );
//        updateMI.setEnabled( simContainer.isInstalled() );
//        uninstallMI.setEnabled( simContainer.isInstalled() );
//        installMI.setEnabled( !simContainer.isInstalled() );
        checkUpdateMI.setEnabled( aSimIsInstalled );
        updateMI.setEnabled( aSimIsInstalled );
        uninstallMI.setEnabled( aSimIsInstalled );
        installMI.setEnabled( aSimIsNotInstalled );
    }
}