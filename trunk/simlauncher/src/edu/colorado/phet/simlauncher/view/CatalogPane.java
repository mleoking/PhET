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

import edu.colorado.phet.simlauncher.Options;
import edu.colorado.phet.simlauncher.actions.CheckForCatalogUpdateAction;
import edu.colorado.phet.simlauncher.actions.CheckForSimUpdateAction;
import edu.colorado.phet.simlauncher.actions.InstallOrUpdateSimAction;
import edu.colorado.phet.simlauncher.menus.CatalogPopupMenu;
import edu.colorado.phet.simlauncher.model.Catalog;
import edu.colorado.phet.simlauncher.model.Category;
import edu.colorado.phet.simlauncher.model.SimContainer;
import edu.colorado.phet.simlauncher.model.Simulation;
import edu.colorado.phet.simlauncher.util.ChangeEventChannel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * CatalogPane
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CatalogPane extends JSplitPane implements SelectedSimsContainer {

    private CategoryPanel categoryPanel;
    private CatalogPane.SimPanel simulationPanel;
    private ChangeEventChannel changeEventChannel = new ChangeEventChannel();
    private Category currentCategory;

    /**
     * Constructor
     */
    public CatalogPane() {
        super( JSplitPane.HORIZONTAL_SPLIT, true );

        categoryPanel = new CategoryPanel();
        simulationPanel = new SimPanel();

        setLeftComponent( categoryPanel );
        setRightComponent( simulationPanel );

        Catalog.instance().addChangeListener( new Catalog.ChangeListener() {
            public void catalogChanged( Catalog.ChangeEvent event ) {
                remove( categoryPanel );
                categoryPanel = new CategoryPanel();
                setLeftComponent( categoryPanel );
                simulationPanel.enableDisableButtons();
            }
        } );

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
            Category allSims = new Category( "All simulations", Catalog.instance().getAllSimulations() );
            ArrayList categories = new ArrayList();
            categories.add( allSims );
            categories.addAll( Catalog.instance().getCategories() );

            // Set up the list of categories
            categoryJList = new JList( (Category[])( categories.toArray( new Category[ categories.size()] ) ) );
            categoryJList.setSelectionMode( DefaultListSelectionModel.SINGLE_SELECTION );
            add( categoryJList, BorderLayout.CENTER );
            categoryJList.addMouseListener( new MouseAdapter() {
                public void mouseClicked( MouseEvent e ) {
                    currentCategory = (Category)categoryJList.getSelectedValue();
                    simulationPanel.setCategory( currentCategory );
                    CatalogPane.this.changeEventChannel.notifyChangeListeners( this );
                }
            } );

            // Set the initially selected category
            Category selectedCategory = ( currentCategory != null ) ? currentCategory : allSims;
            categoryJList.setSelectedValue( selectedCategory, true );
        }

        public Category getSelectedCategory() {
            return currentCategory;
        }
    }

    /**
     * The panel that contains the SimTable
     */
    private class SimPanel extends JPanel implements Catalog.ChangeListener, SelectedSimsContainer {
        private HashMap categoryToSimTable = new HashMap();
        private SimTable simTable;
        private SimTable.SimComparator simTableSortType = SimTable.NAME_SORT;
        private JScrollPane simTableScrollPane;
        private JButton installBtn;
        private GridBagConstraints tableGbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                                      GridBagConstraints.NORTH,
                                                                      GridBagConstraints.BOTH,
                                                                      new Insets( 0, 10, 0, 10 ), 0, 0 );
        private ArrayList columns;
        private JButton checkForUpdateBtn;
        private JButton selectAllBtn;
        private JButton clearAllBtn;

        /**
         * Constructor
         */
        public SimPanel() {
            super( new GridBagLayout() );

            setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Simulations" ) );

            GridBagConstraints headerGbc = new GridBagConstraints( 0, 1, 1, 1, 1, 0.001,
                                                                   GridBagConstraints.NORTH,
                                                                   GridBagConstraints.HORIZONTAL,
                                                                   new Insets( 0, 10, 0, 10 ), 0, 0 );
            add( createHeaderPanel(), headerGbc );

            // Add a listener to the catalog that will update the sim table if the catalog changes
            Options.instance().addListener( new Options.ChangeListener() {
                public void optionsChanged( Options.ChangeEvent event ) {
                    updateSimTable();
                }
            } );
            updateSimTable();
        }

        /**
         * Displays a SimTable for the current category. If one doesn't exist, it is created and added
         * to the cache that maps categories to their corresponding SimTables
         *
         * @param category
         */
        private void setCategory( Category category ) {
            if( categoryToSimTable.get( category ) == null ) {
                updateSimTable();
                categoryToSimTable.put( category, simTable );
            }
            else {
                if( simTable != null ) {
                    System.out.println( "CatalogPane$SimPanel.setCategory" );
                    remove( simTableScrollPane );
                    simTableScrollPane.remove( simTable );
                }
                simTable = (SimTable)categoryToSimTable.get( category );
                simTableScrollPane = new JScrollPane( simTable );
                add( simTableScrollPane, tableGbc );

                // Enable and disable the buttons as appropriate
                enableDisableButtons();

                // This is required to get rid of screen turds
                revalidate();
            }
        }

        /**
         * Creates the panel that goes at the top of the pane
         *
         * @return a JPanel
         */
        private JPanel createHeaderPanel() {
            JPanel header = new JPanel( new GridBagLayout() );
            Border border = BorderFactory.createTitledBorder( BorderFactory.createLineBorder( Color.black ),
                                                              "Actions" );
            header.setBorder( border );
            GridBagConstraints headerGbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                                   GridBagConstraints.CENTER,
                                                                   GridBagConstraints.NONE,
                                                                   new Insets( 0, 10, 0, 10 ), 0, 0 );

            // Select All and Clear All buttons
            // todo: make checkBoxColumn programattically determined
            int checkBoxColumn = 0;
            if( checkBoxColumn >= 0 ) {
                selectAllBtn = new JButton( "Select All" );
                selectAllBtn.addActionListener( new AbstractAction() {
                    public void actionPerformed( ActionEvent e ) {
                        selectAll( true );
                    }
                } );

                clearAllBtn = new JButton( "Clear All" );
                clearAllBtn.addActionListener( new AbstractAction() {
                    public void actionPerformed( ActionEvent e ) {
                        selectAll( false );
                    }
                } );

                JPanel btnPanel = new JPanel( new GridBagLayout() );
                GridBagConstraints sbpGbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                                    1, 1, 1, 1,
                                                                    GridBagConstraints.CENTER,
                                                                    GridBagConstraints.HORIZONTAL,
                                                                    new Insets( 5, 0, 5, 0 ), 0, 0 );
                btnPanel.add( selectAllBtn, sbpGbc );
                btnPanel.add( clearAllBtn, sbpGbc );
                headerGbc.anchor = GridBagConstraints.WEST;
                header.add( btnPanel, headerGbc );
            }

            // Install button
            {
                installBtn = new JButton( "Install / Update" );
                ActionListener installBtnActionListener = new InstallOrUpdateSimAction( this,
                                                                                        this );
                installBtn.addActionListener( installBtnActionListener );
                installBtn.setEnabled( false );

                // Check for Update button
                checkForUpdateBtn = new JButton( "Check for Updates" );
                checkForUpdateBtn.addActionListener( new AbstractAction() {
                    public void actionPerformed( ActionEvent e ) {
                        new CheckForCatalogUpdateAction( CatalogPane.this, true ).actionPerformed( e );
                        new CheckForSimUpdateAction( simTable, CatalogPane.this ).actionPerformed( e );
                    }
                } );

                JPanel btnPanel = new JPanel( new GridBagLayout() );
                GridBagConstraints sbpGbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                                    1, 1, 1, 1,
                                                                    GridBagConstraints.CENTER,
                                                                    GridBagConstraints.HORIZONTAL,
                                                                    new Insets( 5, 0, 5, 0 ), 0, 0 );
                btnPanel.add( installBtn, sbpGbc );
                btnPanel.add( checkForUpdateBtn, sbpGbc );

                headerGbc.gridx++;
                headerGbc.anchor = GridBagConstraints.CENTER;
                header.add( btnPanel, headerGbc );
            }

            // "Show Thumbnails" checkbox
            final JCheckBox showThumbnailsCB = new JCheckBox( "Show thumbnails" );
            showThumbnailsCB.addActionListener( new AbstractAction() {
                public void actionPerformed( ActionEvent e ) {
                    Options.instance().setShowCatalogThumbnails( showThumbnailsCB.isSelected() );
                }
            } );
            showThumbnailsCB.setSelected( Options.instance().isShowCatalogThumbnails() );
            headerGbc.gridx++;
            headerGbc.anchor = GridBagConstraints.EAST;
            header.add( showThumbnailsCB, headerGbc );

            return header;
        }

        /**
         * Selects or de-selects all the simulations
         *
         * @param areSelected
         */
        private void selectAll( boolean areSelected ) {
            TableModel tableModel = simTable.getModel();
            for( int i = 0; i < tableModel.getRowCount(); i++ ) {
                tableModel.setValueAt( new Boolean( areSelected ), i, 0 );
            }
            enableDisableButtons();
        }

        /**
         * Enables/disables the buttons in the header depending on the state of things
         */
        private void enableDisableButtons() {
            Simulation[] selections = simTable.getSelectedSimulations();
            boolean installedSimIsSelected = false;
            boolean aSimIsInstalled = false;
            boolean aSimIsNotInstalled = false;
            boolean aSimIsNotCurrent = false;
            for( int i = 0; i < selections.length && !installedSimIsSelected; i++ ) {
                Simulation sim = selections[i];
                aSimIsInstalled |= sim.isInstalled();
                aSimIsNotInstalled |= !sim.isInstalled();
                aSimIsNotCurrent |= !sim.isCurrent();
            }
            checkForUpdateBtn.setEnabled( aSimIsInstalled );
            installBtn.setEnabled( aSimIsNotInstalled || aSimIsNotCurrent );

            // Notify listeners that things have changed
            changeEventChannel.notifyChangeListeners( CatalogPane.this );
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

            // Create the SimulationTable
            columns = new ArrayList();
            columns.add( SimTable.SELECTION_CHECKBOX );
            columns.add( SimTable.NAME );
            if( Options.instance().isShowCatalogThumbnails() ) {
                columns.add( SimTable.THUMBNAIL );
            }
            columns.add( SimTable.IS_INSTALLED );
            columns.add( SimTable.IS_UP_TO_DATE );
            simTable = new SimTable( simListA,
                                     simTableSortType,
                                     ListSelectionModel.SINGLE_SELECTION,
                                     columns );

            // Put the SimTable in the cache
            categoryToSimTable.put( category, simTable );

            // Add a mouse handler to the table
            simTable.addMouseListener( new MouseHandler() );

            simTableScrollPane = new JScrollPane( simTable );
            add( simTableScrollPane, tableGbc );

            // Enable/disable buttons
            enableDisableButtons();

            // This is required to get rid of screen turds
            revalidate();
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
            System.out.println( "CatalogPane$SimPanel.getSimulations" );
            return simTable.getSimulations();
        }

        public Simulation[] getSelectedSimulations() {
            return simTable.getSelectedSimulations();
        }

        //--------------------------------------------------------------------------------------------------
        // Handles mouse clicks on the simulation table
        //--------------------------------------------------------------------------------------------------

        /**
         * Handles enabling/disabling the intall button, checking and unchecking the check box, and right clicks
         * popping up a context menu
         */
        private class MouseHandler extends MouseAdapter {
            public void mouseClicked( MouseEvent event ) {
                // If a double left click, offer to install the simulation
//                Simulation sim = simTable.getSelection();
//                installBtn.setEnabled( sim != null );
//                if( !event.isPopupTrigger() && event.getClickCount() == 2 ) {
//                    int choice = JOptionPane.showConfirmDialog( UninstalledSimsPane.this,
//                                                                "Do you want to install the simulation?",
//                                                                "Confirm",
//                                                                JOptionPane.OK_CANCEL_OPTION );
//                    if( choice == JOptionPane.OK_OPTION ) {
//                        try {
//                            sim.install();
//                        }
//                        catch( SimResourceException e ) {
//                            e.printStackTrace();
//                        }
//                    }
//                }

                enableDisableButtons();

                // Notify change listeners
                changeEventChannel.notifyChangeListeners( CatalogPane.this );
            }

            // Required to get e.isPopupTrigger() to return true on right-click
            public void mouseReleased( MouseEvent event ) {

                // If right click, pop up context menu. Use the first line below to set up menu for all
                // selected sims. Use the second if you only want the one that the JTable thinks selected
                SimContainer simContainer = simTable;
                if( event.isPopupTrigger() && simContainer != null ) {
                    new CatalogPopupMenu( simContainer ).show( event.getComponent(), event.getX(), event.getY() );
                }

                // Notify change listeners
                changeEventChannel.notifyChangeListeners( CatalogPane.this );
            }
        }

        private class SelectionFlipper extends MouseAdapter {
            public void mouseClicked( MouseEvent e ) {
                simTable.getSelection();
                int selectedRow = simTable.getSelectedRow();
                boolean oldValue = ( (Boolean)simTable.getValueAt( selectedRow, 0 ) ).booleanValue();
                simTable.setValueAt( new Boolean( !oldValue ), selectedRow, 0 );
            }
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of SelectedSimsContainer
    //--------------------------------------------------------------------------------------------------

    public Simulation getSimulation() {
        return simulationPanel.getSimulation();
    }

    public Simulation[] getSimulations() {
        System.out.println( "CatalogPane.getSimulations" );
        return simulationPanel.getSimulations();
    }

    public Simulation[] getSelectedSimulations() {
        return simulationPanel.getSelectedSimulations();
    }
}