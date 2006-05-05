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

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.util.List;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * UninstalledSimsPane
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class UninstalledSimsPane extends JPanel {

    public UninstalledSimsPane() {
        setLayout( new BorderLayout() );
        JTabbedPane tabbedPane = new JTabbedPane();
        List categories = new SimulationFactory().getCategories( "simulations.xml" );
        for( int i = 0; i < categories.size(); i++ ) {
            Category category = (Category)categories.get( i );
            tabbedPane.addTab( category.getName(), new SimPanel() );

        }

        add( tabbedPane );
    }


    private class SimPanel extends JPanel implements Simulation.ChangeListener {
        private JList simJList;

        public SimPanel() {
            super( new GridBagLayout() );

            Simulation.addListener( this );

            simJList = new JList();
            simJList.addMouseListener( new MouseAdapter() {
                public void mouseClicked( MouseEvent evt ) {
                    JList list = (JList)evt.getSource();
                    if( evt.getClickCount() == 2 ) {          // Double-click
                        installSim( (Simulation)list.getSelectedValue() );
                    }
                }
            } );

            // Populate the list
            updateSims();

            // Install button
            final JButton installBtn = new JButton( "Install" );
            installBtn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    installSim( (Simulation)simJList.getSelectedValue() );
                }
            } );
            installBtn.setEnabled( false );

            simJList.addListSelectionListener( new ListSelectionListener() {
                public void valueChanged( ListSelectionEvent e ) {
                    installBtn.setEnabled( simJList.getSelectedValue() != null );
                }
            } );

            GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                             GridBagConstraints.CENTER,
                                                             GridBagConstraints.NONE,
                                                             new Insets( 0, 0, 0, 0 ), 0, 0 );
            add( simJList, gbc );
            gbc.gridy++;
            add( installBtn, gbc );
        }

        private void updateSims() {
            List simList = Simulation.getUninstalledSims();
            Simulation[] sims = (Simulation[])simList.toArray( new Simulation[ simList.size()] );
            simJList.setListData( sims );
        }

        private void installSim( Simulation sim ) {
            sim.install();
        }

        //--------------------------------------------------------------------------------------------------
        // Implementation of Simulation.ChangeListener
        //--------------------------------------------------------------------------------------------------

        public void instancesChanged() {
            updateSims();
        }
    }
}
