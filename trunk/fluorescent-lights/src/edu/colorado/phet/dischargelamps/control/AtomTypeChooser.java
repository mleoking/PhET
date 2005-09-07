/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps.control;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.dischargelamps.DischargeLampModule;
import edu.colorado.phet.dischargelamps.model.*;
import edu.colorado.phet.lasers.model.PhysicsUtil;
import edu.colorado.phet.lasers.model.atom.AtomicState;
import edu.colorado.phet.lasers.model.atom.GroundState;
import edu.colorado.phet.lasers.model.photon.Photon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

/**
 * AtomTypeChooser
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class AtomTypeChooser extends JPanel {
    private GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                             GridBagConstraints.NORTHWEST,
                                                             GridBagConstraints.NONE,
                                                             new Insets( 0, 10, 0, 10 ), 0, 0 );

    public AtomTypeChooser( final DischargeLampModel model, ElementProperties[] elementProperties ) {
        super( new GridBagLayout() );

        JLabel label = new JLabel( SimStrings.get( "ControlPanel.AtomTypeButtonLabel" ) );
        gbc.anchor = GridBagConstraints.EAST;
        this.add( label, gbc );

        JComboBox comboBox = new JComboBox( elementProperties );

//        comboBox.addItem( new HydrogenProperties() );
//        comboBox.addItem( new NeonProperties() );
//        comboBox.addItem( new ConfigurableElementProperties( 2, model ) );
        comboBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                JComboBox cb = (JComboBox)e.getSource();
                // Get the selected item and tell it to do its thing
                ElementProperties selection = (ElementProperties)cb.getSelectedItem();
                model.setElementProperties( selection );
            }
        } );
        ElementProperties selection = (ElementProperties)comboBox.getSelectedItem();
        model.setElementProperties( selection );

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        this.add( comboBox, gbc );
    }
}
