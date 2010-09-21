/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.controller;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import edu.colorado.phet.mri.MriResources;
import edu.colorado.phet.mri.model.MriModel;
import edu.colorado.phet.mri.model.SampleMaterial;
import edu.colorado.phet.mri.util.ControlBorderFactory;

/**
 * SampleMaterialSelector
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SampleMaterialSelector extends JPanel {

    public SampleMaterialSelector( final MriModel model ) {
        super( new GridBagLayout() );

        setBorder( ControlBorderFactory.createPrimaryBorder( MriResources.getString( "ControlPanel.SampleMaterial" ) ) );
//        JLabel label = new JLabel( "Sample material:" );
        final JComboBox selector = new JComboBox( SampleMaterial.INSTANCES );
        selector.addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
                model.setSampleMaterial( (SampleMaterial)selector.getSelectedItem() );
            }
        } );
        add( selector );

        GridBagConstraints gbc = new GridBagConstraints( GridBagConstraints.RELATIVE, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.EAST,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 10, 5, 10, 5 ), 0, 0 );
//        add( label, gbc );
        gbc.anchor = GridBagConstraints.CENTER;
        add( selector, gbc );

    }
}
