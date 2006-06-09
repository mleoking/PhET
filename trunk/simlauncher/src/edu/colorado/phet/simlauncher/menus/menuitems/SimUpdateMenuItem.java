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
import edu.colorado.phet.simlauncher.actions.UpdateSimAction;

/**
 * SimUpdateCheckMenuItem
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimUpdateMenuItem extends PhetSiteConnectionDependentMenuItem {

    public SimUpdateMenuItem( SimContainer simContainer, PhetSiteConnection phetSiteConnection ) {
        super( "Update simulation", phetSiteConnection );
        addActionListener( new UpdateSimAction( simContainer, this ) );
    }
}
