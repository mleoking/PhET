/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher;

import edu.colorado.phet.simlauncher.actions.InstallSimAction;
import edu.colorado.phet.simlauncher.menus.UninstalledSimPopupMenu;
import edu.colorado.phet.simlauncher.resources.SimResourceException;
import edu.colorado.phet.simlauncher.util.ChangeEventChannel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * UninstalledSimsPane
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class UninstalledSimsPane extends JSplitPane implements SimContainer {

    private CategoryPanel categoryPanel;
    private UninstalledSimsPane.SimPanel simulationPanel;
    private ChangeEventChannel changeEventChannel = new ChangeEventChannel();

    /**
     * Constructor
     */
    public UninstalledSimsPane() {
        super( JSplitPane.HORIZONTAL_SPLIT, null, null );

        categoryPanel = new CategoryPanel();
        simulationPanel = new SimPanel();

        setLeftComponent( categoryPanel );
        setRightComponent( simulationPanel );
    }

    public void addChangeListener( ChangeEventChannel.ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeEventChannel.ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
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
            List categories = Catalog.instance().getCategories();
            Category allSims = new Category( "All simulations", Catalog.instance().getAllSimulations() );
            categories.add( allSims );
            categoryJList = new JList( (Category[])( categories.toArray( new Category[ categories.size()] ) ) );
            categoryJList.setSelectedValue( allSims, true );
            add( categoryJList, BorderLayout.CENTER );

            categoryJList.addMouseListener( new MouseAdapter() {
                public void mouseClicked( MouseEvent e ) {
                    simulationPanel.updateSimTable();
                }
            } );
        }

        public Category getSelectedCategory() {
            Category category = (Category)categoryJList.getSelectedValue();
            return category;
        }
    }

    /**
     *
     */
    private class SimPanel extends JPanel implements Catalog.ChangeListener, SimContainer {
        private SimTable simTable;
        private SimTable.SimComparator simTableSortType = SimTable.NAME_SORT;
        private JScrollPane simTableScrollPane;
        private JButton installBtn;
        private GridBagConstraints tableGbc = new GridBagConstraints( 0, 1, 1, 1, 1, 1,
                                                                      GridBagConstraints.CENTER,
                                                                      GridBagConstraints.VERTICAL,
                                                                      new Insets( 0, 0, 0, 0 ), 0, 0 );
        private GridBagConstraints installButtonGbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                                              GridBagConstraints.CENTER,
                                                                              GridBagConstraints.NONE,
                                                                              new Insets( 10, 0, 20, 0 ), 0, 0 );

        /**
         * Constructor
         */
        public SimPanel() {
            super( new GridBagLayout() );

            setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Simulatons" ) );

            // Install button
            installBtn = new JButton( "Install" );
            installBtn.addActionListener( new InstallSimAction( this, this ) );
            installBtn.setEnabled( false );
            add( installBtn, installButtonGbc );

            Catalog.instance().addChangeListener( this );
            Options.instance().addListener( new Options.ChangeListener() {
                public void optionsChanged( Options.ChangeEvent event ) {
                    updateSimTable();
                }
            } );

            updateSimTable();
        }

        /**
         *
         */
        private void updateSimTable() {
            if( simTable != null ) {
                remove( simTableScrollPane );
                simTableScrollPane.remove( simTable );
            }

            // Get the simulations in the selected category
            Category category = categoryPanel.getSelectedCategory();
            List simListA = null;
            if( category == null ) {
                simListA = new ArrayList( Catalog.instance().getAllSimulations() );
            }
            else {
                simListA = new ArrayList( category.getSimulations() );
            }

            // Filter out installed sims            
//            simListA.removeAll( Catalog.instance().getInstalledSimulations() );

            // Create the SimulationTable
            simTable = new SimTable( simListA,
                                     Options.instance().isShowUninstalledThumbnails(),
                                     simTableSortType,
                                     ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
            simTable.addMouseListener( new MouseHandler() );

            simTableScrollPane = new JScrollPane( simTable );
            add( simTableScrollPane, tableGbc );

            // Disable the install button, since there is no selected simulation anymore
            installBtn.setEnabled( false );

            // This is required to get rid of screen turds if the old table had a scrollbar and the
            // new one doesn't
            repaint();
        }

        //--------------------------------------------------------------------------------------------------
        // Implementation of Catalog.ChangeListener
        //--------------------------------------------------------------------------------------------------

        public void catalogChanged( Catalog.ChangeEvent event ) {
            updateSimTable();
            changeEventChannel.notifyChangeListeners( this );
        }

        //--------------------------------------------------------------------------------------------------
        // Implementation of SimulationContainer
        //--------------------------------------------------------------------------------------------------

        public Simulation getSimulation() {
            return simTable.getSimulation();
        }

        public Simulation[] getSimulations() {
            return simTable.getSimulations();
        }

        //--------------------------------------------------------------------------------------------------
        // Handles mouse clicks on the simulation table
        //--------------------------------------------------------------------------------------------------

        /**
         * Handles enabling/disabling the intall button, double clicks for installing and right clicks
         * popping up a context menu
         */
        private class MouseHandler extends MouseAdapter {
            public void mouseClicked( MouseEvent event ) {
                // If a double left click, offer to install the simulation
                Simulation sim = simTable.getSelection();
                installBtn.setEnabled( sim != null );
                if( !event.isPopupTrigger() && event.getClickCount() == 2 ) {
                    int choice = JOptionPane.showConfirmDialog( UninstalledSimsPane.this,
                                                                "Do you want to install the simulation?",
                                                                "Confirm",
                                                                JOptionPane.OK_CANCEL_OPTION );
                    if( choice == JOptionPane.OK_OPTION ) {
                        try {
                            sim.install();
                        }
                        catch( SimResourceException e ) {
                            e.printStackTrace();
                        }
                    }
                }
                // Notify change listeners
                changeEventChannel.notifyChangeListeners( UninstalledSimsPane.this );
            }

            // Required to get e.isPopupTrigger() to return true on right-click
            public void mouseReleased( MouseEvent event ) {
                Simulation sim = simTable.getSelection();
                installBtn.setEnabled( sim != null );

                // If right click, pop up context menu
                if( event.isPopupTrigger() && sim != null ) {
                    new UninstalledSimPopupMenu( sim ).show( event.getComponent(), event.getX(), event.getY() );
                }

                // Notify change listeners
                changeEventChannel.notifyChangeListeners( UninstalledSimsPane.this );
            }
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

}