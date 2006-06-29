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

import edu.colorado.phet.simlauncher.PhetSiteConnection;
import edu.colorado.phet.simlauncher.JavaSimulation;
import edu.colorado.phet.simlauncher.menus.menuitems.*;

import javax.swing.*;

/**
 * SimulationPopupMenu
 * <p/>
 * The context menu that is displayed when the user right-clicks on a simulation
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class InstalledSimPopupMenu extends JPopupMenu {

    /**
     * @param simulation
     */
    public InstalledSimPopupMenu( JavaSimulation simulation ) {
        add( new SimLaunchMenuItem( simulation ) );
        add( new SimDescriptionMenuItem( simulation ) );
        add( new SimUpdateCheckMenuItem( simulation, PhetSiteConnection.instance() ) );
        add( new SimUpdateMenuItem( simulation, PhetSiteConnection.instance() ) );
        add( new SimUninstallMenuItem( simulation ) );
    }
}