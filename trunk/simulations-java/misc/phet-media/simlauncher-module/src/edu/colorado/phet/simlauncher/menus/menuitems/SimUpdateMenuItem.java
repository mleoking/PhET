/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src/edu/colorado/phet/simlauncher/menus/menuitems/SimUpdateMenuItem.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.3 $
 * Date modified : $Date: 2006/07/25 18:00:17 $
 */
package edu.colorado.phet.simlauncher.menus.menuitems;

import edu.colorado.phet.simlauncher.actions.UpdateSimAction;
import edu.colorado.phet.simlauncher.model.PhetSiteConnection;
import edu.colorado.phet.simlauncher.model.SimContainer;

/**
 * SimUpdateCheckMenuItem
 *
 * @author Ron LeMaster
 * @version $Revision: 1.3 $
 */
public class SimUpdateMenuItem extends PhetSiteConnectionDependentMenuItem {

    public SimUpdateMenuItem( SimContainer simContainer, PhetSiteConnection phetSiteConnection ) {
        super( "Update simulation", phetSiteConnection );
        addActionListener( new UpdateSimAction( simContainer, this ) );
    }
}
