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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * SimLauncherMenuBar
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimLauncherMenuBar extends JMenuBar {

    public SimLauncherMenuBar() {
        add( new FileMenu() );
        add( new SimulationMenu() );
        add( new SimulationsViewMenu() );
        add( new OptionsMenu() );
        add( new HelpMenu() );
    }
}
