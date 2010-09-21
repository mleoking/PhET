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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.colorado.phet.mri.MriResources;

/**
 * EmRepSelector
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class EmRepSelector extends JPanel {
    public EmRepSelector( final AbstractMriModule module ) {
        super( new GridBagLayout() );

        setBorder( BorderFactory.createEtchedBorder() );
//        setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "EMW Representation" ) );

        JRadioButton photonViewButton = new JRadioButton( MriResources.getString( "ControlPanel.PhotonView" ) );
        photonViewButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setEmRep( NmrModule.PHOTON_VIEW );
            }
        } );

        JRadioButton waveViewButton = new JRadioButton( MriResources.getString( "ControlPanel.WaveView" ) );
        waveViewButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setEmRep( NmrModule.WAVE_VIEW );
            }
        } );
        ButtonGroup repBtnGrp = new ButtonGroup();
        repBtnGrp.add( photonViewButton );
        repBtnGrp.add( waveViewButton );
        waveViewButton.setSelected( true );

        GridBagConstraints gbc = new GridBagConstraints( GridBagConstraints.RELATIVE, 0,
                                                         1, 1, 1, 1,
                                                         GridBagConstraints.WEST,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        add( waveViewButton, gbc );
        add( photonViewButton, gbc );
    }
}
