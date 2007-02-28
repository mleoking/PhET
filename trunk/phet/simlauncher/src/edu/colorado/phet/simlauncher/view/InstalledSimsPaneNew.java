/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.view;

import edu.colorado.phet.simlauncher.model.Catalog;
import edu.colorado.phet.simlauncher.model.Category;
import edu.colorado.phet.simlauncher.model.Simulation;
import edu.colorado.phet.simlauncher.util.ChangeEventChannel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * InstalledSimsPane
 * <p/>
 * The enabling/disabling of the Launch button is not clean. I couldn't see how to do it in one place.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class InstalledSimsPaneNew extends JSplitPane implements SelectedSimsContainer,
                                                                Catalog.ChangeListener {

    private CategoryPanel categoryPanel;
    private InstalledSimsPane simulationPanel;
    private ChangeEventChannel changeEventChannel = new ChangeEventChannel();

    /**
     * Constructor
     */
    public InstalledSimsPaneNew() {
        super( JSplitPane.HORIZONTAL_SPLIT, true );

        categoryPanel = new CategoryPanel();
        simulationPanel = new InstalledSimsPane();
        simulationPanel.addChangeListener( new InstalledSimsPane.ChangeListener() {
            public void stateChanged( InstalledSimsPane.ChangeEvent event ) {
                changeEventChannel.notifyChangeListeners( simulationPanel );
            }
        } );

        setLeftComponent( categoryPanel );
        JPanel rightPanel = new JPanel();
        rightPanel.add( simulationPanel );
        setRightComponent( simulationPanel );

    }

    public void addChangeListener( ChangeEventChannel.ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeEventChannel.ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of Catalog.ChangeListener
    //--------------------------------------------------------------------------------------------------

    public void catalogChanged( Catalog.ChangeEvent event ) {
        List simList = getInstalledSimsInCurrentCategory();
        simulationPanel.updateSimTable( simList );
    }

    /**
     * Returns a list of the simulations in the currently selected category that are
     * installed
     * @return  a list of the simulations in the currently selected category that are
     * installed
     */
    private List getInstalledSimsInCurrentCategory() {
        List simList = null;
        Category category = categoryPanel.getSelectedCategory();
        if( category == null ) {
            simList = new ArrayList( Catalog.instance().getAllSimulations() );
        }
        else {
            simList = new ArrayList( category.getSimulations() );
        }
        List installedSims = Catalog.instance().getInstalledSimulations();
        for( int i = simList.size() - 1; i >= 0; i-- ) {
            Object o = simList.get( i );
            if( !installedSims.contains( o ) ) {
                simList.remove( o );
            }
        }
        return simList;
    }

    //--------------------------------------------------------------------------------------------------
    // Inner classes
    //--------------------------------------------------------------------------------------------------

    /**
     *
     */
    private class CategoryPanel extends JPanel {
        private JList categoryJList;

        public CategoryPanel() {
            setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Categories" ) );
            Category allSims = new Category( "All simulations", Catalog.instance().getAllSimulations() );
            ArrayList categories = new ArrayList();
            categories.add( allSims );
            categories.addAll( Catalog.instance().getCategories() );
            categoryJList = new JList( (Category[])( categories.toArray( new Category[ categories.size()] ) ) );
            categoryJList.setSelectionMode( DefaultListSelectionModel.SINGLE_SELECTION );
            categoryJList.setSelectedValue( allSims, true );
            add( categoryJList, BorderLayout.CENTER );

            categoryJList.addMouseListener( new MouseAdapter() {
                public void mouseClicked( MouseEvent e ) {
                    // Get the simulations in the selected category
                    List simList = getInstalledSimsInCurrentCategory();
                    simulationPanel.updateSimTable( simList );
                }
            } );
        }

        public Category getSelectedCategory() {
            Category category = (Category)categoryJList.getSelectedValue();
            return category;
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of SimulationContainer
    //--------------------------------------------------------------------------------------------------

    public Simulation getSimulation() {
        return simulationPanel.getSimulation();
    }

    public Simulation[] getSimulations() {
        return simulationPanel.getSimulations();
    }

    public Simulation[] getSelectedSimulations() {
        return simulationPanel.getSelectedSimulations();
    }
}