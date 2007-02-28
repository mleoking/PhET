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

import edu.colorado.phet.simlauncher.actions.SimListingOptionsAction;

import javax.swing.*;

/**
 * SimulationsViewMenu
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
class SimulationsViewMenu extends JMenu {
    private JRadioButtonMenuItem alphabeticalSortMI;
    private JRadioButtonMenuItem mostRecentlyUsedMI;
    private JRadioButtonMenuItem customMI;

    public SimulationsViewMenu() {
        super( "View" );
        add( new JMenuItem( new SimListingOptionsAction( this ) ) );

        JMenu subMenu = new InstalledSimSortMenu();
        subMenu.setVisible( true );
        add( subMenu );
    }
}