/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src/edu/colorado/phet/simlauncher/menus/SimLauncherMenuBar.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.6 $
 * Date modified : $Date: 2006/07/17 22:23:56 $
 */
package edu.colorado.phet.simlauncher.menus;

import javax.swing.*;

/**
 * SimLauncherMenuBar
 *
 * @author Ron LeMaster
 * @version $Revision: 1.6 $
 */
public class SimLauncherMenuBar extends JMenuBar {

    public SimLauncherMenuBar() {
        add( new FileMenu() );
        add( new SimulationMenu() );
//        add( new SimulationsViewMenu() );
        add( new OptionsMenu() );
        add( new HelpMenu() );
    }
}
