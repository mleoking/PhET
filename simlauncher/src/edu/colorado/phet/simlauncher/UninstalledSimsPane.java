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

import edu.colorado.phet.simlauncher.actions.InstallSimulationAction;
import edu.colorado.phet.simlauncher.menus.UninstalledSimulationPopupMenu;
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
public class UninstalledSimsPane extends JSplitPane implements SimulationContainer,
                                                               ChangeEventChannel.ChangeEventSource {
//public class UninstalledSimsPane extends JPanel {
    private CategoryPanel categoryPanel;
    private UninstalledSimsPane.SimPanel simulationPanel;
    private ChangeEventChannel changeEventChannel = new ChangeEventChannel();

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

    /**
     *
     */
    private class CategoryPanel extends JPanel {
        private JList categoryJList;

        public CategoryPanel() {
            setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Categories" ) );
            List categories = Category.getInstances();
            Category allSims = new Category( "All simulations", Simulation.getAllInstances() );
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
    private class SimPanel extends JPanel implements Simulation.ChangeListener, SimulationContainer {
        private SimulationTable simTable;
        private JScrollPane simTableScrollPane;
        private JButton installBtn;

        public SimPanel() {
            super( new GridBagLayout() );

            setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Available Simulations" ) );

            Simulation.addListener( this );

            // Install button
            installBtn = new JButton( "Install" );
            installBtn.addActionListener( new InstallSimulationAction( this, this ) );
            installBtn.setEnabled( false );

            GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                             GridBagConstraints.CENTER,
                                                             GridBagConstraints.BOTH,
                                                             new Insets( 0, 0, 0, 0 ), 0, 0 );
            updateSimTable();
            gbc.gridy++;
            gbc.fill = GridBagConstraints.NONE;
            add( installBtn, gbc );


            Options.instance().addListener( new Options.ChangeListener() {
                public void optionsChanged( Options.ChangeEvent event ) {
                    updateSimTable();
                }
            } );

        }

        private void updateSimTable() {
            if( simTable != null ) {
                remove( simTableScrollPane );
                simTableScrollPane.remove( simTable );
            }

            // Get the simulations in the selected category
            Category category = categoryPanel.getSelectedCategory();
            List simListA = null;
            if( category == null ) {
                simListA = new ArrayList( Simulation.getAllInstances() );
            }
            else {
                simListA = new ArrayList( category.getSimulations() );
            }
            simListA.removeAll( Simulation.getInstalledSims() );

            // Create the SimulationTable
            simTable = new SimulationTable( simListA, Options.instance().isShowUninstalledThumbnails() );
            simTable.addMouseListener( new MouseAdapter() {
                public void mouseClicked( MouseEvent e ) {
                    handleSimulationSelection( e );
                }

                public void mousePressed( MouseEvent e ) {
                    handleSimulationSelection( e );
                }

                public void mouseReleased( MouseEvent e ) {
                    handleSimulationSelection( e );
                }
            } );

            simTableScrollPane = new JScrollPane( simTable );
            GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                             GridBagConstraints.CENTER,
                                                             GridBagConstraints.BOTH,
                                                             new Insets( 0, 0, 0, 0 ), 0, 0 );
            add( simTableScrollPane, gbc );
            revalidate();
        }

        //--------------------------------------------------------------------------------------------------
        // Implementation of Simulation.ChangeListener
        //--------------------------------------------------------------------------------------------------

        public void instancesChanged() {
            updateSimTable();
            changeEventChannel.notifyChangeListeners( this );
        }

        //--------------------------------------------------------------------------------------------------
        // Implementation of SimulationContainer
        //--------------------------------------------------------------------------------------------------

        public Simulation getSimulation() {
            return simTable.getSimulation();
        }


        /**
         * @param event
         */
        private void handleSimulationSelection( MouseEvent event ) {
            Simulation sim = simTable.getSelection();
            installBtn.setEnabled( sim != null );
            if( event.isPopupTrigger() && sim != null ) {
                new UninstalledSimulationPopupMenu( sim ).show( this, event.getX(), event.getY() );
            }
            changeEventChannel.notifyChangeListeners( UninstalledSimsPane.this );
        }

    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of SimulationContainer
    //--------------------------------------------------------------------------------------------------

    public Simulation getSimulation() {
        return simulationPanel.getSimulation();
    }

}
