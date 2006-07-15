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

import edu.colorado.phet.simlauncher.actions.LaunchSimAction;
import edu.colorado.phet.simlauncher.menus.InstalledSimPopupMenu;
import edu.colorado.phet.simlauncher.util.ChangeEventChannel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * InstalledSimsPane
 * <p/>
 * The enabling/disabling of the Launch button is not clean. I couldn't see how to do it in one place.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class InstalledSimsPaneNew extends JSplitPane implements Catalog.ChangeListener, SimContainer {
//public class InstalledSimsPane extends JPanel implements Catalog.ChangeListener,
//                                                         SimContainer {


    private CategoryPanelNew categoryPanel;
    private InstalledSimsPaneNew.MySimPane simulationPanel;
    private ChangeEventChannel changeEventChannel = new ChangeEventChannel();


    public InstalledSimsPaneNew() {
        super( JSplitPane.HORIZONTAL_SPLIT, true );
//        super( JSplitPane.HORIZONTAL_SPLIT, null, null );

        simulationPanel = new InstalledSimsPaneNew.MySimPane();
        categoryPanel = new CategoryPanelNew( simulationPanel );
//    simulationPanel = new MySimPane();

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

    public Simulation getSimulation() {
        return simulationPanel.getSimulation();
    }

    public Simulation[] getSimulations() {
        return simulationPanel.getSimulations();
    }

    public void catalogChanged( Catalog.ChangeEvent event ) {
        simulationPanel.catalogChanged( event );
    }


    //--------------------------------------------------------------------------------------------------
    // Inner classes
    //--------------------------------------------------------------------------------------------------

    private class MySimPane extends AbstractSimPanel {
        private SimTable simTable;
        private SimTable.SimComparator simTableSortType = Options.instance().getInstalledSimulationsSortType();
        private JScrollPane simTableScrollPane;
        private ChangeEventChannel changeEventChannel = new ChangeEventChannel();
        private JButton launchBtn;
        private GridBagConstraints tableGbc = new GridBagConstraints( 0, 1, 2, 1, 1, 1,
                                                                      GridBagConstraints.CENTER,
                                                                      GridBagConstraints.VERTICAL,
                                                                      new Insets( 0, 0, 0, 0 ), 0, 0 );
        private GridBagConstraints headerGbc = new GridBagConstraints( 0, 0, 1, 1, 1, 0.1,
                                                                       GridBagConstraints.CENTER,
                                                                       GridBagConstraints.NONE,
                                                                       new Insets( 10, 0, 0, 0 ), 0, 0 );

        /**
         * Constructor
         */
        public MySimPane() {
            super( new GridBagLayout() );

            // Launch button
            launchBtn = new JButton( "Launch" );
            // Add an extension to the Launch action that resorts the table if the sort order is
            // most-recently-used
            launchBtn.addActionListener( new LaunchSimAction( this ) {
                public void actionPerformed( ActionEvent e ) {
                    super.actionPerformed( e );
                    if( Options.instance().getInstalledSimulationsSortType().equals( SimTable.MOST_RECENTLY_USED_SORT ) )
                    {
                        updateSimTable();
                    }
                }
            } );
            launchBtn.setEnabled( false );
            headerGbc.weightx = 1;
            add( launchBtn, headerGbc );

            // "Show Thumbnails" checkbox
            final JCheckBox showThumbnailsCB = new JCheckBox( "Show thumbnails" );
            showThumbnailsCB.addActionListener( new AbstractAction() {
                public void actionPerformed( ActionEvent e ) {
                    Options.instance().setShowInstalledThumbnails( showThumbnailsCB.isSelected() );
                }
            } );
            showThumbnailsCB.setSelected( Options.instance().isShowInstalledThumbnails() );
            headerGbc.gridx++;
            headerGbc.weightx = 0.01;
            add( showThumbnailsCB, headerGbc );

            // Creates the SimTable
            updateSimTable();

            // Listen for changes in Options
            Options.instance().addListener( new Options.ChangeListener() {
                public void optionsChanged( Options.ChangeEvent event ) {
                    if( simTableSortType != event.getOptions().getInstalledSimulationsSortType() ) {
                        simTableSortType = event.getOptions().getInstalledSimulationsSortType();
                    }
                    updateSimTable();
                }
            } );
        }

        /**
         * Creates the SimTable
         */
        protected void updateSimTable() {
            if( simTable != null ) {
                simTableScrollPane.remove( simTable );
                remove( simTableScrollPane );
            }

            ArrayList columns = new ArrayList();
            columns.add( SimTable.NAME );
            if( Options.instance().isShowInstalledThumbnails() ) {
                columns.add( SimTable.THUMBNAIL );
            }
            columns.add( SimTable.IS_UP_TO_DATE );
            simTable = new SimTable( Catalog.instance().getInstalledSimulations(),
                                     simTableSortType,
                                     ListSelectionModel.SINGLE_SELECTION,
                                     columns );

            // Add mouse handler
            simTable.addMouseListener( new InstalledSimsPaneNew.MySimPane.MouseHandler() );

            simTableScrollPane = new JScrollPane( simTable );
            add( simTableScrollPane, tableGbc );

            // Disable the lauch button. Since the table is new, there can't be any simulation selected
            launchBtn.setEnabled( false );

            // This is required to get rid of screen turds if the old table had a scrollbar and the
            // new one doesn't
            revalidate();
            repaint();
        }


        /**
         *
         */
        public void addChangeListener( ChangeEventChannel.ChangeListener listener ) {
            changeEventChannel.addListener( listener );
        }

        /**
         *
         */
        public void removeChangeListener( ChangeEventChannel.ChangeListener listener ) {
            changeEventChannel.removeListener( listener );
        }

        //--------------------------------------------------------------------------------------------------
        // Implementation of Catalog.ChangeListener
        //--------------------------------------------------------------------------------------------------

        public void catalogChanged( Catalog.ChangeEvent event ) {
            updateSimTable();
            changeEventChannel.notifyChangeListeners( InstalledSimsPaneNew.this );
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

        private class MouseHandler extends MouseAdapter {
            public void mouseClicked( MouseEvent event ) {
                Simulation sim = simTable.getSelection();
                launchBtn.setEnabled( sim != null );

                // If a double left click, launch the simulation
                if( !event.isPopupTrigger() && event.getClickCount() == 2 ) {
                    sim.launch();
                }

                // Notify change listeners
                changeEventChannel.notifyChangeListeners( InstalledSimsPaneNew.this );
            }

            // Required to get e.isPopupTrigger() to return true on right-click
            public void mouseReleased( MouseEvent event ) {
                Simulation sim = simTable.getSelection();
                launchBtn.setEnabled( sim != null );

                // If it's a right click and a simulation is selected, pop up the context menu
                if( event.isPopupTrigger() && sim != null ) {
                    new InstalledSimPopupMenu( sim ).show( event.getComponent(), event.getX(), event.getY() );
                }

                // Notify change listeners
                changeEventChannel.notifyChangeListeners( InstalledSimsPaneNew.this );
            }
        }
}
}
