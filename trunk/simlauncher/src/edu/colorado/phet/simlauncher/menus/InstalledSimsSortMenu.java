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

import edu.colorado.phet.simlauncher.actions.SetInstalledSimulatonSortAction;
import edu.colorado.phet.simlauncher.SimTable;
import edu.colorado.phet.simlauncher.Options;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * InstalledSimsSortMenu
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class InstalledSimsSortMenu extends JMenu implements Options.ChangeListener{
    private JRadioButtonMenuItem alphabeticalSortMI;
    private JRadioButtonMenuItem mostRecentlyUsedMI;
    private JRadioButtonMenuItem customMI;

    /**
     * Constructor
     */
    public InstalledSimsSortMenu() {
        super( "Sort installed simulations" );

        // Listen for changes in the Options
        Options.instance().addListener( this );

        alphabeticalSortMI = new JRadioButtonMenuItem( "Alphabetical" );
        add( alphabeticalSortMI );
        alphabeticalSortMI.addActionListener( new SetInstalledSimulatonSortAction( SimTable.NAME_SORT) );

        mostRecentlyUsedMI = new JRadioButtonMenuItem( "Most recently used first" );
        add( mostRecentlyUsedMI );
        mostRecentlyUsedMI.addActionListener( new SetInstalledSimulatonSortAction( SimTable.MOST_RECENTLY_USED_SORT ) );

        customMI = new JRadioButtonMenuItem( "Custom" );
        add( customMI );
        customMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                JOptionPane.showMessageDialog( InstalledSimsSortMenu.this, "User can reorder the simulations in the list by drag-and-drop, \nthen have that order preserved " );
            }
        } );

        ButtonGroup bg = new ButtonGroup();
        bg.add( alphabeticalSortMI );
        bg.add( mostRecentlyUsedMI );
        bg.add( customMI );
        setSelectedSortOption( Options.instance().getInstalledSimulationsSortType() );
    }

    private void setSelectedSortOption( SimTable.SimComparator sortType ) {
        if( sortType.equals( SimTable.MOST_RECENTLY_USED_SORT ) ) {
            mostRecentlyUsedMI.setSelected( true );
        }
        else if( sortType.equals( SimTable.NAME_SORT ) ) {
            alphabeticalSortMI.setSelected( true );
        }
        else {
            customMI.setSelected( true );
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of Options.ChangeListener
    //--------------------------------------------------------------------------------------------------

    public void optionsChanged( Options.ChangeEvent event ) {
        setSelectedSortOption( event.getOptions().getInstalledSimulationsSortType() );
    }
}
