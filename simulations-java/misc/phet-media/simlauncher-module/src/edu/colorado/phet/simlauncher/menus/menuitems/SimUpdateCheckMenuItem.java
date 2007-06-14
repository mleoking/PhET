/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src/edu/colorado/phet/simlauncher/menus/menuitems/SimUpdateCheckMenuItem.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.5 $
 * Date modified : $Date: 2006/07/25 23:05:14 $
 */
package edu.colorado.phet.simlauncher.menus.menuitems;

import edu.colorado.phet.simlauncher.actions.CheckForSimUpdateAction;
import edu.colorado.phet.simlauncher.model.PhetSiteConnection;
import edu.colorado.phet.simlauncher.model.SimContainer;

/**
 * SimUpdateCheckMenuItem
 *
 * @author Ron LeMaster
 * @version $Revision: 1.5 $
 */
public class SimUpdateCheckMenuItem extends PhetSiteConnectionDependentMenuItem {

    public SimUpdateCheckMenuItem( SimContainer simContainer, PhetSiteConnection phetSiteConnection ) {
        super( "Check for updates", phetSiteConnection );
        addActionListener( new CheckForSimUpdateAction( simContainer, this ) );
    }
}
