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
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * InstalledSimsPane
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class InstalledSimsPane extends JPanel implements Simulation.ChangeListener {
    private JList simJList;


    /**
     *
     */
    public InstalledSimsPane() {
        super( new GridBagLayout() );

        Simulation.addListener( this );

        simJList = new JList();
        simJList.addMouseListener( new MouseAdapter() {
            public void mouseClicked( MouseEvent evt ) {
                JList list = (JList)evt.getSource();
                if( evt.getClickCount() == 2 ) {          // Double-click
                    launchSim( (Simulation)list.getSelectedValue() );
                }
            }
        } );

        // Populate the list
        updateSims();

        // Launch button
        final JButton launchBtn = new JButton( "Launch" );
        launchBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                launchSim( (Simulation)simJList.getSelectedValue() );
            }
        } );
        launchBtn.setEnabled( false );

        simJList.addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent e ) {
                launchBtn.setEnabled( simJList.getSelectedValue() != null );
            }
        } );

        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        add( simJList, gbc );
        gbc.gridy++;
        add( launchBtn, gbc );
    }

    private void updateSims() {
        List simList = Simulation.getInstalledSims();
        Simulation[] sims = (Simulation[])simList.toArray( new Simulation[ simList.size()] );
        simJList.setListData( sims );
    }

    /**
     * @param sim
     */
    private void launchSim( Simulation sim ) {
        JOptionPane.showMessageDialog( SwingUtilities.getRoot( this ), sim + " will be launched" );
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of Simulation.ChangeListener
    //--------------------------------------------------------------------------------------------------

    public void instancesChanged() {
        updateSims();
    }
}
