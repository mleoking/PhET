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
import edu.colorado.phet.simlauncher.actions.MostRecentlyUsedSortAction;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * SimulationsViewMenu
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
class SimulationsViewMenu extends JMenu {
    public SimulationsViewMenu() {
        super( "View" );
        add( new JMenuItem( new SimListingOptionsAction( this ) ) );
        JMenu subMenu = new JMenu( "Sort installed simulations" );
        subMenu.add( new JMenuItem( "Alphabetical" ) );
        JMenuItem mostRecentlyUsedMI = new JMenuItem( "Most recently used first" );
        subMenu.add( mostRecentlyUsedMI );
        mostRecentlyUsedMI.addActionListener( new MostRecentlyUsedSortAction() );

        JMenuItem customMI = new JMenuItem( "Custom" );
        subMenu.add( customMI );
        customMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                JOptionPane.showMessageDialog( SimulationsViewMenu.this, "User can reorder the simulations in the list by drag-and-drop, \nthen have that order preserved ");
            }
        } );
        subMenu.setVisible( true );
        add( subMenu );
    }

}
