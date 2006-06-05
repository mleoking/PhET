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
//        JMenu subMenu = new JMenu( "Sort installed simulations" );
//        alphabeticalSortMI = new JRadioButtonMenuItem( "Alphabetical" );
//        subMenu.add( alphabeticalSortMI );
//        alphabeticalSortMI.addActionListener( new SetInstalledSimulatonSortAction( SimTable.NAME_SORT) );
//
//        mostRecentlyUsedMI = new JRadioButtonMenuItem( "Most recently used first" );
//        subMenu.add( mostRecentlyUsedMI );
//        mostRecentlyUsedMI.addActionListener( new SetInstalledSimulatonSortAction( SimTable.MOST_RECENTLY_USED_SORT ) );
//
//        customMI = new JRadioButtonMenuItem( "Custom" );
//        subMenu.add( customMI );
//        customMI.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                JOptionPane.showMessageDialog( SimulationsViewMenu.this, "User can reorder the simulations in the list by drag-and-drop, \nthen have that order preserved " );
//            }
//        } );
//
//        ButtonGroup bg = new ButtonGroup();
//        bg.add( alphabeticalSortMI );
//        bg.add( mostRecentlyUsedMI );
//        bg.add( customMI );
//        setSelectedSortOption( Options.instance().getInstalledSimulationsSortType() );

        subMenu.setVisible( true );
        add( subMenu );
    }

//    private void setSelectedSortOption( SimTable.SimComparator sortType ) {
//        if( sortType.equals( SimTable.MOST_RECENTLY_USED_SORT ) ) {
//            mostRecentlyUsedMI.setSelected( true );
//        }
//        else if( sortType.equals( SimTable.NAME_SORT ) ) {
//            alphabeticalSortMI.setSelected( true );
//        }
//        else {
//            customMI.setSelected( true );
//        }
//    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of Options.ChangeListener
    //--------------------------------------------------------------------------------------------------

//    public void optionsChanged( Options.ChangeEvent event ) {
//        setSelectedSortOption( event.getOptions().getInstalledSimulationsSortType() );
//    }
}
