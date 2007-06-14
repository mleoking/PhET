/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src/edu/colorado/phet/simlauncher/menus/menuitems/SimLaunchMenuItem.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.2 $
 * Date modified : $Date: 2006/07/25 18:00:17 $
 */
package edu.colorado.phet.simlauncher.menus.menuitems;

import edu.colorado.phet.simlauncher.actions.LaunchSimAction;
import edu.colorado.phet.simlauncher.model.SimContainer;

import javax.swing.*;

/**
 * SimLaunchMenuItem
 *
 * @author Ron LeMaster
 * @version $Revision: 1.2 $
 */
public class SimLaunchMenuItem extends JMenuItem {

    public SimLaunchMenuItem( SimContainer simContainer ) {
        super( "Launch simulation" );
        addActionListener( new LaunchSimAction( simContainer ) );
    }
}
