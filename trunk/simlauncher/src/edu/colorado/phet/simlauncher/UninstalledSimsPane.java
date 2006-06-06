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
                                                                      GridBagConstraints.NONE,
                                                                      new Insets( 0, 0, 0, 0 ), 0, 0 );
        private GridBagConstraints installButtonGbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                                             GridBagConstraints.CENTER,
                                                                             GridBagConstraints.NONE,
//                                                                             new Insets( 0, 0, 0, 0 ), 0, 0 );
                                                                             new Insets( 10, 0, 20, 0 ), 0, 0 );

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
                System.out.println( "UninstalledSimsPane$SimPanel.updateSimTable" );
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
            simListA.removeAll( Catalog.instance().getInstalledSimulations() );

            // Create the SimulationTable
            simTable = new SimTable( simListA,
                                     Options.instance().isShowUninstalledThumbnails(),
                                     simTableSortType );
            simTable.addMouseListener( new MouseAdapter() {
                public void mouseClicked( MouseEvent e ) {
                    handleSimulationSelection( e );
                }

                public void mousePressed( MouseEvent e ) {
//                    handleSimulationSelection( e );
                }

                // Required to get e.isPopupTrigger() to return true on right-click
                public void mouseReleased( MouseEvent e ) {
                    handleSimulationSelection( e );
                }
            } );

            simTableScrollPane = new JScrollPane( simTable );
            add( simTableScrollPane, tableGbc );
            revalidate();
            repaint();

            // Disable the install button, since there is no selected simulation anymore
            installBtn.setEnabled( false );
        }

        /**
         * Handles selection events on the JTable of simulations
         * @param event
         */
        private void handleSimulationSelection( MouseEvent event ) {
            Simulation sim = simTable.getSelection();
            installBtn.setEnabled( sim != null );

            // If right click, pop up context menu
            if( event.isPopupTrigger() && sim != null ) {
                new UninstalledSimPopupMenu( sim ).show( event.getComponent(), event.getX(), event.getY() );
            }

            // If a double left click, offer to install the simulation
            if( !event.isPopupTrigger() && event.getClickCount() == 2 ) {
                int choice = JOptionPane.showConfirmDialog( this, "Do you want to install the simulation?", "Confirm", JOptionPane.OK_CANCEL_OPTION );
                if( choice == JOptionPane.OK_OPTION ) {
                    sim.install();
                }
            }

            changeEventChannel.notifyChangeListeners( UninstalledSimsPane.this );
        }

        //--------------------------------------------------------------------------------------------------
        // Implementation of Catalog.ChangeListener
        //--------------------------------------------------------------------------------------------------

        public void catatlogChanged( Catalog.ChangeEvent event ) {
            updateSimTable();
            changeEventChannel.notifyChangeListeners( this );
        }

        //--------------------------------------------------------------------------------------------------
        // Implementation of SimulationContainer
        //--------------------------------------------------------------------------------------------------

        public Simulation getSimulation() {
            return simTable.getSimulation();
        }

    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of SimulationContainer
    //--------------------------------------------------------------------------------------------------

    public Simulation getSimulation() {
        return simulationPanel.getSimulation();
    }

}
