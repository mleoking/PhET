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

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.mri.util.ControlBorderFactory;
import edu.colorado.phet.mri.view.TumorSelector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * HeadOnOffControl
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class HeadControl extends JPanel {

    public HeadControl( final HeadModule module ) {
        super( new GridBagLayout() );
        setBorder( ControlBorderFactory.createPrimaryBorder( SimStrings.get( "ControlPanel.Head" ) ) );
        TumorSelector tumorSelector = new TumorSelector( module.getHead(), module.getModel() );
        final JCheckBox showHideCB = new JCheckBox( SimStrings.get( "ControlPanel.ShowHead" ) );
        showHideCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.getHeadGraphic().setVisible( showHideCB.isSelected() );
            }
        } );
        showHideCB.setSelected( true );

        final JCheckBox showAtomsCB = new JCheckBox( SimStrings.get( "ControlPanel.ShowAtoms" ) );
        showAtomsCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setDipolesVisible( showAtomsCB.isSelected() );
            }
        } );
        showAtomsCB.setSelected( true );

        final JCheckBox showFieldCB = new JCheckBox( SimStrings.get( "ControlPanel.ShowFieldArrows" ) );
        showFieldCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setFieldArrowsVisible( showFieldCB.isSelected() );
            }
        } );
        showFieldCB.setSelected( true );

        GridBagConstraints gbc = new GridBagConstraints( 0, 0,
                                                         1, 1, 1, 1,
                                                         GridBagConstraints.WEST,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        add( showHideCB, gbc );
        gbc.gridy++;
        add( showAtomsCB, gbc );
        gbc.gridy++;
        add( showFieldCB, gbc );
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        add( tumorSelector, gbc );
    }
}
