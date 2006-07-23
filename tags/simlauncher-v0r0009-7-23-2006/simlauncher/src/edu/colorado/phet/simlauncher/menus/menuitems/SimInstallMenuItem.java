/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.menus.menuitems;

import edu.colorado.phet.simlauncher.PhetSiteConnection;
import edu.colorado.phet.simlauncher.SimContainer;
import edu.colorado.phet.simlauncher.actions.InstallSimAction;

/**
 * SimUpdateCheckMenuItem
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimInstallMenuItem extends PhetSiteConnectionDependentMenuItem {
    private SimContainer simContainer;

    public SimInstallMenuItem( SimContainer simContainer, PhetSiteConnection phetSiteConnection ) {
        super( "Install", phetSiteConnection );
        addActionListener( new InstallSimAction( simContainer, this ) );
    }
}
