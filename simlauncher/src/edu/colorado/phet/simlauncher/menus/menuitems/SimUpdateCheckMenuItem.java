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
import edu.colorado.phet.simlauncher.actions.CheckForUpdateSimAction;

/**
 * SimUpdateCheckMenuItem
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimUpdateCheckMenuItem extends PhetSiteConnectionDependentMenuItem {

    public SimUpdateCheckMenuItem( SimContainer simContainer, PhetSiteConnection phetSiteConnection ) {
        super( "Check for update", phetSiteConnection );
        addActionListener( new CheckForUpdateSimAction( simContainer, this ) );
    }
}
