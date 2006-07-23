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

import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.simlauncher.actions.LaunchSimAction;
import edu.colorado.phet.simlauncher.menus.InstalledSimPopupMenu;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;

/**
 * InstalledSimsPane
 * <p/>
 * The enabling/disabling of the Launch button is not clean. I couldn't see how to do it in one place.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class InstalledSimsPane extends JPanel implements Catalog.ChangeListener,
                                                         SimContainer {

    private SimTable simTable;
    private SimTable.SimComparator simTableSortType = Options.instance().getInstalledSimulationsSortType();
    private JScrollPane simTableScrollPane;
    private EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();
    private JButton launchBtn;
    private GridBagConstraints tableGbc = new GridBagConstraints( 0, 0, 2, 1, 1, 1,
                                                                  GridBagConstraints.NORTH,
                                                                  GridBagConstraints.BOTH,
                                                                  new Insets( 0, 10, 0, 10 ), 0, 0 );
    private GridBagConstraints headerGbc = new GridBagConstraints( 0, 1, 1, 1, 1, 0.001,
                                                                   GridBagConstraints.NORTH,
                                                                   GridBagConstraints.HORIZONTAL,
                                                                   new Insets( 0, 10, 0, 10 ), 0, 0 );
    private java.util.List currentSims;

    /**
     * Constructor
     */
    public InstalledSimsPane() {
        super( new GridBagLayout() );

        setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Simulatons" ) );

        setPreferredSize( new Dimension( 800, 400) );

        // Add the button panel
        add( createHeaderPanel(), headerGbc );

        // Creates the SimTable
        updateSimTable( Catalog.instance().getInstalledSimulations() );

        // Listen for changes in Options
        Options.instance().addListener( new Options.ChangeListener() {
            public void optionsChanged( Options.ChangeEvent event ) {
                if( simTableSortType != event.getOptions().getInstalledSimulationsSortType() ) {
                    simTableSortType = event.getOptions().getInstalledSimulationsSortType();
                }
                updateSimTable( currentSims );
            }
        } );
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
        header.setBorder( border);
        GridBagConstraints headerGbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                               GridBagConstraints.CENTER,
                                                               GridBagConstraints.NONE,
                                                               new Insets( 0, 10, 0, 10 ), 0, 0 );

        // Launch button
        {
            // Launch button
            launchBtn = new JButton( "Launch" );
            // Add an extension to the Launch action that resorts the table if the sort order is
            // most-recently-used
            launchBtn.addActionListener( new LaunchSimAction( this ) {
                public void actionPerformed( ActionEvent e ) {
                    super.actionPerformed( e );
                    if( Options.instance().getInstalledSimulationsSortType().equals( SimTable.MOST_RECENTLY_USED_SORT ) ) {
                        updateSimTable( currentSims );
                    }
                }
            } );
            launchBtn.setEnabled( false );
            headerGbc.weightx = 1;
            add( launchBtn, headerGbc );

            JPanel btnPanel = new JPanel( new GridBagLayout() );
            GridBagConstraints sbpGbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                                1, 1, 1, 1,
                                                                GridBagConstraints.CENTER,
                                                                GridBagConstraints.HORIZONTAL,
                                                                new Insets( 5, 0, 5, 0 ), 0, 0 );
            btnPanel.add( launchBtn, sbpGbc );

            headerGbc.gridx++;
            headerGbc.anchor = GridBagConstraints.CENTER;
            header.add( btnPanel, headerGbc );
        }

        // "Show Thumbnails" checkbox
        final JCheckBox showThumbnailsCB = new JCheckBox( "Show thumbnails" );
        showThumbnailsCB.addActionListener( new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                Options.instance().setShowInstalledThumbnails( showThumbnailsCB.isSelected() );
            }
        } );
        showThumbnailsCB.setSelected( Options.instance().isShowInstalledThumbnails() );
        headerGbc.gridx++;
        headerGbc.anchor = GridBagConstraints.EAST;
        header.add( showThumbnailsCB, headerGbc );

        return header;
    }


    /**
     *
     */
    public void addChangeListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    /**
     *
     */
    public void removeChangeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }

    /**
     * Creates the SimTable
     */
    public void updateSimTable( java.util.List simList ) {

        currentSims = simList;

        if( simTable != null ) {
            simTableScrollPane.remove( simTable );
            remove( simTableScrollPane );
        }

        ArrayList columns = new ArrayList();
        columns.add( SimTable.NAME );
        if( Options.instance().isShowInstalledThumbnails() ) {
            columns.add( SimTable.THUMBNAIL );
        }
//        columns.add( SimTable.IS_UP_TO_DATE );
        simTable = new SimTable( simList,
                                 simTableSortType,
                                 ListSelectionModel.SINGLE_SELECTION,
                                 columns );

        // Add mouse handler
        simTable.addMouseListener( new MouseHandler() );

        //  Put the SimTable in a JPanel, then put the JPanel in the ScrollPane. This will make
        //  ScrollPane a size that is no bigger than neccesary to contain the SimTable
//        jp.add( simTable );
//        simTableScrollPane = new JScrollPane( jp );
        simTableScrollPane = new JScrollPane( simTable );
//        jp.add( simTableScrollPane );
//        add( jp, tableGbc );
        add( simTableScrollPane, tableGbc );

        // Disable the lauch button. Since the table is new, there can't be any simulation selected
        launchBtn.setEnabled( false );

        // This is required to get rid of screen turds if the old table had a scrollbar and the
        // new one doesn't
        revalidate();
        repaint();
    }

    public void updateSimTable() {
        updateSimTable( Catalog.instance().getInstalledSimulations() );
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of Catalog.ChangeListener
    //--------------------------------------------------------------------------------------------------
    public class ChangeEvent extends EventObject {
        public ChangeEvent( Object source ) {
            super( source );
        }

        public InstalledSimsPane getInstalledSimsPane() {
            return (InstalledSimsPane)getSource();
        }
    }

    public interface ChangeListener extends EventListener {
        void stateChanged( ChangeEvent event );
    }

    public void catalogChanged( Catalog.ChangeEvent event ) {
        updateSimTable( Catalog.instance().getInstalledSimulations() );
        changeListenerProxy.stateChanged( new ChangeEvent( this ));
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
            changeListenerProxy.stateChanged( new ChangeEvent( InstalledSimsPane.this ));
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
            changeListenerProxy.stateChanged( new ChangeEvent( InstalledSimsPane.this ));
        }
    }
}