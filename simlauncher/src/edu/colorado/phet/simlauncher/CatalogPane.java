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

import edu.colorado.phet.simlauncher.actions.InstallOrUpdateSimAction;
import edu.colorado.phet.simlauncher.actions.CheckForUpdateSimAction;
import edu.colorado.phet.simlauncher.menus.CatalogPopupMenu;
import edu.colorado.phet.simlauncher.util.ChangeEventChannel;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * CatalogPane
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CatalogPane extends JSplitPane implements SimContainer {

    private CategoryPanel categoryPanel;
    private CatalogPane.SimPanel simulationPanel;
    private ChangeEventChannel changeEventChannel = new ChangeEventChannel();

    /**
     * Constructor
     */
    public CatalogPane() {
        super( JSplitPane.HORIZONTAL_SPLIT, true );
//        super( JSplitPane.HORIZONTAL_SPLIT, null, null );

        categoryPanel = new CategoryPanel();
        simulationPanel = new SimPanel();

        setLeftComponent( categoryPanel );
        JPanel rightPanel = new JPanel();
        rightPanel.add( simulationPanel );
        setRightComponent( simulationPanel );

//        setLayout( new GridBagLayout() );
//        GridBagConstraints gbc = new  GridBagConstraints( GridBagConstraints.RELATIVE, 0, 1,1,1,1,
//                                                          GridBagConstraints.NORTH,
//                                                          GridBagConstraints.NONE,
//                                                          new Insets(0,0,0,0),0,0);
//        add( categoryPanel, gbc );
//        gbc.fill = GridBagConstraints.BOTH;
//        add( simulationPanel, gbc );

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
     * The panel that contains the SimTable
     */
    private class SimPanel extends JPanel implements Catalog.ChangeListener, SimContainer {
        private SimTable simTable;
        private SimTable.SimComparator simTableSortType = SimTable.NAME_SORT;
        private JScrollPane simTableScrollPane;
        private JButton installBtn;
        private GridBagConstraints tableGbc = new GridBagConstraints( 0, 1, 1, 1, 1, 1,
                                                                      GridBagConstraints.CENTER,
                                                                      GridBagConstraints.BOTH,
                                                                      new Insets( 0, 10, 0, 10 ), 0, 0 );
        private ArrayList columns;
        private JButton checkForUpdateBtn;

        /**
         * Constructor
         */
        public SimPanel() {
            super( new GridBagLayout() );

            setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Simulatons" ) );


            GridBagConstraints headerGbc = new GridBagConstraints( 0, 0, 1, 1, 1, 0.1,
                                                                   GridBagConstraints.CENTER,
                                                                   GridBagConstraints.HORIZONTAL,
                                                                   new Insets( 10, 0, 20, 0 ), 0, 0 );
            add( createHeaderPanel(), headerGbc );

            // Add a listener to the catalog that will update the sim table if the catalog changes
            Catalog.instance().addChangeListener( this );
            Options.instance().addListener( new Options.ChangeListener() {
                public void optionsChanged( Options.ChangeEvent event ) {
                    updateSimTable();
                }
            } );
            updateSimTable();
        }

        /**
         * Creates the panel that goes at the top of the pane
         *
         * @return a JPanel
         */
        private JPanel createHeaderPanel() {
            JPanel header = new JPanel( new GridBagLayout() );
            GridBagConstraints headerGbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                                   GridBagConstraints.CENTER,
                                                                   GridBagConstraints.NONE,
                                                                   new Insets( 0, 10, 0, 10 ), 0, 0 );

            // Select All and Clear All buttons
            // todo: make checkBoxColumn programattically determined
            int checkBoxColumn = 0;
            if( checkBoxColumn >= 0 ) {
                JButton selectAllBtn = new JButton( "Select All" );
                selectAllBtn.addActionListener( new AbstractAction() {
                    public void actionPerformed( ActionEvent e ) {
                        selectAll( true);
                    }
                } );

                JButton clearAllBtn = new JButton( "Clear All" );
                clearAllBtn.addActionListener( new AbstractAction() {
                    public void actionPerformed( ActionEvent e ) {
                        selectAll( false);
                    }
                } );

                JPanel btnPanel = new JPanel( new GridBagLayout() );
                GridBagConstraints sbpGbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                                    1, 1, 1, 1,
                                                                    GridBagConstraints.CENTER,
                                                                    GridBagConstraints.NONE,
                                                                    new Insets( 5, 0, 5, 0 ), 0, 0 );
                btnPanel.add( selectAllBtn, sbpGbc );
                btnPanel.add( clearAllBtn, sbpGbc );
                headerGbc.anchor = GridBagConstraints.WEST;
                header.add( btnPanel, headerGbc );
            }

            // Install button
            {
                installBtn = new JButton( "Install / Update" );
                installBtn.addActionListener( new InstallOrUpdateSimAction( this, this ) );
                installBtn.setEnabled( false );

                // Check for Update button
                checkForUpdateBtn = new JButton( "Check for Updates" );
                checkForUpdateBtn.addActionListener( new AbstractAction( ){
                    public void actionPerformed( ActionEvent e ) {
                        new CheckForUpdateSimAction( simTable, CatalogPane.this ).actionPerformed( e );
                    }
                });

//                checkForUpdateBtn.addActionListener( new CheckForUpdateSimAction( simTable, this ) );


                JPanel btnPanel = new JPanel( new GridBagLayout() );
                GridBagConstraints sbpGbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                                    1, 1, 1, 1,
                                                                    GridBagConstraints.CENTER,
                                                                    GridBagConstraints.NONE,
                                                                    new Insets( 5, 0, 5, 0 ), 0, 0 );
                btnPanel.add( installBtn, sbpGbc );
                btnPanel.add( checkForUpdateBtn, sbpGbc );

                headerGbc.gridx++;
                headerGbc.anchor = GridBagConstraints.CENTER;
//                header.add( installBtn, headerGbc );
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
            // The Install button should be enabled if any check boxes are checked
            installBtn.setEnabled( simTable.getSelections().length > 0 );
            Simulation[] selections = simTable.getSelections();
            boolean installedSimIsSelected = false;
            for( int i = 0; i < selections.length && !installedSimIsSelected; i++ ) {
                Simulation selection = selections[i];
                installedSimIsSelected = selection.isInstalled();
            }
            checkForUpdateBtn.setEnabled( installedSimIsSelected );
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

            // Add a mouse handler to the table
            simTable.addMouseListener( new MouseHandler() );

            //  Put the SimTable in a JPanel, then put the JPanel in the ScrollPane. This will make
            //  ScrollPane a size that is no bigger than neccesary to contain the SimTable
            JPanel jp = new JPanel( new GridBagLayout() );
            jp.add( simTable, new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                      GridBagConstraints.CENTER,
                                                      GridBagConstraints.VERTICAL,
                                                      new Insets( 0, 0, 0, 0 ), 0, 0
            ) );
//            simTableScrollPane = new JScrollPane( jp );
            simTableScrollPane = new JScrollPane( simTable );
            add( simTableScrollPane, tableGbc );

            // Disable the install button, since there is no selected simulation anymore
            installBtn.setEnabled( false );
            checkForUpdateBtn.setEnabled( false );

            // This is required to get rid of screen turds if the old table had a scrollbar and the
            // new one doesn't
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
            return simTable.getSimulations();
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
                Simulation sim = simTable.getSelection();

                // If right click, pop up context menu
                if( event.isPopupTrigger() && sim != null ) {
                    new CatalogPopupMenu( sim ).show( event.getComponent(), event.getX(), event.getY() );
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
    // Implementation of SimulationContainer
    //--------------------------------------------------------------------------------------------------

    public Simulation getSimulation() {
        return simulationPanel.getSimulation();
    }

    public Simulation[] getSimulations() {
        return simulationPanel.getSimulations();
    }

}