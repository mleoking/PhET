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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * EmRepSelector
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class EmRepSelector extends JPanel {
    public EmRepSelector( final AbstractMriModule module ) {
        super( new GridBagLayout() );

        JRadioButton photonViewButton = new JRadioButton( "Photons" );
        photonViewButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setEmRep( MriModuleA.PHOTON_VIEW );
            }
        } );

        JRadioButton waveViewButton = new JRadioButton( "Waves" );
        waveViewButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setEmRep( MriModuleA.WAVE_VIEW );
            }
        } );
        ButtonGroup repBtnGrp = new ButtonGroup();
        repBtnGrp.add( photonViewButton );
        repBtnGrp.add( waveViewButton );
        waveViewButton.setSelected( true );

        GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                         1, 1, 1, 1,
                                                         GridBagConstraints.WEST,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        add( waveViewButton, gbc );
        add( photonViewButton, gbc );
    }
}
