/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src/edu/colorado/phet/simlauncher/menus/menuitems/SimInstallMenuItem.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.2 $
 * Date modified : $Date: 2006/07/25 18:00:17 $
 */
package edu.colorado.phet.simlauncher.menus.menuitems;

import edu.colorado.phet.simlauncher.actions.InstallSimAction;
import edu.colorado.phet.simlauncher.model.PhetSiteConnection;
import edu.colorado.phet.simlauncher.model.SimContainer;

/**
 * SimUpdateCheckMenuItem
 *
 * @author Ron LeMaster
 * @version $Revision: 1.2 $
 */
public class SimInstallMenuItem extends PhetSiteConnectionDependentMenuItem {
    private SimContainer simContainer;

    public SimInstallMenuItem( SimContainer simContainer, PhetSiteConnection phetSiteConnection ) {
        super( "Install", phetSiteConnection );
        addActionListener( new InstallSimAction( simContainer, this ) );
    }
}
