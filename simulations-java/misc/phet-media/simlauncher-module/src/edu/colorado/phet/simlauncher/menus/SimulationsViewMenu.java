/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src/edu/colorado/phet/simlauncher/menus/SimulationsViewMenu.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.7 $
 * Date modified : $Date: 2006/06/07 00:25:54 $
 */
package edu.colorado.phet.simlauncher.menus;

import edu.colorado.phet.simlauncher.actions.SimListingOptionsAction;

import javax.swing.*;

/**
 * SimulationsViewMenu
 *
 * @author Ron LeMaster
 * @version $Revision: 1.7 $
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